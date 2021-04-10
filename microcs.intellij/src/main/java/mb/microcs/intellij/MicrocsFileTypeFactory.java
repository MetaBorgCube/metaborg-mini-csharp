package mb.microcs.intellij;

import mb.spoofax.intellij.IntellijFileTypeFactory;

public class MicrocsFileTypeFactory extends IntellijFileTypeFactory {
    // Instantiated by IntelliJ.
    private MicrocsFileTypeFactory() {
        super(mb.microcs.intellij.MicrocsPlugin.getComponent());
    }
}
