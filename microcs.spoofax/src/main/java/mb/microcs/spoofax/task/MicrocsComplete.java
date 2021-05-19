package mb.microcs.spoofax.task;

import mb.common.editing.TextEdit;
import mb.common.region.Region;
import mb.common.style.StyleName;
import mb.common.util.ListView;
import mb.completions.common.CompletionItem;
import mb.completions.common.CompletionResult;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.Terms;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.terms.stratego.TermOrigin;
import mb.nabl2.terms.stratego.TermPlaceholder;
import mb.pie.api.ExecContext;
import mb.pie.api.ExecException;
import mb.pie.api.Function;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.statix.common.PlaceholderVarMap;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.completions.TermCompleter;
import mb.microcs.spoofax.MicrocsScope;
import mb.strategies.DebugEventHandler;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.representer.Represent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MicrocsScope
public class MicrocsComplete implements TaskDef<MicrocsComplete.Input, @Nullable CompletionResult> {

    public static class Input implements Serializable {
        public final ResourceKey resourceKey;
        public final int caretLocation;
        public final Supplier<@Nullable IStrategoTerm> astSupplier;
        public final Function<IStrategoTerm, @Nullable String> prettyPrinterFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> preAnalyzeFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> postAnalyzeFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> isInjFunction;     // Should be a predicate
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> upgradePlaceholdersFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> downgradePlaceholdersFunction;

        public Input(Supplier<IStrategoTerm> supplier) {
            // This constructor is only here to satisfy the compiler
            // because of the generated createCompletionTask() method
            // in GeneratedMicrocsInstance
            throw new UnsupportedOperationException();
        }

        public Input(
            ResourceKey resourceKey,
            int caretLocation,
            Supplier<IStrategoTerm> astSupplier,
            Function<IStrategoTerm, @Nullable String> prettyPrinterFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> preAnalyzeFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> postAnalyzeFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> isInjFunction,     // Should be a predicate
            Function<IStrategoTerm, @Nullable IStrategoTerm> upgradePlaceholdersFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> downgradePlaceholdersFunction
        ) {
            this.resourceKey = resourceKey;
            this.caretLocation = caretLocation;
            this.astSupplier = astSupplier;
            this.prettyPrinterFunction = prettyPrinterFunction;
            this.preAnalyzeFunction = preAnalyzeFunction;
            this.postAnalyzeFunction = postAnalyzeFunction;
            this.isInjFunction = isInjFunction;
            this.upgradePlaceholdersFunction = upgradePlaceholdersFunction;
            this.downgradePlaceholdersFunction = downgradePlaceholdersFunction;
        }
    }

    private final Logger log;
    private final TermCompleter completer = new TermCompleter();
    private final StrategoTerms strategoTerms;
    private final ITermFactory termFactory;
    private final Provider<StatixAnalyzer> analyzerProvider;

    @Inject public MicrocsComplete(
        LoggerFactory loggerFactory,
        StrategoTerms strategoTerms,
        ITermFactory termFactory,
        Provider<StatixAnalyzer> analyzerProvider
    ) {
        this.log = loggerFactory.create(MicrocsComplete.class);
        this.strategoTerms = strategoTerms;
        this.termFactory = termFactory;
        this.analyzerProvider = analyzerProvider;
    }

    @Override
    public String getId() {
        return this.getClass().getName();
    }

