package mb.microcs.intellij;

import mb.spoofax.intellij.IntellijFileElementType;

import javax.inject.Inject;

@mb.microcs.spoofax.MicrocsScope
public class MicrocsFileElementType extends IntellijFileElementType {
    @Inject public MicrocsFileElementType() {
        super(mb.microcs.intellij.MicrocsPlugin.getComponent());
    }
}
