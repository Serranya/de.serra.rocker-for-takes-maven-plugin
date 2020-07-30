package de.serra.rft;
import de.serra.rft.DefaultResponseClass.Flag;

import com.fizzed.rocker.compiler.RockerUtil;
import com.fizzed.rocker.model.TemplateModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class FileWritingResponseClass implements ResponseClass {
	private final TemplateModel model;
	private final Path dir;
	private final Set<Flag> flags;
	private final Iterable<String> annotations;

	public FileWritingResponseClass(TemplateModel model, Path baseDir, Iterable<String> annotations) {
		this(model, baseDir, Collections.emptySet(), annotations);
	}

	public FileWritingResponseClass(TemplateModel model, Path baseDir, Set<Flag> flags, Iterable<String> annotations) {
		this.model = model;
		this.dir = baseDir;
		this.flags = flags;
		this.annotations = annotations;
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	@Override
	public void generate() throws IOException {
		if (model.hasRockerBodyArgument()) {
			return;
		}

		Path outDirPath = Optional.ofNullable(RockerUtil.packageNameToPath(model.getPackageName()))
				.map(dir::resolve).orElse(dir);
		if (Files.exists(outDirPath)) {
			Files.createDirectories(outDirPath);
		}

		try (OutputStream out = Files.newOutputStream(outDirPath.resolve("Rs" + model.getName() + ".java"))) {
			new DefaultResponseClass(model, out, flags, annotations).generate();
		}
	}
}
