package mb.minics.spoofax;

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
import mb.minics.spoofax.task.MinicsComplete;
import mb.minics.spoofax.task.MinicsDowngradePlaceholders;
import mb.minics.spoofax.task.MinicsIsInj;
import mb.minics.spoofax.task.MinicsPostAnalyze;
import mb.minics.spoofax.task.MinicsPreAnalyze;
import mb.minics.spoofax.task.MinicsPrettyPrint;
import mb.minics.spoofax.task.MinicsStatixSpec;
import mb.minics.spoofax.task.MinicsUpgradePlaceholders;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;


public class MinicsInstance extends GeneratedMinicsInstance {
    private final mb.minics.spoofax.task.MinicsParse minicsParse;
    private final mb.minics.spoofax.task.MinicsComplete minicsComplete;
    private final MinicsStatixSpec statixSpecTaskDef;
    private final MinicsPrettyPrint prettyPrintTaskDef;
    private final MinicsPreAnalyze preAnalyzeTaskDef;
    private final MinicsPostAnalyze postAnalyzeTaskDef;
    private final MinicsIsInj isInjTaskDef;
    private final MinicsUpgradePlaceholders upgradePlaceholdersTaskDef;
    private final MinicsDowngradePlaceholders downgradePlaceholdersTaskDef;

    @Inject public MinicsInstance(
        mb.minics.spoofax.task.MinicsParse minicsParse,
        mb.minics.spoofax.task.MinicsTokenize minicsTokenize,
        mb.minics.spoofax.task.MinicsCheckMulti minicsCheckMulti,
        mb.minics.spoofax.task.MinicsStyle minicsStyle,
        mb.minics.spoofax.task.MinicsComplete minicsComplete,
        MinicsStatixSpec statixSpecTaskDef,
        MinicsPrettyPrint prettyPrintTaskDef,
        MinicsPreAnalyze preAnalyzeTaskDef,
        MinicsPostAnalyze postAnalyzeTaskDef,
        MinicsIsInj isInjTaskDef,
        MinicsUpgradePlaceholders upgradePlaceholdersTaskDef,
        MinicsDowngradePlaceholders downgradePlaceholdersTaskDef,
        mb.minics.spoofax.command.MinicsCompileFileCommand minicsCompileFileCommand,
        mb.minics.spoofax.command.MinicsCompileFileAltCommand minicsCompileFileAltCommand,
        mb.minics.spoofax.command.MinicsCompileDirectoryCommand minicsCompileDirectoryCommand,
        mb.minics.spoofax.command.MinicsShowParsedAstCommand minicsShowParsedAstCommand,
        mb.minics.spoofax.command.MinicsShowDesugaredAstCommand minicsShowDesugaredAstCommand,
        mb.minics.spoofax.command.MinicsShowAnalyzedAstCommand minicsShowAnalyzedAstCommand,
        mb.minics.spoofax.command.MinicsShowPrettyPrintedTextCommand minicsShowPrettyPrintedTextCommand,
        mb.minics.spoofax.command.MinicsShowScopeGraphCommand minicsShowScopeGraphCommand,
        Set<CommandDef<?>> commandDefs,
        Set<AutoCommandRequest<?>> autoCommandDefs
    ) {
        super(
            minicsParse,
            minicsTokenize,
            minicsCheckMulti,
            minicsStyle,
            minicsComplete,
            minicsCompileFileCommand,
            minicsCompileFileAltCommand,
            minicsCompileDirectoryCommand,
            minicsShowParsedAstCommand,
            minicsShowDesugaredAstCommand,
            minicsShowAnalyzedAstCommand,
            minicsShowPrettyPrintedTextCommand,
            minicsShowScopeGraphCommand,
            commandDefs,
            autoCommandDefs
        );
        this.minicsParse = minicsParse;
        this.minicsComplete = minicsComplete;
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
        return minicsComplete.createTask(new MinicsComplete.Input(
            resourceKey,
            primarySelection.getStartOffset(),
            minicsParse.createAstSupplier(resourceKey).map(Result::get),
            (c, t) -> prettyPrintTaskDef.createFunction().apply(c, new MinicsPrettyPrint.Input(c2 -> t)),
            (c, t) -> preAnalyzeTaskDef.createFunction().apply(c, new MinicsPreAnalyze.Input(c2 -> t)),
            (c, t) -> postAnalyzeTaskDef.createFunction().apply(c, new MinicsPostAnalyze.Input(c2 -> t)),
            (c, t) -> isInjTaskDef.createFunction().apply(c, new MinicsIsInj.Input(c2 -> t)),    // Should be a predicate
            (c, t) -> upgradePlaceholdersTaskDef.createFunction().apply(c, new MinicsUpgradePlaceholders.Input(c2 -> t)),
            (c, t) -> downgradePlaceholdersTaskDef.createFunction().apply(c, new MinicsDowngradePlaceholders.Input(c2 -> t))
        ));
    }

}
