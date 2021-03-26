package mb.minics.intellij;

import mb.spoofax.intellij.IntellijFileTypeFactory;

public class MinicsFileTypeFactory extends IntellijFileTypeFactory {
    // Instantiated by IntelliJ.
    private MinicsFileTypeFactory() {
        super(mb.minics.intellij.MinicsPlugin.getComponent());
    }
}
