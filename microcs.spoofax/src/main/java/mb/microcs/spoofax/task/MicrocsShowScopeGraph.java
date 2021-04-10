package mb.microcs.spoofax.task;

import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoUtil;
import mb.microcs.spoofax.MicrocsScope;
import mb.microcs.spoofax.task.MicrocsAnalyze;
import mb.microcs.spoofax.task.MicrocsParse;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Inject;
import javax.inject.Provider;

@MicrocsScope
public class MicrocsShowScopeGraph implements TaskDef<MicrocsShowArgs, CommandFeedback> {
    private final MicrocsParse parse;
    private final MicrocsAnalyze analyze;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject public MicrocsShowScopeGraph(
        MicrocsParse parse,
        MicrocsAnalyze analyze,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        this.parse = parse;
        this.analyze = analyze;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MicrocsShowArgs input) {
        final ResourceKey key = input.key;
        return context.require(analyze, new MicrocsAnalyze.Input(key, parse.createAstSupplier(key)))
            .mapCatching(output -> {
                final StrategoRuntime strategoRuntime = strategoRuntimeProvider.get().addContextObject(output.context);
                final ITermFactory termFactory = strategoRuntime.getTermFactory();
                final IStrategoTerm inputTerm = termFactory.makeTuple(output.result.ast, termFactory.makeString(key.asString()));
                return StrategoUtil.toString(strategoRuntime.invoke("spoofax3-editor-show-analysis-term", inputTerm));
            })
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Scope graph for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MicrocsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
