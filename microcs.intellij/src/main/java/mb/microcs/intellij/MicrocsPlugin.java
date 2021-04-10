package mb.microcs.intellij;

import mb.pie.dagger.DaggerRootPieComponent;
import mb.pie.dagger.PieComponent;
import mb.pie.dagger.RootPieModule;
import mb.pie.runtime.PieBuilderImpl;
import mb.resource.dagger.DaggerResourceServiceComponent;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.intellij.SpoofaxPlugin;
import mb.microcs.spoofax.DaggerMicrocsResourcesComponent;
import mb.microcs.spoofax.MicrocsResourcesComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MicrocsPlugin {
    private static @Nullable MicrocsResourcesComponent resourcesComponent;
    private static @Nullable ResourceServiceComponent resourceServiceComponent;
    private static @Nullable MicrocsIntellijComponent component;
    private static @Nullable PieComponent pieComponent;

    public static MicrocsResourcesComponent getResourcesComponent() {
        if(resourcesComponent == null) {
            throw new RuntimeException("Cannot access MicrocsResourcesComponent; MicrocsPlugin has not been started yet, or has been stopped");
        }
        return resourcesComponent;
    }

    public static ResourceServiceComponent getResourceServiceComponent() {
        if(resourceServiceComponent == null) {
            throw new RuntimeException("Cannot access ResourceServiceComponent; MicrocsPlugin has not been started yet, or has been stopped");
        }
        return resourceServiceComponent;
    }

    public static mb.microcs.intellij.MicrocsIntellijComponent getComponent() {
        if(component == null) {
            throw new RuntimeException("Cannot access MicrocsIntellijComponent; MicrocsPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    public static PieComponent getPieComponent() {
        if(pieComponent == null) {
            throw new RuntimeException("Cannot access PieComponent; MicrocsPlugin has not been started yet, or has been stopped");
        }
        return pieComponent;
    }

    public static void init() {
        resourcesComponent = DaggerMicrocsResourcesComponent.create();
        resourceServiceComponent = DaggerResourceServiceComponent.builder()
            .resourceServiceModule(SpoofaxPlugin.getResourceServiceComponent().createChildModule().addRegistriesFrom(resourcesComponent))
            .loggerComponent(SpoofaxPlugin.getLoggerComponent())
            .build();
        component = DaggerMicrocsIntellijComponent.builder()
            .intellijLoggerComponent(SpoofaxPlugin.getLoggerComponent())
            .microcsResourcesComponent(resourcesComponent)
            .resourceServiceComponent(resourceServiceComponent)
            .intellijPlatformComponent(SpoofaxPlugin.getPlatformComponent())
            .build();
        pieComponent = DaggerRootPieComponent.builder()
            .rootPieModule(new RootPieModule(PieBuilderImpl::new, component))
            .loggerComponent(SpoofaxPlugin.getLoggerComponent())
            .resourceServiceComponent(resourceServiceComponent)
            .build();
    }
}
