package mb.minics;

import mb.log.api.LoggerFactory;
import mb.statix.common.StatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Inject;

public class MinicsAnalyzerFactory {
    private final StatixSpec spec;
    private ITermFactory termFactory;
    private LoggerFactory loggerFactory;

    @Inject public MinicsAnalyzerFactory(
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        this.termFactory = termFactory;
        this.loggerFactory = loggerFactory;
        this.spec = StatixSpec.fromClassLoaderResources(MinicsAnalyzerFactory.class, "/mb/minics/minics.stx.aterm");
        // TODO: This is a FileSpec(), we need a Spec():
//        this.spec = StatixSpec.fromClassLoaderResources(MinicsAnalyzerFactory.class, "/mb/minics/src-gen/static/static-semantics.spec.aterm");
    }

    public MinicsAnalyzer create() {
        return new MinicsAnalyzer(spec, termFactory, loggerFactory);
    }
}
