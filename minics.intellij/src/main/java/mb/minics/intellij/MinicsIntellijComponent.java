package mb.minics.intellij;

import dagger.Component;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.IntellijPlatformComponent;
import mb.spoofax.intellij.log.IntellijLoggerComponent;
import mb.minics.spoofax.SpoofaxModule;
import mb.minics.spoofax.MinicsModule;
import mb.minics.spoofax.MinicsModuleExt;
import mb.minics.spoofax.MinicsResourcesComponent;
import mb.minics.spoofax.MinicsScope;

@MinicsScope
@Component(
    modules = {
        MinicsModule.class,
        MinicsIntellijModule.class,
        SpoofaxModule.class,
        MinicsModuleExt.class
    },
    dependencies = {
        IntellijLoggerComponent.class,
        MinicsResourcesComponent.class,
        ResourceServiceComponent.class,
        IntellijPlatformComponent.class
    }
)
public interface MinicsIntellijComponent extends IntellijLanguageComponent, mb.minics.spoofax.MinicsComponent {
    @Override mb.minics.intellij.MinicsLanguage getLanguage();

    @Override mb.minics.intellij.MinicsFileType getFileType();

    @Override mb.minics.intellij.MinicsFileElementType getFileElementType();
}
