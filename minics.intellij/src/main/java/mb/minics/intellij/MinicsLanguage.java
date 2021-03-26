package mb.minics.intellij;

import mb.spoofax.intellij.IntellijLanguage;

import javax.inject.Inject;

@mb.minics.spoofax.MinicsScope
public class MinicsLanguage extends IntellijLanguage {
    @Inject public MinicsLanguage() {
        super(MinicsPlugin.getComponent());
    }
}
