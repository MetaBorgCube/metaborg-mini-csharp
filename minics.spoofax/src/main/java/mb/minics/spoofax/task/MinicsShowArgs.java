package mb.minics.spoofax.task;

import mb.common.region.Region;
import mb.resource.ResourceKey;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class MinicsShowArgs implements Serializable {
    public final ResourceKey key;
    public final @Nullable Region region;

    public MinicsShowArgs(ResourceKey key, @Nullable Region region) {
        this.key = key;
        this.region = region;
    }

    @Override public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        final MinicsShowArgs other = (MinicsShowArgs)obj;
        return key.equals(other.key) &&
            Objects.equals(region, other.region);
    }

    @Override public int hashCode() {
        return Objects.hash(key, region);
    }

    @Override public String toString() {
        return key.toString() + (region != null ? "@" + region : "");
    }
}
