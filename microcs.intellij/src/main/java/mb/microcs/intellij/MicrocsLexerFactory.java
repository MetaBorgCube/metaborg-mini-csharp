package mb.microcs.intellij;

import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.microcs.intellij.MicrocsPlugin;
import mb.microcs.spoofax.MicrocsScope;

import javax.inject.Inject;

@MicrocsScope
public class MicrocsLexerFactory extends SpoofaxLexerFactory {
    @Inject public MicrocsLexerFactory() {
        super(MicrocsPlugin.getComponent(), MicrocsPlugin.getResourceServiceComponent(), MicrocsPlugin.getPieComponent());
    }
}
