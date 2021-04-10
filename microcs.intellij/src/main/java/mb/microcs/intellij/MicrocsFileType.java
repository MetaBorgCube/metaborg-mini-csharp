package mb.microcs.intellij;

import mb.spoofax.intellij.IntellijLanguageFileType;

import javax.inject.Inject;

@mb.microcs.spoofax.MicrocsScope
public class MicrocsFileType extends IntellijLanguageFileType {
    @Inject protected MicrocsFileType() {
        super(mb.microcs.intellij.MicrocsPlugin.getComponent());
    }
}
