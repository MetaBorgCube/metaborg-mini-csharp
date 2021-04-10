package mb.microcs.spoofax.task;

import mb.common.region.Region;
import mb.jsglr.common.TermTracer;
import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.stratego.common.StrategoUtil;
import mb.microcs.spoofax.MicrocsScope;
import mb.microcs.spoofax.task.MicrocsAnalyze;
import mb.microcs.spoofax.task.MicrocsParse;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;

@MicrocsScope
public class MicrocsShowAnalyzedAst implements TaskDef<MicrocsShowArgs, CommandFeedback> {
    private final MicrocsParse parse;
    private final MicrocsAnalyze analyze;

    @Inject public MicrocsShowAnalyzedAst(MicrocsParse parse, MicrocsAnalyze analyze) {
        this.parse = parse;
        this.analyze = analyze;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, MicrocsShowArgs input) {
        final ResourceKey key = input.key;
        final @Nullable Region region = input.region;
        return context.require(analyze, new MicrocsAnalyze.Input(key, parse.createAstSupplier(key)))
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

    @Override public Task<CommandFeedback> createTask(MicrocsShowArgs input) {
        return TaskDef.super.createTask(input);
    }
}
