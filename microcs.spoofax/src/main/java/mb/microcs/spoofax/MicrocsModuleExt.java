package mb.microcs.spoofax;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import mb.log.api.LoggerFactory;
import mb.pie.api.TaskDef;
import mb.statix.common.StatixAnalyzer;
import mb.microcs.MicrocsAnalyzer;
import mb.microcs.MicrocsAnalyzerFactory;
import mb.microcs.spoofax.task.MicrocsDowngradePlaceholders;
import mb.microcs.spoofax.task.MicrocsIsInj;
import mb.microcs.spoofax.task.MicrocsPostAnalyze;
import mb.microcs.spoofax.task.MicrocsPreAnalyze;
import mb.microcs.spoofax.task.MicrocsPrettyPrint;
import mb.microcs.spoofax.task.MicrocsStatixSpec;
import mb.microcs.spoofax.task.MicrocsUpgradePlaceholders;
import org.spoofax.interpreter.terms.ITermFactory;

import java.util.HashSet;
import java.util.Set;

@Module
public class MicrocsModuleExt {

    @Provides @MicrocsScope
    static MicrocsAnalyzerFactory provideAnalyzerFactory(LoggerFactory loggerFactory, ITermFactory termFactory) {
        return new MicrocsAnalyzerFactory(termFactory, loggerFactory);
    }
    @Provides @MicrocsScope
    static MicrocsAnalyzer provideAnalyzer(MicrocsAnalyzerFactory analyzerFactory) {
        return analyzerFactory.create();
    }
    @Provides @MicrocsScope
    static StatixAnalyzer bindAnalyzer(MicrocsAnalyzer analyzer) { return analyzer; }

    @Provides @MicrocsScope @MicrocsQualifier @ElementsIntoSet
    static Set<TaskDef<?, ?>> provideTaskDefsSet(
        mb.microcs.spoofax.task.MicrocsComplete microcsComplete,
        MicrocsStatixSpec statixSpec,
        MicrocsPrettyPrint prettyPrintTaskDef,
        MicrocsPreAnalyze preAnalyzeTaskDef,
        MicrocsPostAnalyze postAnalyzeTaskDef,
        MicrocsIsInj isInjTaskDef,
        MicrocsUpgradePlaceholders upgradePlaceholdersTaskDef,
        MicrocsDowngradePlaceholders downgradePlaceholdersTaskDef
    ) {
        final HashSet<TaskDef<?, ?>> taskDefs = new HashSet<>();
        taskDefs.add(microcsComplete);
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
