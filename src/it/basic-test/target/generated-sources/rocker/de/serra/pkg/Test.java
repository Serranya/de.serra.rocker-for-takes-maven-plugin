package de.serra.pkg;

import java.io.IOException;
import com.fizzed.rocker.ForIterator;
import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.RockerOutput;
import com.fizzed.rocker.runtime.DefaultRockerTemplate;
import com.fizzed.rocker.runtime.PlainTextUnloadedClassLoader;

/*
 * Auto generated code to render template de/serra/pkg/Test.rocker.html
 * Do not edit this file. Changes will eventually be overwritten by Rocker parser!
 */
@SuppressWarnings("unused")
public class Test extends com.fizzed.rocker.runtime.DefaultRockerModel {

    static public com.fizzed.rocker.ContentType getContentType() { return com.fizzed.rocker.ContentType.HTML; }
    static public String getTemplateName() { return "Test.rocker.html"; }
    static public String getTemplatePackageName() { return "de.serra.pkg"; }
    static public String getHeaderHash() { return "1243303407"; }
    static public long getModifiedAt() { return 1544712452834L; }
    static public String[] getArgumentNames() { return new String[] { "title", "content" }; }

    // argument @ [1:2]
    private String title;
    // argument @ [1:2]
    private String content;

    public Test title(String title) {
        this.title = title;
        return this;
    }

    public String title() {
        return this.title;
    }

    public Test content(String content) {
        this.content = content;
        return this;
    }

    public String content() {
        return this.content;
    }

    static public Test template(String title, String content) {
        return new Test()
            .title(title)
            .content(content);
    }

    @Override
    protected DefaultRockerTemplate buildTemplate() throws RenderingException {
        // optimized for convenience (runtime auto reloading enabled if rocker.reloading=true)
        return com.fizzed.rocker.runtime.RockerRuntime.getInstance().getBootstrap().template(this.getClass(), this);
    }

    static public class Template extends com.fizzed.rocker.runtime.DefaultRockerTemplate {

        // <!DOCTYPE html>\n<html>\n\t<head>\n\t\t<meta charset=\"utf-8\">\n\t\t<title>
        static private final byte[] PLAIN_TEXT_0_0;
        // </title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/styles.css\" />\n\t</head>\n\t<body>\n\t\t<main>\n\t\t\t
        static private final byte[] PLAIN_TEXT_1_0;
        // \n\t\t</main>\n\t</body>\n</html>\n\n
        static private final byte[] PLAIN_TEXT_2_0;

        static {
            PlainTextUnloadedClassLoader loader = PlainTextUnloadedClassLoader.tryLoad(Test.class.getClassLoader(), Test.class.getName() + "$PlainText", "UTF-8");
            PLAIN_TEXT_0_0 = loader.tryGet("PLAIN_TEXT_0_0");
            PLAIN_TEXT_1_0 = loader.tryGet("PLAIN_TEXT_1_0");
            PLAIN_TEXT_2_0 = loader.tryGet("PLAIN_TEXT_2_0");
        }

        // argument @ [1:2]
        protected final String title;
        // argument @ [1:2]
        protected final String content;

        public Template(Test model) {
            super(model);
            __internal.setCharset("UTF-8");
            __internal.setContentType(getContentType());
            __internal.setTemplateName(getTemplateName());
            __internal.setTemplatePackageName(getTemplatePackageName());
            this.title = model.title();
            this.content = model.content();
        }

        @Override
        protected void __doRender() throws IOException, RenderingException {
            // PlainText @ [1:37]
            __internal.aboutToExecutePosInTemplate(1, 37);
            __internal.writeValue(PLAIN_TEXT_0_0);
            // ValueExpression @ [7:10]
            __internal.aboutToExecutePosInTemplate(7, 10);
            __internal.renderValue(title, false);
            // PlainText @ [7:16]
            __internal.aboutToExecutePosInTemplate(7, 16);
            __internal.writeValue(PLAIN_TEXT_1_0);
            // ValueExpression @ [12:4]
            __internal.aboutToExecutePosInTemplate(12, 4);
            __internal.renderValue(content, false);
            // PlainText @ [12:12]
            __internal.aboutToExecutePosInTemplate(12, 12);
            __internal.writeValue(PLAIN_TEXT_2_0);
        }
    }

    private static class PlainText {

        static private final String PLAIN_TEXT_0_0 = "<!DOCTYPE html>\n<html>\n\t<head>\n\t\t<meta charset=\"utf-8\">\n\t\t<title>";
        static private final String PLAIN_TEXT_1_0 = "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/styles.css\" />\n\t</head>\n\t<body>\n\t\t<main>\n\t\t\t";
        static private final String PLAIN_TEXT_2_0 = "\n\t\t</main>\n\t</body>\n</html>\n\n";

    }

}