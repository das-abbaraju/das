package com.picsauditing.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityAdaptor {
	private VelocityEngine velocityEngine;

	public VelocityAdaptor() {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		try {
			velocityEngine = new VelocityEngine(props);
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(VelocityAdaptor.class);
			logger.error("failed to create VelocityAdaptor");
		}
	}

	public String merge(String template, Map<String, Object> data) throws IOException {
		StringWriter result = new StringWriter();
		VelocityContext velocityContext = new VelocityContext(data);
		velocityEngine.setProperty(VelocityEngine.SET_NULL_ALLOWED, true);
		data.put("pics_dateTool", new DateTool());
		velocityEngine.evaluate(velocityContext, result, "pics-template-engine", template);
		return result.toString();
	}

	public static String mergeTemplate(String template, Map<String, Object> data) throws Exception {
		VelocityAdaptor adaptor = new VelocityAdaptor();
		return adaptor.merge(template, data);
	}
}
