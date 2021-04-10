package mb.microcs.spoofax.task;

import mb.log.api.LoggerFactory;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.stratego.common.StrategoRuntime;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;

/**
 * Downgrades &lt;P&gt; placeholder terms to Plhdr() constructors.
 */
public final class MicrocsDowngradePlaceholders extends StrategoTaskDefBase<MicrocsDowngradePlaceholders.Input, @Nullable IStrategoTerm> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> termSupplier;

        public Input(Supplier<@Nullable IStrategoTerm> termSupplier) {
            this.termSupplier = termSupplier;
        }
    }

    @Inject public MicrocsDowngradePlaceholders(
        Provider<StrategoRuntime> strategoRuntimeProvider,
        LoggerFactory loggerFactory
    ) {
        super(strategoRuntimeProvider, loggerFactory);
    }

    @Override public @Nullable IStrategoTerm exec(ExecContext context, Input input) throws Exception {
        return callStrategy("downgrade-placeholders-microcs", input.termSupplier.get(context));
    }
}
