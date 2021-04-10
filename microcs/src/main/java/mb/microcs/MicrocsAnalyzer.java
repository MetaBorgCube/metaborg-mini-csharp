package mb.microcs;

import mb.log.api.LoggerFactory;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Statix-based semantic analyzer for Mini-C#.
 */
public class MicrocsAnalyzer extends StatixAnalyzer {

    public MicrocsAnalyzer(
        StatixSpec spec,
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        super(spec, termFactory, loggerFactory);
    }
}
