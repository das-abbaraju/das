package com.picsauditing.util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class VelocityAdaptor {
	private VelocityEngine velocityEngine;
    Logger logger = LoggerFactory.getLogger(VelocityAdaptor.class);

	public VelocityAdaptor() {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		try {
			velocityEngine = new VelocityEngine(props);
		} catch (Exception e) {
			logger.error("failed to create VelocityAdaptor");
		}
	}

	public VelocityAdaptor(Properties properties) {
		try {
			velocityEngine = new VelocityEngine(properties);
		} catch (Exception e) {
			logger.error("failed to create VelocityAdaptor");
		}
	}

	public String merge(String template, Map<String, Object> data) throws IOException, TemplateParseException {
		StringWriter result = new StringWriter();
		VelocityContext velocityContext = getVelocityContext(data);
		data.put("pics_dateTool", new DateTool());
		try {
			velocityEngine.evaluate(velocityContext, result, "pics-template-engine", template);
		} catch (ParseErrorException e) {
			throw new TemplateParseException("Failed to parse the template. See Caused by below for line and column:\n" + headFragment(template) , e, template);
		}
		return result.toString();
	}

	public static String mergeTemplate(String template, Map<String, Object> data) throws IOException, TemplateParseException {
		VelocityAdaptor adaptor = new VelocityAdaptor();
		return adaptor.merge(template, data);
	}

	private String headFragment(String value) {
		int fragmentSize = 500;
		if (value != null && value.length() > fragmentSize) {
			return "[Fragment]\n" + value.substring(0, fragmentSize) + "\n ...";
		}
		return value;
	}

    public String mergeTemplateAndData(String templateName, Map<String, Object> data, String templateFolderPath) throws IOException, TemplateParseException {
        StringWriter writer = new StringWriter();

        InputStreamReader reader = getInputStreamReaderForTemplatePath(templateFolderPath + "/" + templateName);
        VelocityContext velocityContext = getVelocityContext(data);

        velocityEngine.evaluate(velocityContext, writer, templateName, reader);

        return writer.toString();
    }

    private VelocityContext getVelocityContext(Map<String, Object> data) {
        VelocityContext velocityContext = new VelocityContext(data);
        velocityEngine.setProperty(VelocityEngine.SET_NULL_ALLOWED, true);
        return velocityContext;
    }

    private InputStreamReader getInputStreamReaderForTemplatePath(String fullPathToTemplate) throws IOException {
        InputStream input = new FileInputStream(fullPathToTemplate);
        if (input == null) {
            throw new IOException("Template file not found at: " + fullPathToTemplate);
        }
        return new InputStreamReader(input);
    }

}
