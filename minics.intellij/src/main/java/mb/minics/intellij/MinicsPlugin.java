package mb.minics.intellij;

import mb.pie.dagger.DaggerRootPieComponent;
import mb.pie.dagger.PieComponent;
import mb.pie.dagger.RootPieModule;
import mb.pie.runtime.PieBuilderImpl;
import mb.resource.dagger.DaggerResourceServiceComponent;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.intellij.SpoofaxPlugin;
import mb.minics.spoofax.DaggerMinicsResourcesComponent;
import mb.minics.spoofax.MinicsResourcesComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MinicsPlugin {
    private static @Nullable MinicsResourcesComponent resourcesComponent;
    private static @Nullable ResourceServiceComponent resourceServiceComponent;
    private static @Nullable MinicsIntellijComponent component;
    private static @Nullable PieComponent pieComponent;

    public static MinicsResourcesComponent getResourcesComponent() {
        if(resourcesComponent == null) {
            throw new RuntimeException("Cannot access MinicsResourcesComponent; MinicsPlugin has not been started yet, or has been stopped");
        }
        return resourcesComponent;
    }

    public static ResourceServiceComponent getResourceServiceComponent() {
        if(resourceServiceComponent == null) {
            throw new RuntimeException("Cannot access ResourceServiceComponent; MinicsPlugin has not been started yet, or has been stopped");
        }
        return resourceServiceComponent;
    }

    public static mb.minics.intellij.MinicsIntellijComponent getComponent() {
        if(component == null) {
            throw new RuntimeException("Cannot access MinicsIntellijComponent; MinicsPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    public static PieComponent getPieComponent() {
        if(pieComponent == null) {
            throw new RuntimeException("Cannot access PieComponent; MinicsPlugin has not been started yet, or has been stopped");
        }
        return pieComponent;
    }

    public static void init() {
        resourcesComponent = DaggerMinicsResourcesComponent.create();
        resourceServiceComponent = DaggerResourceServiceComponent.builder()
            .resourceServiceModule(SpoofaxPlugin.getResourceServiceComponent().createChildModule().addRegistriesFrom(resourcesComponent))
            .loggerComponent(SpoofaxPlugin.getLoggerComponent())
            .build();
        component = DaggerMinicsIntellijComponent.builder()
            .intellijLoggerComponent(SpoofaxPlugin.getLoggerComponent())
            .minicsResourcesComponent(resourcesComponent)
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
