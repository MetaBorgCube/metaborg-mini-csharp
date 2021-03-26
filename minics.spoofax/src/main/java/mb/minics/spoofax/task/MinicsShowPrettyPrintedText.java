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
import java.io.IOException;

@MinicsScope
public class MinicsShowPrettyPrintedText implements TaskDef<MinicsShowArgs, CommandFeedback> {
    private final MinicsParse parse;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject public MinicsShowPrettyPrintedText(
        MinicsParse parse,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        this.parse = parse;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MinicsShowArgs input) throws IOException {
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
            .mapCatching(ast -> StrategoUtil.toString(strategoRuntimeProvider.get().invoke("pp-minics-string", ast)))
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Pretty-printed text for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MinicsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
