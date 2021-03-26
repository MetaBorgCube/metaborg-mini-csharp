package mb.minics;

import mb.log.api.LoggerFactory;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Statix-based semantic analyzer for Mini-C#.
 */
public class MinicsAnalyzer extends StatixAnalyzer {

    public MinicsAnalyzer(
        StatixSpec spec,
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        super(spec, termFactory, loggerFactory);
    }
}
