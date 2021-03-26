package mb.minics.spoofax.task;

import mb.common.region.Region;
import mb.jsglr.common.TermTracer;
import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoUtil;
import mb.minics.spoofax.MinicsScope;
import mb.minics.spoofax.task.MinicsParse;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;

@MinicsScope
public class MinicsShowDesugaredAst implements TaskDef<MinicsShowArgs, CommandFeedback> {
    private final MinicsParse parse;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject public MinicsShowDesugaredAst(
        MinicsParse parse,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        this.parse = parse;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MinicsShowArgs input) throws Exception {
        final ResourceKey key = input.key;
        final @Nullable Region region = input.region;
        return context
            .require(parse.createAstSupplier(key))
            .map(ast -> {
                if(region != null) {
                    return TermTracer.getSmallestTermEncompassingRegion(ast, region);
                } else {
                    return ast;
                }
            })
            .mapCatching(ast -> StrategoUtil.toString(strategoRuntimeProvider.get().invoke("desugar-all", ast)))
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Desugared AST for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MinicsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