    @Override
    public @Nullable CompletionResult exec(ExecContext context, Input input) throws Exception {
        StatixAnalyzer analyzer = analyzerProvider.get();

        // 1) Get the file in which code completion is invoked & parse the file with syntactic completions enabled,
        //    resulting in an AST with placeholders
        //    ==> This should be done by specifying the correct astProvider
        // TODO: get the ast in 'completion mode', with placeholders (use placeholder recovery or inference)
        @Nullable IStrategoTerm ast = input.astSupplier.get(context);
        if (ast == null){
            log.error("Completion failed: we didn't get an AST.");
            return null;   // Cannot complete when we don't get an AST.
        }

        @Nullable IStrategoTerm explicatedAst = explicate(context, input, ast);
        if (explicatedAst == null) {
            log.error("Completion failed: we did not get an explicated AST.");
            return null;    // Cannot complete when we don't get an explicated AST.
        }

        // Convert to Statix AST
        IStrategoTerm annotatedAst = StrategoTermIndices.index(explicatedAst, input.resourceKey.toString(), termFactory);
        ITerm tmpStatixAst = strategoTerms.fromStratego(annotatedAst);
        PlaceholderVarMap placeholderVarMap = new PlaceholderVarMap(input.resourceKey.toString());
        // FIXME: Ideally we would know the sort of the placeholder
        //  so we can use that sort to call the correct downgrade-placeholders-Lang-Sort strategy
        // FIXME: We can generate: downgrade-placeholders-Lang(|sort) = where(<?"Exp"> sort); downgrade-placeholders-Lang-Exp
        ITerm statixAst = StrategoPlaceholders.replacePlaceholdersByVariables(tmpStatixAst, placeholderVarMap);
        @Nullable ITermVar placeholderVar = findPlaceholderAt(statixAst, input.caretLocation);
        if (placeholderVar == null) {
            log.error("Completion failed: we don't know the placeholder.");
            return null;   // Cannot complete when we don't know the placeholder.
        }

        // 4) Get the solver state of the program (whole project),
        //    which should have some remaining constraints on the placeholder.
        //    TODO: What to do when the file is semantically incorrect? Recovery?

        List<IStrategoTerm> completionTerms;
        // FIXME: Remove this!
//        final Path debugPath = Paths.get("/Users/daniel/repos/spoofax3/devenv-cc/debug.yml");
//        System.out.println("DEBUG path: " + debugPath.toAbsolutePath());
        try(final StrategyEventHandler eventHandler = StrategyEventHandler.none()) {
//        try(final StrategyEventHandler eventHandler = new EventHandler(debugPath)) {
            SolverContext ctx = analyzer.createContext(eventHandler, null);//placeholderVar);
            // TODO: Specify spec name and root rule name somewhere
            SolverState startState = analyzer.createStartState(statixAst, "statics", "programOk")
                .withExistentials(placeholderVarMap.getVars())
                .precomputeCriticalEdges(ctx.getSpec());
            SolverState initialState = analyzer.analyze(ctx, startState);
            if(initialState.hasErrors()) {
                log.error("Completion failed: input program validation failed.\n" + initialState.toString());
                return null;    // Cannot complete when analysis fails.
            }
            if(initialState.getConstraints().isEmpty()) {
                log.error("Completion failed: no constraints left, nothing to complete.\n" + initialState.toString());
                return null;    // Cannot complete when there are no constraints left.
            }

            // 5) Invoke the completer on the solver state, indicating the placeholder for which we want completions
            // 6) Get the possible completions back, as a list of ASTs with new solver states
            completionTerms = complete(context, input, ctx, initialState, placeholderVar, placeholderVarMap);
        }

        // 7) Format each completion as a proposal, with pretty-printed text
        List<String> completionStrings = completionTerms.stream().map(proposal -> {
            try {
                // TODO: We should call the correct downgrade-placeholders-Lang-Sort based on the
                //  sort of the placeholder.
                @Nullable IStrategoTerm downgradedTerm = downgrade(context, input, proposal);
                if (downgradedTerm == null) {
                    log.warn("Downgrading failed on proposal: " + proposal);
                    return proposal.toString();  // Return the term when downgrading failed
                }
                @Nullable IStrategoTerm implicatedTerm = implicate(context, input, downgradedTerm);
                if (implicatedTerm == null) {
                    log.warn("Implication failed on downgraded: " + downgradedTerm + "\nFrom proposal: " + proposal);
                    return downgradedTerm.toString();  // Return the term when implication failed
                }
                @Nullable String prettyPrinted = prettyPrint(context, implicatedTerm, input.prettyPrinterFunction);
                if (prettyPrinted == null) {
                    log.warn("Pretty-printing failed on implicated: " + implicatedTerm + "\nFrom downgraded: " + downgradedTerm + "\nFrom proposal: " + proposal);
                    return implicatedTerm.toString();  // Return the term when pretty-printing failed
                }
                return prettyPrinted;
            } catch(ExecException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        // 8) Insert the selected completion: insert the pretty-printed text in the code,
        //    and (maybe?) add the solver state to the current solver state
        List<CompletionItem> completionItems = completionStrings.stream().map(s -> createCompletionProposal(s, input.caretLocation)).collect(Collectors.toList());

        if (completionItems.isEmpty()) {
            log.warn("Completion returned no completion proposals.");
        }

        return new CompletionResult(ListView.copyOf(completionItems), Objects.requireNonNull(getRegion(placeholderVar)), true);
    }

    private static class EventHandler extends DebugEventHandler {

        public EventHandler(Path path) throws IOException {
            super(path, new DebugRepresenter());
        }

        public EventHandler(OutputStream outputStream) {
            super(outputStream, new DebugRepresenter());
        }

        public EventHandler(Writer writer) {
            super(writer, new DebugRepresenter());
        }

        @Override
        protected Object represent(Object ctx, Object obj) {
            if (obj instanceof SolverState) return represent(ctx, (SolverState)obj);
            if (obj instanceof SelectedConstraintSolverState) return represent(ctx, (SelectedConstraintSolverState)obj);
            return super.represent(ctx, obj);
        }

        private Map<String, Object> represent(Object ctx, SolverState ss) {
            @Nullable ITerm focusValue = null;
            if (ctx instanceof SolverContext) {
                final SolverContext sc = (SolverContext)ctx;
                final @Nullable ITermVar focusVar = sc.getFocusVar();
                if(focusVar != null) focusValue = ss.project(focusVar);
            }
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("focus", focusValue != null ? focusValue.toString() : "<no focus>");
            map.put("state", ss);
            return map;
        }

        private Map<String, Object> represent(Object ctx, SelectedConstraintSolverState ss) {
            @Nullable ITerm focusValue = null;
            if (ctx instanceof SolverContext) {
                final SolverContext sc = (SolverContext)ctx;
                final @Nullable ITermVar focusVar = sc.getFocusVar();
                if(focusVar != null) focusValue = ss.getInnerState().project(focusVar);
            }
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("focus", focusValue != null ? focusValue.toString() : "<no focus>");
            map.put("selected", ss.getSelected());
            map.put("state", ss);
            return map;
        }
    }
//    private static class DebugRepresenter extends DebugEventHandler.DebugRepresenter {
//        public DebugRepresenter() {
//            setRepr(SolverState.class, new SolverStateRepresent());
//        }
//
//        public class SolverStateRepresent implements Represent {
//            @Override
//            public Node representData(Object data) {
//                SolverState state = (SolverState)data;
//                // TODO: Project var
//                String value = state.toString();
//                return representScalar(Tag.STR, value, DumperOptions.ScalarStyle.LITERAL);
//            }
//        }
//    }

    /**
     * Creates a completion proposal.
     *
     * @param text the text to insert
     * @param caretOffset the caret location
     * @return the created proposal
     */
    private CompletionItem createCompletionProposal(String text, int caretOffset) {
        ListView<TextEdit> textEdits = ListView.of(new TextEdit(Region.atOffset(caretOffset), text));
        String label = normalizeText(text);
        StyleName style = Objects.requireNonNull(StyleName.fromString("meta.template"));
        return new CompletionItem(label, "", "", "", "", style, textEdits, false);
    }

    private String normalizeText(String text) {
        // Replace all sequences of layout with a single space
        return text.replaceAll("\\s+", " ");
    }

    /**
     * Returns the pretty-printed version of the specified term.
     *
     * @param context the execution context
     * @param term the term to pretty-print
     * @param prettyPrinterFunction the pretty-printer function
     * @return the pretty-printed term; or {@code null} when it failed
     */
    private @Nullable String prettyPrint(ExecContext context, IStrategoTerm term, Function<IStrategoTerm, String> prettyPrinterFunction) throws ExecException, InterruptedException {
        return prettyPrinterFunction.apply(context, term);
    }

    private List<IStrategoTerm> complete(ExecContext execContext, Input input, SolverContext ctx, SolverState state, ITermVar placeholderVar, PlaceholderVarMap placeholderVarMap) throws InterruptedException {
        final Predicate<ITerm> isInjPredicate = t -> {
            final IStrategoTerm st = ctx.getStrategoTerms().toStratego(t, true);
            final @Nullable IStrategoTerm result = input.isInjFunction.apply(execContext, st);
            return result != null;
        };
        final List<TermCompleter.CompletionSolverProposal> proposalTerms = completer.complete(ctx, isInjPredicate, state, placeholderVar);
        return orderProposals(proposalTerms.stream()
            .filter(p -> //!StrategoPlaceholders.containsLiteralVar(p.getTerm())      // Don't show proposals that require a literal to be filled out, such as an ID, string literal, int literal
                         !StrategoPlaceholders.isPlaceholder(p.getTerm())           // Don't show proposals of naked placeholder constructors (e.g., Exp-Plhdr())
                      && !(p.getTerm() instanceof ITermVar))                        // Don't show proposals of naked term variables (e.g., $Exp0, which would become a Stratego placeholder eventually)
            )
            .map(p -> strategoTerms.toStratego(p.getTerm(), true))
            .collect(Collectors.toList());
    }

    // TODO: This should be part of the completion strategy, so we can check that our ordering puts the most likely candidates first
    //  when doing the completeness tests.
    /**
     * Orders the proposals.
     *
     * We want to order deeper terms before shallower ones,
     * and terms with less placeholders before terms with more placeholders.
     *
     * @param proposals the proposals to order
     * @return the ordered proposals
     */
    private Stream<TermCompleter.CompletionSolverProposal> orderProposals(Stream<TermCompleter.CompletionSolverProposal> proposals) {
        return proposals.sorted(Comparator
            // Sort expanded queries after expanded rules before leftovers
            .<TermCompleter.CompletionSolverProposal, Integer>comparing(it -> it.getNewState().getMeta().getExpandedQueries() > 0 ? 2 : (it.getNewState().getMeta().getExpandedRules() > 0 ? 1 : 0))
            // Sort more expanded queries/rules after less expanded queries/rules
            .<Integer>thenComparing(it -> it.getNewState().getMeta().getExpandedQueries() + it.getNewState().getMeta().getExpandedRules())
            // Sort solutions with higher rank after solutions with lower rank
            .<Integer>thenComparing(it -> rankTerm(it.getTerm()))
        );
    }

    /**
     * Ranks the term. A higher value means a better result.
     *
     * Terms are ranked by how many concrete (non-placeholder) terms they have.
     * The deeper the term, the higher the rank.
     *
     * @param root the term to rank
     * @return the rank
     */
    private int rankTerm(ITerm root) {
        int sum = 0;
        int level = -1;
        Deque<ITerm> currWorklist = new ArrayDeque<>();
        Deque<ITerm> nextWorklist = new ArrayDeque<>();
        nextWorklist.add(root);
        while (!nextWorklist.isEmpty()) {
            Deque<ITerm> tmp = currWorklist;
            currWorklist = nextWorklist;
            nextWorklist = tmp;
            level += 1;
            while(!currWorklist.isEmpty()) {
                final ITerm term = currWorklist.remove();
                final int addition =  (term instanceof ITermVar ? 0 : 1 << level);
                if (Integer.MAX_VALUE - addition < sum) return Integer.MAX_VALUE;
                sum += addition;
                if(term instanceof IApplTerm) {
                    nextWorklist.addAll(((IApplTerm)term).getArgs());
                }
            }
        }
        return sum;
    }

    /**
     * Finds the placeholder near the caret location in the specified term.
     *
     * This method assumes all terms in the term are uniquely identifiable,
     * for example through a term index or unique tree path.
     *
     * @param term the term (an AST with placeholders)
     * @param caretOffset the caret location
     * @return the placeholder; or {@code null} if not found
     */
    private @Nullable ITermVar findPlaceholderAt(ITerm term, int caretOffset) {
        if (!termContainsCaret(term, caretOffset)) return null;
        // Recurse into the term
        return term.match(Terms.cases(
            (appl) -> appl.getArgs().stream().map(a -> findPlaceholderAt(a, caretOffset)).filter(Objects::nonNull).findFirst().orElse(null),
            (list) -> list.match(ListTerms.cases(
                (cons) -> {
                    @Nullable final ITermVar headMatch = findPlaceholderAt(cons.getHead(), caretOffset);
                    if (headMatch != null) return headMatch;
                    return findPlaceholderAt(cons.getTail(), caretOffset);
                },
                (nil) -> null,
                (var) -> null
            )),
            (string) -> null,
            (integer) -> null,
            (blob) -> null,
            (var) -> isPlaceholder(var) ? var : null
        ));
    }

    private boolean isPlaceholder(ITermVar var) {
        return TermPlaceholder.has(var);
    }

    /**
     * Determines whether the specified term contains the specified caret offset.
     *
     * @param term the term
     * @param caretOffset the caret offset to find
     * @return {@code true} when the term contains the caret offset;
     * otherwise, {@code false}.
     */
    private boolean termContainsCaret(ITerm term, int caretOffset) {
        @Nullable Region region = getRegion(term);
        if (region == null) {
            // One of the children must contain the caret
            return term.match(Terms.cases(
                (appl) -> appl.getArgs().stream().anyMatch(a -> termContainsCaret(a, caretOffset)),
                (list) -> list.match(ListTerms.cases(
                    (cons) -> {
                        final boolean headContains = termContainsCaret(cons.getHead(), caretOffset);
                        if (headContains) return true;
                        return termContainsCaret(cons.getTail(), caretOffset);
                    },
                    (nil) -> false,
                    (var) -> false
                )),
                (string) -> false,
                (integer) -> false,
                (blob) -> false,
                (var) -> false
            ));
        }
        return region.contains(caretOffset);
    }

    /**
     * Gets the region occupied by the specified term.
     *
     * @param term the term
     * @return the term's region; or {@code null} when it could not be determined
     */
    private static @Nullable Region getRegion(ITerm term) {
        @Nullable final TermOrigin origin = TermOrigin.get(term).orElse(null);
        if (origin == null) return null;
        final ImploderAttachment imploderAttachment = origin.getImploderAttachment();
        // We get the zero-based offset of the first character in the token
        int startOffset = imploderAttachment.getLeftToken().getStartOffset();
        // We get the zero-based offset of the character following the token, which is why we have to add 1
        int endOffset = imploderAttachment.getRightToken().getEndOffset() + 1;
        // If the token is empty or malformed, we skip it. (An empty token cannot contain a caret anyway.)
        if (endOffset <= startOffset) return null;

        return Region.fromOffsets(
            startOffset,
            endOffset
        );
    }

    private @Nullable IStrategoTerm explicate(ExecContext context, Input input, IStrategoTerm term) {
        return input.preAnalyzeFunction.apply(context, term);
    }

    private @Nullable IStrategoTerm implicate(ExecContext context, Input input, IStrategoTerm term) {
        return input.postAnalyzeFunction.apply(context, term);
    }

    private @Nullable IStrategoTerm upgrade(ExecContext context, Input input, IStrategoTerm term) {
        return input.upgradePlaceholdersFunction.apply(context, term);
    }

    private @Nullable IStrategoTerm downgrade(ExecContext context, Input input, IStrategoTerm term) {
        return input.downgradePlaceholdersFunction.apply(context, term);
    }
}
