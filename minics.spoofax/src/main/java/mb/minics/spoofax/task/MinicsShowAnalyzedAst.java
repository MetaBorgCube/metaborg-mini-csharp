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
import mb.minics.spoofax.task.MinicsAnalyze;
import mb.minics.spoofax.task.MinicsParse;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;

@MinicsScope
public class MinicsShowAnalyzedAst implements TaskDef<MinicsShowArgs, CommandFeedback> {
    private final MinicsParse parse;
    private final MinicsAnalyze analyze;

    @Inject public MinicsShowAnalyzedAst(MinicsParse parse, MinicsAnalyze analyze) {
        this.parse = parse;
        this.analyze = analyze;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MinicsShowArgs input) {
        final ResourceKey key = input.key;
        final @Nullable Region region = input.region;
        return context.require(analyze, new MinicsAnalyze.Input(key, parse.createAstSupplier(key)))
            .map(output -> {
                if(region != null) {
                    return TermTracer.getSmallestTermEncompassingRegion(output.result.ast, region);
                } else {
                    return output.result.ast;
                }
            })
            .map(StrategoUtil::toString)
            .mapOrElse(text -> CommandFeedback.of(ShowFeedback.showText(text, "Analyzed AST for '" + key + "'")), e -> CommandFeedback.ofTryExtractMessagesFrom(e, key));
    }

    @Override public Task<CommandFeedback> createTask(MinicsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
