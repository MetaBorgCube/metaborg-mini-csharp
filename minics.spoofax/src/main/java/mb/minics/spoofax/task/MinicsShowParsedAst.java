package mb.minics.spoofax.task;

import mb.common.region.Region;
import mb.jsglr.common.TermTracer;
import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.stratego.common.StrategoUtil;
import mb.minics.spoofax.MinicsScope;
import mb.minics.spoofax.task.MinicsParse;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;

@MinicsScope
public class MinicsShowParsedAst implements TaskDef<MinicsShowArgs, CommandFeedback> {
    private final MinicsParse parse;

    @Inject public MinicsShowParsedAst(MinicsParse parse) {
        this.parse = parse;
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
            .map(StrategoUtil::toString)
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Parsed AST for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MinicsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
