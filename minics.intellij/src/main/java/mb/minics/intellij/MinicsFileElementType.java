package mb.minics.intellij;

import mb.spoofax.intellij.IntellijFileElementType;

import javax.inject.Inject;

@mb.minics.spoofax.MinicsScope
public class MinicsFileElementType extends IntellijFileElementType {
    @Inject public MinicsFileElementType() {
        super(mb.minics.intellij.MinicsPlugin.getComponent());
    }
}
