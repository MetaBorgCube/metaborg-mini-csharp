package mb.minics.spoofax.task;

import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoUtil;
import mb.minics.spoofax.MinicsScope;
import mb.minics.spoofax.task.MinicsAnalyze;
import mb.minics.spoofax.task.MinicsParse;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Inject;
import javax.inject.Provider;

@MinicsScope
public class MinicsShowScopeGraph implements TaskDef<MinicsShowArgs, CommandFeedback> {
    private final MinicsParse parse;
    private final MinicsAnalyze analyze;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject public MinicsShowScopeGraph(
        MinicsParse parse,
        MinicsAnalyze analyze,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        this.parse = parse;
        this.analyze = analyze;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MinicsShowArgs input) {
        final ResourceKey key = input.key;
        return context.require(analyze, new MinicsAnalyze.Input(key, parse.createAstSupplier(key)))
            .mapCatching(output -> {
                final StrategoRuntime strategoRuntime = strategoRuntimeProvider.get().addContextObject(output.context);
                final ITermFactory termFactory = strategoRuntime.getTermFactory();
                final IStrategoTerm inputTerm = termFactory.makeTuple(output.result.ast, termFactory.makeString(key.asString()));
                return StrategoUtil.toString(strategoRuntime.invoke("spoofax3-editor-show-analysis-term", inputTerm));
            })
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Scope graph for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MinicsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
