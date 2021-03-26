package mb.minics.spoofax;

import dagger.Component;
import mb.log.dagger.LoggerComponent;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.core.platform.PlatformComponent;

@MinicsScope
@Component(
    modules = {
        MinicsModule.class,
        MinicsModuleExt.class,
        SpoofaxModule.class
    },
    dependencies = {
        LoggerComponent.class,
        MinicsResourcesComponent.class,
        ResourceServiceComponent.class,
        PlatformComponent.class
    }
)
public interface MinicsComponent extends GeneratedMinicsComponent  {
    mb.minics.spoofax.task.MinicsComplete getMinicsComplete();
    mb.minics.spoofax.task.MinicsAnalyze getMinicsAnalyze();
}
