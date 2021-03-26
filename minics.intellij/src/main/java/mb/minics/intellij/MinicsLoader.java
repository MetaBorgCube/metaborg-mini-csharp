package mb.minics.intellij;

import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;

public class MinicsLoader implements ApplicationLoadListener {
    // Instantiated by IntelliJ.
    private MinicsLoader() {}

    @Override public void beforeApplicationLoaded(Application application, String configPath) {
        try {
            MinicsPlugin.init();
        } catch(Exception e) {
            throw new RuntimeException("Failed to initialize Mini C# IntelliJ plugin", e);
        }
    }
}
