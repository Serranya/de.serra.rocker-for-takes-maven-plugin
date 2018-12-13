package de.serra.rft;

import com.fizzed.rocker.compiler.RockerUtil;
import com.fizzed.rocker.model.TemplateModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileWritingResponseClass implements ResponseClass {
	private final TemplateModel model;
	private final Path dir;

	public FileWritingResponseClass(TemplateModel model, Path baseDir) {
		this.model = model;
		this.dir = baseDir;
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
			new DefaultResponseClass(model, out).generate();
		}
	}
}
