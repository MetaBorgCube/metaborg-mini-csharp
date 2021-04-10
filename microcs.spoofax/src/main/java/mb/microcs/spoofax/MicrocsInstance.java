package mb.microcs.spoofax;

import mb.common.message.KeyedMessages;
import mb.common.option.Option;
import mb.common.result.Result;
import mb.common.style.Styling;
import mb.common.region.Region;
import mb.common.token.Tokens;
import mb.common.util.CollectionView;
import mb.common.util.EntryView;
import mb.common.util.ListView;
import mb.common.util.MapView;
import mb.common.util.SetView;
import mb.completions.common.CompletionResult;
import mb.pie.api.Task;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.match.PathResourceMatcher;
import mb.resource.hierarchical.match.path.ExtensionsPathMatcher;
import mb.resource.hierarchical.match.path.NoHiddenPathMatcher;
import mb.resource.hierarchical.walk.PathResourceWalker;
import mb.spoofax.core.language.LanguageInstance;
import mb.spoofax.core.language.cli.CliCommand;
import mb.spoofax.core.language.command.AutoCommandRequest;
import mb.spoofax.core.language.command.CommandDef;
import mb.spoofax.core.language.command.CommandExecutionType;
import mb.spoofax.core.language.command.EditorFileType;
import mb.spoofax.core.language.command.HierarchicalResourceType;
import mb.spoofax.core.language.command.arg.RawArgs;
import mb.spoofax.core.language.menu.CommandAction;
import mb.spoofax.core.language.menu.MenuItem;
import mb.microcs.spoofax.task.MicrocsComplete;
import mb.microcs.spoofax.task.MicrocsDowngradePlaceholders;
import mb.microcs.spoofax.task.MicrocsIsInj;
import mb.microcs.spoofax.task.MicrocsPostAnalyze;
import mb.microcs.spoofax.task.MicrocsPreAnalyze;
import mb.microcs.spoofax.task.MicrocsPrettyPrint;
import mb.microcs.spoofax.task.MicrocsStatixSpec;
import mb.microcs.spoofax.task.MicrocsUpgradePlaceholders;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;


public class MicrocsInstance extends GeneratedMicrocsInstance {
    private final mb.microcs.spoofax.task.MicrocsParse microcsParse;
    private final mb.microcs.spoofax.task.MicrocsComplete microcsComplete;
    private final MicrocsStatixSpec statixSpecTaskDef;
    private final MicrocsPrettyPrint prettyPrintTaskDef;
    private final MicrocsPreAnalyze preAnalyzeTaskDef;
    private final MicrocsPostAnalyze postAnalyzeTaskDef;
    private final MicrocsIsInj isInjTaskDef;
    private final MicrocsUpgradePlaceholders upgradePlaceholdersTaskDef;
    private final MicrocsDowngradePlaceholders downgradePlaceholdersTaskDef;

    @Inject public MicrocsInstance(
        mb.microcs.spoofax.task.MicrocsParse microcsParse,
        mb.microcs.spoofax.task.MicrocsTokenize microcsTokenize,
        mb.microcs.spoofax.task.MicrocsCheckMulti microcsCheckMulti,
        mb.microcs.spoofax.task.MicrocsStyle microcsStyle,
        mb.microcs.spoofax.task.MicrocsComplete microcsComplete,
        MicrocsStatixSpec statixSpecTaskDef,
        MicrocsPrettyPrint prettyPrintTaskDef,
        MicrocsPreAnalyze preAnalyzeTaskDef,
        MicrocsPostAnalyze postAnalyzeTaskDef,
        MicrocsIsInj isInjTaskDef,
        MicrocsUpgradePlaceholders upgradePlaceholdersTaskDef,
        MicrocsDowngradePlaceholders downgradePlaceholdersTaskDef,
        mb.microcs.spoofax.command.MicrocsCompileFileCommand microcsCompileFileCommand,
        mb.microcs.spoofax.command.MicrocsCompileFileAltCommand microcsCompileFileAltCommand,
        mb.microcs.spoofax.command.MicrocsCompileDirectoryCommand microcsCompileDirectoryCommand,
        mb.microcs.spoofax.command.MicrocsShowParsedAstCommand microcsShowParsedAstCommand,
        mb.microcs.spoofax.command.MicrocsShowDesugaredAstCommand microcsShowDesugaredAstCommand,
        mb.microcs.spoofax.command.MicrocsShowAnalyzedAstCommand microcsShowAnalyzedAstCommand,
        mb.microcs.spoofax.command.MicrocsShowPrettyPrintedTextCommand microcsShowPrettyPrintedTextCommand,
        mb.microcs.spoofax.command.MicrocsShowScopeGraphCommand microcsShowScopeGraphCommand,
        Set<CommandDef<?>> commandDefs,
        Set<AutoCommandRequest<?>> autoCommandDefs
    ) {
        super(
            microcsParse,
            microcsTokenize,
            microcsCheckMulti,
            microcsStyle,
            microcsComplete,
            microcsCompileFileCommand,
            microcsCompileFileAltCommand,
            microcsCompileDirectoryCommand,
            microcsShowParsedAstCommand,
            microcsShowDesugaredAstCommand,
            microcsShowAnalyzedAstCommand,
            microcsShowPrettyPrintedTextCommand,
            microcsShowScopeGraphCommand,
            commandDefs,
            autoCommandDefs
        );
        this.microcsParse = microcsParse;
        this.microcsComplete = microcsComplete;
        this.statixSpecTaskDef = statixSpecTaskDef;
        this.prettyPrintTaskDef = prettyPrintTaskDef;
        this.preAnalyzeTaskDef = preAnalyzeTaskDef;
        this.postAnalyzeTaskDef = postAnalyzeTaskDef;
        this.isInjTaskDef = isInjTaskDef;
        this.upgradePlaceholdersTaskDef = upgradePlaceholdersTaskDef;
        this.downgradePlaceholdersTaskDef = downgradePlaceholdersTaskDef;
    }


    @Override
    public Task<@Nullable CompletionResult> createCompletionTask(ResourceKey resourceKey, Region primarySelection) {
        return microcsComplete.createTask(new MicrocsComplete.Input(
            resourceKey,
            primarySelection.getStartOffset(),
            microcsParse.createAstSupplier(resourceKey).map(Result::get),
            (c, t) -> prettyPrintTaskDef.createFunction().apply(c, new MicrocsPrettyPrint.Input(c2 -> t)),
            (c, t) -> preAnalyzeTaskDef.createFunction().apply(c, new MicrocsPreAnalyze.Input(c2 -> t)),
            (c, t) -> postAnalyzeTaskDef.createFunction().apply(c, new MicrocsPostAnalyze.Input(c2 -> t)),
            (c, t) -> isInjTaskDef.createFunction().apply(c, new MicrocsIsInj.Input(c2 -> t)),    // Should be a predicate
            (c, t) -> upgradePlaceholdersTaskDef.createFunction().apply(c, new MicrocsUpgradePlaceholders.Input(c2 -> t)),
            (c, t) -> downgradePlaceholdersTaskDef.createFunction().apply(c, new MicrocsDowngradePlaceholders.Input(c2 -> t))
        ));
    }

}
