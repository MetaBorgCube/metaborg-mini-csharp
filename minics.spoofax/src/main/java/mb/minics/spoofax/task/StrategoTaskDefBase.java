package mb.minics.spoofax.task;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.pie.api.TaskDef;
import mb.stratego.common.StrategoException;
import mb.stratego.common.StrategoRuntime;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Provider;
import java.io.Serializable;

/**
 * Invokes a Stratego strategy.
 */
public abstract class StrategoTaskDefBase<I extends Serializable, O extends @Nullable Serializable> implements TaskDef<I, O> {

    protected final Logger log;
    protected final Provider<StrategoRuntime> strategoRuntimeProvider;

    protected StrategoTaskDefBase(
        Provider<StrategoRuntime> strategoRuntimeProvider,
        LoggerFactory loggerFactory
    ) {
        this.strategoRuntimeProvider = strategoRuntimeProvider;
        this.log = loggerFactory.create(StrategoTaskDefBase.class);
    }

    @Override public String getId() {
        return getClass().getName();
    }

    protected @Nullable IStrategoTerm callStrategy(String strategyName, @Nullable IStrategoTerm term) {
        if(term == null) {
            log.error("Task failed, got no input.");
            return null;
        }

        final StrategoRuntime strategoRuntime = strategoRuntimeProvider.get();
        final @Nullable IStrategoTerm result;
        try {
            result = strategoRuntime.invoke(strategyName, term);;
        } catch (StrategoException e) {
            log.error("Executing Stratego strategy '{}' failed with input: {}", e, strategyName, term);
            return null;
        }
        if(result == null) {
            log.error("Executing Stratego strategy '{}' failed with input: {}", strategyName, term);
            return null;
        }
        return result;
    }
}
