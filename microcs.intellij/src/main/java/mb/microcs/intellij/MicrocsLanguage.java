package mb.microcs.intellij;

import mb.spoofax.intellij.IntellijLanguage;

import javax.inject.Inject;

@mb.microcs.spoofax.MicrocsScope
public class MicrocsLanguage extends IntellijLanguage {
    @Inject public MicrocsLanguage() {
        super(MicrocsPlugin.getComponent());
    }
}
