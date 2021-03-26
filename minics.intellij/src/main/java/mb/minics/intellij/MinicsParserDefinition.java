package mb.minics.intellij;

import mb.spoofax.intellij.editor.SpoofaxParserDefinition;

public class MinicsParserDefinition extends SpoofaxParserDefinition {
    // Instantiated by IntelliJ.
    private MinicsParserDefinition() {
        super(MinicsPlugin.getComponent());
    }
}
