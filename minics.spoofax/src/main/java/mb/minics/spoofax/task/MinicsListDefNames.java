package mb.minics.spoofax.task;

import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoUtil;
import mb.minics.spoofax.MinicsScope;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import javax.inject.Provider;

@MinicsScope
public class MinicsListDefNames implements TaskDef<Supplier<? extends Result<IStrategoTerm, ?>>, Result<String, ?>> {
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject
    public MinicsListDefNames(Provider<StrategoRuntime> strategoRuntimeProvider) {
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override
    public String getId() { return "mb.minics.spoofax.task.reusable.MinicsListDefNames"; }

    @Override
    public Result<String, ?> exec(ExecContext context, Supplier<? extends Result<IStrategoTerm, ?>> astSupplier) throws Exception {
        return context.require(astSupplier)
            .mapCatching((ast) -> StrategoUtil.toString(strategoRuntimeProvider.get().invoke("list-of-def-names", ast), Integer.MAX_VALUE));
    }
}
