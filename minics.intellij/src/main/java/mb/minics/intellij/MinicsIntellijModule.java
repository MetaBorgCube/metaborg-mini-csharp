package mb.minics.intellij;

import com.intellij.openapi.util.IconLoader;
import dagger.Module;
import dagger.Provides;
import mb.spoofax.intellij.IntellijLanguage;
import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.spoofax.intellij.psi.SpoofaxTokenTypeManager;
import mb.spoofax.intellij.editor.ScopeManager;
import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighter;

import mb.minics.spoofax.MinicsScope;

import javax.swing.*;

@Module
public class MinicsIntellijModule {
    @Provides @MinicsScope
    static IntellijLanguage provideSpoofaxLanguage(mb.minics.intellij.MinicsLanguage language) {
        // Downcast because injections in spoofax.intellij require an IntellijLanguage, and dagger does not implicitly downcast.
        return language;
    }

    @Provides @MinicsScope
    static SpoofaxLexerFactory provideLexerFactory(MinicsLexerFactory lexerFactory) {
        return lexerFactory;
    }

    @Provides @MinicsScope
    static Icon provideFileIcon() {
        return IconLoader.getIcon("/META-INF/fileIcon.svg");
    }


    @Provides @MinicsScope
    static SpoofaxSyntaxHighlighter.Factory provideSyntaxHighlighterFactory(ScopeManager scopeManager) {
        return new SpoofaxSyntaxHighlighter.Factory(scopeManager); // TODO: generate language-specific class instead.
    }

    @Provides @MinicsScope
    static SpoofaxTokenTypeManager provideTokenTypeManager(IntellijLanguage language) {
        return new SpoofaxTokenTypeManager(language); // TODO: generate language-specific class instead.
    }

    @Provides @MinicsScope
    static ScopeManager provideScopeManager() {
        return new ScopeManager(); // TODO: generate language-specific class instead.
    }
}
