package mb.microcs.intellij;

import mb.spoofax.intellij.editor.SpoofaxParserDefinition;

public class MicrocsParserDefinition extends SpoofaxParserDefinition {
    // Instantiated by IntelliJ.
    private MicrocsParserDefinition() {
        super(MicrocsPlugin.getComponent());
    }
}
