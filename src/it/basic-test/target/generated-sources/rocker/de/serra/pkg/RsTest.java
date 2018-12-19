package de.serra.pkg;

import de.serra.condorcat.rocker.InputStreamRockerOutputFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWrap;
import java.io.IOException;
import java.io.InputStream;


@SuppressFBWarnings(value = "USBR_UNNECESSARY_STORE_BEFORE_RETURN", justification = "generated code")
@EqualsAndHashCode(callSuper = true)
public class RsTest extends RsWrap {
	private final String title;
	private final String content;

	public RsTest(String title, String content) {
		super(new Response() {

			@Override
			public Iterable<String> head() {
				return new RsEmpty().head();
			}

			@Override
			 public InputStream body() throws IOException {
				return new Test()
						.title(title)
						.content(content)
						.render(new InputStreamRockerOutputFactory()).getStream();
			}
		});
		this.title = title;
		this.content = content;
	}

	public String title() {
		return title;
	}

	public String content() {
		return content;
	}
}
