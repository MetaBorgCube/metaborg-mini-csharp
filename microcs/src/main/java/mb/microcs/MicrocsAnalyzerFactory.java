package mb.microcs;

import mb.log.api.LoggerFactory;
import mb.statix.common.StatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Inject;

public class MicrocsAnalyzerFactory {
    private final StatixSpec spec;
    private ITermFactory termFactory;
    private LoggerFactory loggerFactory;

    @Inject public MicrocsAnalyzerFactory(
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        this.termFactory = termFactory;
        this.loggerFactory = loggerFactory;
        this.spec = StatixSpec.fromClassLoaderResources(MicrocsAnalyzerFactory.class, "/mb/microcs/microcs.stx.aterm");
        // TODO: This is a FileSpec(), we need a Spec():
//        this.spec = StatixSpec.fromClassLoaderResources(MicrocsAnalyzerFactory.class, "/mb/microcs/src-gen/static/static-semantics.spec.aterm");
    }

    public MicrocsAnalyzer create() {
        return new MicrocsAnalyzer(spec, termFactory, loggerFactory);
    }
}
