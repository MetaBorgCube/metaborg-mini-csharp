package mb.minics.intellij.editor;

import mb.spoofax.intellij.editor.SpoofaxCompletionContributor;
import mb.minics.intellij.MinicsPlugin;

public class MinicsCompletionContributor extends SpoofaxCompletionContributor {
    // Instantiated by IntelliJ.
    protected MinicsCompletionContributor() {
        super(MinicsPlugin.getComponent(), MinicsPlugin.getPieComponent());
    }
}
