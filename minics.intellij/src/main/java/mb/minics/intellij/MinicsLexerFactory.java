package mb.minics.intellij;

import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.minics.intellij.MinicsPlugin;
import mb.minics.spoofax.MinicsScope;

import javax.inject.Inject;

@MinicsScope
public class MinicsLexerFactory extends SpoofaxLexerFactory {
    @Inject public MinicsLexerFactory() {
        super(MinicsPlugin.getComponent(), MinicsPlugin.getResourceServiceComponent(), MinicsPlugin.getPieComponent());
    }
}
