package mb.microcs.intellij;

import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighterFactory;

public class MicrocsSyntaxHighlighterFactory extends SpoofaxSyntaxHighlighterFactory {
    // Instantiated by IntelliJ.
    private MicrocsSyntaxHighlighterFactory() {
        super(MicrocsPlugin.getComponent());
    }
}
