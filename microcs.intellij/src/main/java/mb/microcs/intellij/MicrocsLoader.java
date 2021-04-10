package mb.microcs.intellij;

import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;

public class MicrocsLoader implements ApplicationLoadListener {
    // Instantiated by IntelliJ.
    private MicrocsLoader() {}

    @Override public void beforeApplicationLoaded(Application application, String configPath) {
        try {
            MicrocsPlugin.init();
        } catch(Exception e) {
            throw new RuntimeException("Failed to initialize Mini C# IntelliJ plugin", e);
        }
    }
}
