package mb.microcs.intellij.editor;

import mb.spoofax.intellij.editor.SpoofaxCompletionContributor;
import mb.microcs.intellij.MicrocsPlugin;

public class MicrocsCompletionContributor extends SpoofaxCompletionContributor {
    // Instantiated by IntelliJ.
    protected MicrocsCompletionContributor() {
        super(MicrocsPlugin.getComponent(), MicrocsPlugin.getPieComponent());
    }
}
