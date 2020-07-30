package de.serra.rft;

import com.fizzed.rocker.ContentType;
import com.fizzed.rocker.model.Argument;
import com.fizzed.rocker.model.JavaImport;
import com.fizzed.rocker.model.TemplateModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class DefaultResponseClass implements ResponseClass {
	private final TemplateModel model;
	private final OutputStream out;
	private final Set<Flag> flags;
	private final Iterable<String> annotations;

	public enum Flag {
		SUPPRESS_FB_WARNINGS, LOMBOK_EQUALS_AND_HASCODE;
	}

	public DefaultResponseClass(TemplateModel model, OutputStream out, Iterable<String> annotations) {
		this(model, out, Collections.emptySet(), annotations);
	}

	public DefaultResponseClass(TemplateModel model, OutputStream out, Set<Flag> flags, Iterable<String> annotations) {
		this.model = model;
		this.out = out;
		this.flags = flags;
		this.annotations = annotations;
	}

	@Override
	public void generate() throws IOException {
		if (model.hasRockerBodyArgument()) {
			return;
		}

		try (Writer w = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
			createSourceTemplate(w);
			w.flush();
		}
	}

	private void createSourceTemplate(Writer w) throws IOException {
		if (model.getPackageName() != null && !"".equals(model.getPackageName())) {
			w.append("package ").append(model.getPackageName()).append(";").append("\n");
		}

		w.append("\n");
		w.append("import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;\n");
		if (flags.contains(Flag.SUPPRESS_FB_WARNINGS)) {
			w.append("import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;\n");
		}
		if (flags.contains(Flag.LOMBOK_EQUALS_AND_HASCODE)) {
			w.append("import lombok.EqualsAndHashCode;\n");
		}
		w.append("import org.takes.Response;\n");
		w.append("import org.takes.rs.RsWithStatus;\n");
		if (ContentType.HTML == model.getContentType()) {
			w.append("import org.takes.rs.RsWithType;\n");
		}
		w.append("import org.takes.rs.RsWrap;\n");
		w.append("import java.io.IOException;\n");
		w.append("import java.io.InputStream;\n");

		w.append("\n");

		for (JavaImport i : model.getImports()) {
			w.append("import ").append(i.getStatement()).append(";\n");
		}

		w.append("\n");

		if (flags.contains(Flag.SUPPRESS_FB_WARNINGS)) {
			w.append("@SuppressFBWarnings(value = \"USBR_UNNECESSARY_STORE_BEFORE_RETURN\", "
					+ "justification = \"generated code\")\n");
		}
		if (flags.contains(Flag.LOMBOK_EQUALS_AND_HASCODE)) {
			w.append("@EqualsAndHashCode(callSuper = true)\n");
		}
		for (String annotation : annotations) {
			w.append(annotation).append("\n");
		}
		w.append("public class Rs").append(model.getName()).append(" extends RsWrap {\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\tprivate final ").append(arg.getType()).append(" ").append(arg.getName()).append(";\n");
		}

		w.append("\n");

		w.append("\tpublic Rs").append(model.getName()).append("(");
		w.append("final Response res");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append(", ");
			w.append("final ").append(arg.getType()).append(" ").append(arg.getName());
		}
		w.append(") {\n");
		w.append("\t\tsuper(\n");
		w.append("\t\t\t\tnew Rs").append(model.getName()).append("Response(\n");
		w.append("\t\t\t\t\t\tres,\n");
		w.append("\t\t\t\t\t\tnew ").append(model.getName()).append("()\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\t\t\t\t\t\t\t.").append(arg.getName()).append("(").append(arg.getName()).append(")\n");
		}
		w.append("\t\t\t\t\t\t\t.render(ArrayOfByteArraysOutput.FACTORY).asInputStream()\n");
		w.append("\t\t\t\t)\n");
		w.append("\t\t);\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\t\tthis.").append(arg.getName()).append(" = ").append(arg.getName()).append(";\n");
		}
		w.append("\t}\n");
		w.append("\n");

		w.append("\tpublic Rs").append(model.getName()).append("(");
		boolean firstCArg = true;
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			if (!firstCArg) {
				w.append(", ");
			} else {
				firstCArg = false;
			}
			w.append("final ").append(arg.getType()).append(" ").append(arg.getName());
		}
		w.append(") {\n");
		if (ContentType.HTML == model.getContentType()) {
			w.append("\t\tthis(new RsWithType(new RsWithStatus(200), \"text/html\")");
		} else {
			w.append("\t\tthis(new RsWithStatus(200)");
		}
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append(", ").append(arg.getName());
		}
		w.append(");\n");
		w.append("\t}\n");

		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\n");
			w.append("\tpublic ").append(arg.getType()).append(" ").append(arg.getName()).append("() {\n");
			w.append("\t\treturn ").append(arg.getName()).append(";\n");
			w.append("\t}\n");
		}

		w.append("\n");
		// @todo maybe extend from custom model and put this class there just once
		for (String annotation : annotations) {
			w.append("\t").append(annotation).append("\n");
		}
		w.append("\tprivate static class Rs").append(model.getName()).append("Response")
				.append(" implements Response {\n");
		w.append("\t\tprivate final Response head;\n");
		w.append("\t\tprivate final InputStream body;\n");
		w.append("\n");
		w.append("\t\tRs").append(model.getName()).append("Response(Response head, InputStream body) {\n");
		w.append("\t\t\tthis.head = head;\n");
		w.append("\t\t\tthis.body = body;\n");
		w.append("\t\t}\n");
		w.append("\n");
		w.append("\t\t@Override\n");
		w.append("\t\tpublic Iterable<String> head() throws IOException {\n");
		w.append("\t\t\treturn head.head();\n");
		w.append("\t\t}\n");
		w.append("\n");
		w.append("\t\t@Override\n");
		w.append("\t\tpublic InputStream body() throws IOException {\n");
		w.append("\t\t\treturn body;\n");
		w.append("\t\t}\n");
		w.append("\t}\n");

		w.append("}\n");
	}
}
