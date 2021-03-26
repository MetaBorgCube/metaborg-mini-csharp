package mb.minics.intellij;

import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighterFactory;

public class MinicsSyntaxHighlighterFactory extends SpoofaxSyntaxHighlighterFactory {
    // Instantiated by IntelliJ.
    private MinicsSyntaxHighlighterFactory() {
        super(MinicsPlugin.getComponent());
    }
}
