package mb.minics.spoofax;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import mb.log.api.LoggerFactory;
import mb.pie.api.TaskDef;
import mb.statix.common.StatixAnalyzer;
import mb.minics.MinicsAnalyzer;
import mb.minics.MinicsAnalyzerFactory;
import mb.minics.spoofax.task.MinicsDowngradePlaceholders;
import mb.minics.spoofax.task.MinicsIsInj;
import mb.minics.spoofax.task.MinicsPostAnalyze;
import mb.minics.spoofax.task.MinicsPreAnalyze;
import mb.minics.spoofax.task.MinicsPrettyPrint;
import mb.minics.spoofax.task.MinicsStatixSpec;
import mb.minics.spoofax.task.MinicsUpgradePlaceholders;
import org.spoofax.interpreter.terms.ITermFactory;

import java.util.HashSet;
import java.util.Set;

@Module
public class MinicsModuleExt {

    @Provides @MinicsScope
    static MinicsAnalyzerFactory provideAnalyzerFactory(LoggerFactory loggerFactory, ITermFactory termFactory) {
        return new MinicsAnalyzerFactory(termFactory, loggerFactory);
    }
    @Provides @MinicsScope
    static MinicsAnalyzer provideAnalyzer(MinicsAnalyzerFactory analyzerFactory) {
        return analyzerFactory.create();
    }
    @Provides @MinicsScope
    static StatixAnalyzer bindAnalyzer(MinicsAnalyzer analyzer) { return analyzer; }

    @Provides @MinicsScope @MinicsQualifier @ElementsIntoSet
    static Set<TaskDef<?, ?>> provideTaskDefsSet(
        mb.minics.spoofax.task.MinicsComplete minicsComplete,
        MinicsStatixSpec statixSpec,
        MinicsPrettyPrint prettyPrintTaskDef,
        MinicsPreAnalyze preAnalyzeTaskDef,
        MinicsPostAnalyze postAnalyzeTaskDef,
        MinicsIsInj isInjTaskDef,
        MinicsUpgradePlaceholders upgradePlaceholdersTaskDef,
        MinicsDowngradePlaceholders downgradePlaceholdersTaskDef
    ) {
        final HashSet<TaskDef<?, ?>> taskDefs = new HashSet<>();
        taskDefs.add(minicsComplete);
        taskDefs.add(statixSpec);
        taskDefs.add(prettyPrintTaskDef);
        taskDefs.add(preAnalyzeTaskDef);
        taskDefs.add(postAnalyzeTaskDef);
        taskDefs.add(isInjTaskDef);
        taskDefs.add(upgradePlaceholdersTaskDef);
        taskDefs.add(downgradePlaceholdersTaskDef);
        return taskDefs;
    }

}
