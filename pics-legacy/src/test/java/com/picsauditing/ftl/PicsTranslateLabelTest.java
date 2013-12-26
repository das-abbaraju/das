package com.picsauditing.ftl;

import com.picsauditing.util.Strings;
import freemarker.core.Environment;
import freemarker.template.*;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@UseReporter(DiffReporter.class)
public class PicsTranslateLabelTest {
    private static Configuration cfg;
    private Template template;
    private Map root;
    private Map parameters;
    private Map s;
    private Writer out;

    static {
        try {
            cfg = new Configuration();

            File cwd = new File(".");
            String templatePath = "src" + Strings.FILE_SEPARATOR
                    + "main" + Strings.FILE_SEPARATOR
                    + "resources" + Strings.FILE_SEPARATOR
                    + "template" + Strings.FILE_SEPARATOR
                    + "pics";

            // to run from Intellij, we're going to do some trickery. If it is maven, the absolute path will be the
            // module. If it isn't the module, we'll assume you're running from your IDE the traditioinal way, in which
            // case the root is most likely to be the project root.
            if (!cwd.getAbsolutePath().endsWith("pics-legacy" + Strings.FILE_SEPARATOR + ".")) {
                templatePath = "pics-legacy" + Strings.FILE_SEPARATOR + templatePath;
            }

            cfg.setDirectoryForTemplateLoading(new File(templatePath));
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            cfg.setIncompatibleEnhancements("2.3.19");
        } catch (Exception e) {
            // tests will fail below
        }
    }

    @Before
    public void setup() throws Exception {
        out = new StringWriter();
        root = new HashMap();
        parameters = new HashMap();
        s = new HashMap();

        parameters.put("id", "test_id");
        root.put("parameters", parameters);
        root.put("s", s);

        template = cfg.getTemplate("translate-label.ftl");
    }

    @Test
    public void test_ReturnsLabelHtmlWithTranslatedText() throws Exception {
        parameters.put("label", "Test Label");
        s.put("if", new StrutsTrueIf());
        s.put("elseif", new StrutsFalseIf());
        s.put("text", new StrutsTextHasValue());

        template.process(root, out);

        Approvals.verify(out.toString());
    }

    @Test
    public void test_ReturnsLabelHtmlWithLabelKeyAsText() throws Exception {
        parameters.put("label", "Test Label");
        s.put("if", new StrutsTrueIf());
        s.put("elseif", new StrutsFalseIf());
        s.put("text", new StrutsTextEmptyValue());

        template.process(root, out);

        Approvals.verify(out.toString());
    }

    @Test
    public void test_ReturnsLabelHtmlWithLabelKeyAsTextWhenHasKeyIsFalse() throws Exception {
        parameters.put("label", "Test Label");
        s.put("if", new StrutsFalseIf());
        s.put("elseif", new StrutsTrueIf());

        template.process(root, out);

        Approvals.verify(out.toString());
    }

    @Test
    public void test_ReturnsNothingWhenLabelKeyIsEmpty() throws Exception {
        parameters.put("label", "");
        s.put("if", new StrutsFalseIf());
        s.put("elseif", new StrutsFalseIf());

        template.process(root, out);

        Approvals.verify(out.toString());
    }

    @Test
    public void test_ReturnsLabelHtmlWithPropertyValueAsText() throws Exception {
        parameters.put("label", null);
        s.put("if", new StrutsTrueIf());
        s.put("elseif", new StrutsFalseIf());
        s.put("text", new StrutsTextEmptyValue());
        s.put("property", new StrutsPropertyValue());

        template.process(root, out);

        Approvals.verify(out.toString());
    }

    private class StrutsTrueIf implements TemplateDirectiveModel {
        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            body.render(env.getOut());
        }
    }
    private class StrutsFalseIf implements TemplateDirectiveModel {
        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            // do nothing
        }
    }

    private class StrutsTextHasValue implements TemplateDirectiveModel {
        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            env.getOut().write("Translated Text");
        }
    }

    private class StrutsTextEmptyValue implements TemplateDirectiveModel {
        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            env.getOut().write("");
        }
    }

    private class StrutsPropertyValue implements TemplateDirectiveModel {
        @Override
        public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            env.getOut().write("Test Property");
        }
    }

}
