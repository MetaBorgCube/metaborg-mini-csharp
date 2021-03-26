package mb.minics.intellij;

import mb.spoofax.intellij.IntellijLanguageFileType;

import javax.inject.Inject;

@mb.minics.spoofax.MinicsScope
public class MinicsFileType extends IntellijLanguageFileType {
    @Inject protected MinicsFileType() {
        super(mb.minics.intellij.MinicsPlugin.getComponent());
    }
}
