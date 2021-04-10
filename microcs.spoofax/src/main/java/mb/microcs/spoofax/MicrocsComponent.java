package mb.microcs.spoofax;

import dagger.Component;
import mb.log.dagger.LoggerComponent;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.core.platform.PlatformComponent;

@MicrocsScope
@Component(
    modules = {
        MicrocsModule.class,
        MicrocsModuleExt.class,
        SpoofaxModule.class
    },
    dependencies = {
        LoggerComponent.class,
        MicrocsResourcesComponent.class,
        ResourceServiceComponent.class,
        PlatformComponent.class
    }
)
public interface MicrocsComponent extends GeneratedMicrocsComponent  {
    mb.microcs.spoofax.task.MicrocsComplete getMicrocsComplete();
    mb.microcs.spoofax.task.MicrocsAnalyze getMicrocsAnalyze();
}
