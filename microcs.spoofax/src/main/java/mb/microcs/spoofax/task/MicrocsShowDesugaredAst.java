package mb.microcs.spoofax.task;

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
import mb.microcs.spoofax.MicrocsScope;
import mb.microcs.spoofax.task.MicrocsParse;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;

@MicrocsScope
public class MicrocsShowDesugaredAst implements TaskDef<MicrocsShowArgs, CommandFeedback> {
    private final MicrocsParse parse;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject public MicrocsShowDesugaredAst(
        MicrocsParse parse,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        this.parse = parse;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MicrocsShowArgs input) throws Exception {
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

    @Override public Task<CommandFeedback> createTask(MicrocsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
