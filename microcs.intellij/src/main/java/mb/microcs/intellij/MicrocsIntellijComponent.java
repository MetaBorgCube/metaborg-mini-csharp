package mb.microcs.intellij;

import dagger.Component;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.IntellijPlatformComponent;
import mb.spoofax.intellij.log.IntellijLoggerComponent;
import mb.microcs.spoofax.SpoofaxModule;
import mb.microcs.spoofax.MicrocsModule;
import mb.microcs.spoofax.MicrocsModuleExt;
import mb.microcs.spoofax.MicrocsResourcesComponent;
import mb.microcs.spoofax.MicrocsScope;

@MicrocsScope
@Component(
    modules = {
        MicrocsModule.class,
        MicrocsIntellijModule.class,
        SpoofaxModule.class,
        MicrocsModuleExt.class
    },
    dependencies = {
        IntellijLoggerComponent.class,
        MicrocsResourcesComponent.class,
        ResourceServiceComponent.class,
        IntellijPlatformComponent.class
    }
)
public interface MicrocsIntellijComponent extends IntellijLanguageComponent, mb.microcs.spoofax.MicrocsComponent {
    @Override mb.microcs.intellij.MicrocsLanguage getLanguage();

    @Override mb.microcs.intellij.MicrocsFileType getFileType();

    @Override mb.microcs.intellij.MicrocsFileElementType getFileElementType();
}
