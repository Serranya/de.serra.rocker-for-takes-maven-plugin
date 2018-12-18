package de.serra.rft;

import com.fizzed.rocker.model.Argument;
import com.fizzed.rocker.model.JavaImport;
import com.fizzed.rocker.model.TemplateModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class DefaultResponseClass implements ResponseClass {
	private final TemplateModel model;
	private final OutputStream out;

	public DefaultResponseClass(TemplateModel model, OutputStream out) {
		this.model = model;
		this.out = out;
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
		w.append("import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;\n");
		w.append("import lombok.EqualsAndHashCode;\n");
		w.append("import org.takes.Response;\n");
		w.append("import org.takes.rs.RsEmpty;\n");
		w.append("import org.takes.rs.RsWrap;\n");
		w.append("import java.io.ByteArrayInputStream;");
		w.append("import java.io.IOException;\n");
		w.append("import java.io.InputStream;\n");

		w.append("\n");

		for (JavaImport i : model.getImports()) {
			w.append("import ").append(i.getStatement()).append(";\n");
		}

		w.append("\n");

		w.append("@SuppressFBWarnings(value = \"USBR_UNNECESSARY_STORE_BEFORE_RETURN\", "
				+ "justification = \"generated code\")\n");
		w.append("@EqualsAndHashCode(callSuper = true)\n");
		w.append("public class Rs").append(model.getName()).append(" extends RsWrap {\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\tprivate final ").append(arg.getType()).append(" ").append(arg.getName()).append(";\n");
		}

		w.append("\n");

		w.append("\tpublic Rs").append(model.getName()).append("(");
		boolean firstCArg = true;
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			if (!firstCArg) {
				w.append(", ");
			} else {
				firstCArg = false;
			}
			w.append(arg.getType()).append(" ").append(arg.getName());
		}
		w.append(") {\n");
		w.append("\t\tsuper(new Response() {\n");
		w.append("\n");
		w.append("\t\t\t@Override\n");
		w.append("\t\t\tpublic Iterable<String> head() {\n");
		w.append("\t\t\t\treturn new RsEmpty().head();\n");
		w.append("\t\t\t}\n");
		w.append("\n");
		w.append("\t\t\t@Override\n");
		w.append("\t\t\t public InputStream body() throws IOException {\n");
		w.append("\t\t\t\treturn new ").append("ByteArrayInputStream(\n");
		w.append("\t\t\t\t\tnew ").append(model.getName()).append("()\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\t\t\t\t\t\t\t.").append(arg.getName()).append("(").append(arg.getName()).append(")\n");
		}
		w.append("\t\t\t\t\t\t\t.render(ArrayOfByteArraysOutput.FACTORY).toByteArray());\n");
		w.append("\t\t\t}\n");
		w.append("\t\t});\n");
		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\t\tthis.").append(arg.getName()).append(" = ").append(arg.getName()).append(";\n");
		}
		w.append("\t}\n");

		for (Argument arg : model.getArgumentsWithoutRockerBody()) {
			w.append("\n");
			w.append("\tpublic ").append(arg.getType()).append(" ").append(arg.getName()).append("() {\n");
			w.append("\t\treturn ").append(arg.getName()).append(";\n");
			w.append("\t}\n");
		}

		w.append("}\n");
	}
}
