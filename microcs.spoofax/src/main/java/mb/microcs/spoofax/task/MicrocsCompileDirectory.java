package mb.microcs.spoofax.task;

import mb.common.message.KeyedMessagesBuilder;
import mb.common.message.Severity;
import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.ResourceService;
import mb.resource.hierarchical.HierarchicalResource;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.match.AllResourceMatcher;
import mb.resource.hierarchical.match.FileResourceMatcher;
import mb.resource.hierarchical.match.PathResourceMatcher;
import mb.resource.hierarchical.match.ResourceMatcher;
import mb.resource.hierarchical.match.path.ExtensionsPathMatcher;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.microcs.spoofax.MicrocsScope;
import mb.microcs.spoofax.task.MicrocsParse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@MicrocsScope
public class MicrocsCompileDirectory implements TaskDef<MicrocsCompileDirectory.Args, CommandFeedback> {
    public static class Args implements Serializable {
        final ResourcePath dir;

        public Args(ResourcePath dir) {
            this.dir = dir;
        }

        @Override public boolean equals(@Nullable Object obj) {
            if(this == obj) return true;
            if(obj == null || getClass() != obj.getClass()) return false;
            final Args other = (Args)obj;
            return dir.equals(other.dir);
        }

        @Override public int hashCode() {
            return Objects.hash(dir);
        }

        @Override public String toString() {
            return dir.toString();
        }
    }

    private final MicrocsParse parse;
    private final MicrocsListDefNames listDefNames;
    private final ResourceService resourceService;

    @Inject
    public MicrocsCompileDirectory(MicrocsParse parse, MicrocsListDefNames listDefNames, ResourceService resourceService) {
        this.parse = parse;
        this.listDefNames = listDefNames;
        this.resourceService = resourceService;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, Args input) throws IOException {
        final ResourcePath dir = input.dir;

        final ResourceMatcher matcher = new AllResourceMatcher(new FileResourceMatcher(), new PathResourceMatcher(new ExtensionsPathMatcher("mcs")));
        final HierarchicalResource directory = context.require(dir, ResourceStampers.modifiedDir(matcher));

        final StringBuffer sb = new StringBuffer();
        final KeyedMessagesBuilder messagesBuilder = new KeyedMessagesBuilder();
        sb.append("[\n  ");
        final AtomicBoolean first = new AtomicBoolean(true);
        directory.list(matcher).forEach((file) -> {
            final ResourcePath filePath = file.getPath();
            if(!first.get()) {
                sb.append(", ");
            }
            final Supplier<Result<IStrategoTerm, JSGLR1ParseException>> astSupplier = parse.createAstSupplier(filePath);
            context.require(listDefNames, astSupplier)
                .ifElse(sb::append, (e) -> {
                    sb.append("[]");
                    messagesBuilder.addMessage("Listing definition names for '" + file + "' failed", e, Severity.Error, filePath);
                });
            sb.append('\n');
            first.set(false);
        });
        sb.append(']');

        final ResourcePath generatedPath = dir.appendSegment("_defnames.aterm");
        final HierarchicalResource generatedResource = resourceService.getHierarchicalResource(generatedPath);
        generatedResource.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        context.provide(generatedResource, ResourceStampers.hashFile());

        return CommandFeedback.of(messagesBuilder.build(), ShowFeedback.showFile(generatedPath));
    }

    @Override public Task<CommandFeedback> createTask(Args input) {
        return TaskDef.super.createTask(input);
    }
}
