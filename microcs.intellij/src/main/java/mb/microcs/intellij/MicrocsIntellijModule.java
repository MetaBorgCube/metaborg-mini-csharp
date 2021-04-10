package mb.microcs.intellij;

import com.intellij.openapi.util.IconLoader;
import dagger.Module;
import dagger.Provides;
import mb.spoofax.intellij.IntellijLanguage;
import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.spoofax.intellij.psi.SpoofaxTokenTypeManager;
import mb.spoofax.intellij.editor.ScopeManager;
import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighter;

import mb.microcs.spoofax.MicrocsScope;

import javax.swing.*;

@Module
public class MicrocsIntellijModule {
    @Provides @MicrocsScope
    static IntellijLanguage provideSpoofaxLanguage(mb.microcs.intellij.MicrocsLanguage language) {
        // Downcast because injections in spoofax.intellij require an IntellijLanguage, and dagger does not implicitly downcast.
        return language;
    }

    @Provides @MicrocsScope
    static SpoofaxLexerFactory provideLexerFactory(MicrocsLexerFactory lexerFactory) {
        return lexerFactory;
    }

    @Provides @MicrocsScope
    static Icon provideFileIcon() {
        return IconLoader.getIcon("/META-INF/fileIcon.svg");
    }


    @Provides @MicrocsScope
    static SpoofaxSyntaxHighlighter.Factory provideSyntaxHighlighterFactory(ScopeManager scopeManager) {
        return new SpoofaxSyntaxHighlighter.Factory(scopeManager); // TODO: generate language-specific class instead.
    }

    @Provides @MicrocsScope
    static SpoofaxTokenTypeManager provideTokenTypeManager(IntellijLanguage language) {
        return new SpoofaxTokenTypeManager(language); // TODO: generate language-specific class instead.
    }

    @Provides @MicrocsScope
    static ScopeManager provideScopeManager() {
        return new ScopeManager(); // TODO: generate language-specific class instead.
    }
}
