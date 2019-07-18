package de.serra.rft;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import com.fizzed.rocker.compiler.RockerConfiguration;
import com.fizzed.rocker.compiler.RockerUtil;
import com.fizzed.rocker.compiler.TemplateParser;
import com.fizzed.rocker.model.TemplateModel;
import com.fizzed.rocker.runtime.ParserException;

import de.serra.rft.DefaultResponseClass.Flag;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class RockerResponseMojo extends AbstractMojo {
	/**
	 * Whether the plugin should let the build fail if it encounters an error.
	 */
	@Parameter(property = "rft.failOnError", defaultValue = "true")
	protected boolean failOnError;

	/**
	 * Directory containing templates. The base directory to search -- which is also
	 * how their "package" name is determined.
	 */
	@Parameter(property = "rft.templateDirectory", defaultValue = "${project.build.sourceDirectory}")
	protected File templateDirectory;

	/**
	 * Directory to output generated Java source files.
	 */
	@Parameter(property = "rft.outputDirectory",
			defaultValue = "${project.build.directory}/generated-sources/rocker", required = true)
	protected File outputDirectory;

	/**
	 * The regex to recognize rocker template files.
	 */
	@Parameter(property = "rft.suffixRegex", defaultValue = ".*\\.rocker\\.(raw|html)$")
	protected String suffixRegex;

	/**
	 * Whether {@code @EqualsAndHashCode} from lombok should be generated.
	 */
	@Parameter(property = "rft.useLombok", defaultValue = "true")
	protected boolean useLombok;

	/**
	 * Whether {@code @SuppressFBWarnings} annotations should be generated.
	 */
	@Parameter(property = "rft.useSupressFbWarnings", defaultValue = "true")
	protected boolean useSupressFbWarnings;

	/**
	 * Adds the specified annotation to all generated classes. Can be used to add a custom {@code @Generated} annotation.
	 */
	@Parameter(property = "rft.annotations")
	protected List<String> annotations;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (templateDirectory == null) {
			throw new MojoExecutionException("Property templateDirectory cannot be null/empty");
		}
		if (this.outputDirectory == null) {
			throw new MojoExecutionException("Property outputDirectory cannot be null/empty");
		}

		Collection<File> allFiles = RockerUtil.listFileTree(templateDirectory);
		Predicate<String> suffixPredicate =
				Pattern.compile(suffixRegex == null ? "rocker.html" : suffixRegex).asMatchPredicate();
		Collection<File> templateFiles =
				allFiles.stream().filter(f -> suffixPredicate.test(f.getName())).collect(Collectors.toList());

		getLog().info("Parsing " + templateFiles.size() + " rocker template files");

		int errors = 0;
		int generated = 0;
		TemplateParser parser = new TemplateParser(new RockerConfiguration());
		parser.getConfiguration().setTemplateDirectory(templateDirectory);
		parser.getConfiguration().setOutputDirectory(outputDirectory);

		for (File f : templateFiles) {
			TemplateModel model = null;
			try {
				// parse model
				model = parser.parse(f);
			} catch (ParserException e) {
				getLog().error("Parsing failed for " + f + ":[" + e.getLineNumber() + "," + e.getColumnNumber()
						+ "] " + e.getMessage());
				if (failOnError) {
					throw new MojoExecutionException("Error while parsing templates", e);
				}
				errors++;
			} catch (IOException e) {
				getLog().error("Unable to parse template", e);
				if (failOnError) {
					throw new MojoExecutionException("Error while parsing templates", e);
				}
				errors++;
			}
			try {
				Set<Flag> flags = EnumSet.noneOf(Flag.class);
				if (useLombok) {
					flags.add(Flag.LOMBOK_EQUALS_AND_HASCODE);
				}
				if (useSupressFbWarnings) {
					flags.add(Flag.SUPPRESS_FB_WARNINGS);
				}
				if (model != null) {
					new FileWritingResponseClass(model, outputDirectory.toPath(), flags, annotations).generate();
					generated++;
				}
			} catch (IOException e) {
				getLog().error("Unable to generate responseClass", e);
				if (failOnError) {
					throw new MojoExecutionException("Unable to generate responseClass", e);
				}
				errors++;
			}
		}

		getLog().info("Generated " + generated + " rocker response java source files");

		if (errors > 0 && failOnError) {
			throw new RuntimeException("Caught " + errors + " errors.");
		}
	}
}
