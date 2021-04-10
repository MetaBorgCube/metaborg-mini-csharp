package mb.microcs.spoofax.task;

import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.statix.common.StatixSpecProvider;
import mb.statix.spec.Spec;
import mb.microcs.spoofax.MicrocsScope;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

@MicrocsScope
public class MicrocsStatixSpec implements TaskDef<MicrocsStatixSpec.Input, @Nullable Spec> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> specAstSupplier;

        public Input(Supplier<@Nullable IStrategoTerm> specAstSupplier) {
            this.specAstSupplier = specAstSupplier;
        }

        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            final Input that = (Input)o;
            return this.specAstSupplier.equals(that.specAstSupplier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.specAstSupplier);
        }
    }

    private final StatixSpecProvider specProvider;

    @Inject public MicrocsStatixSpec(
        StatixSpecProvider specProvider
    ) {
        this.specProvider = specProvider;
    }

    @Override
    public String getId() {
        return MicrocsStatixSpec.class.getName();
    }

    @Override
    public @Nullable Spec exec(ExecContext context, Input input) throws Exception {
        @Nullable IStrategoTerm specAst = input.specAstSupplier.get(context);
        if (specAst == null) return null;
        return specProvider.getSpec(specAst);
    }
}
