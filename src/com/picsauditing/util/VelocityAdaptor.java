package com.picsauditing.util;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;

public class VelocityAdaptor {
	public static String mergeTemplate(String template, Map<String, Object> data) throws Exception {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		StringWriter result = new StringWriter();

		VelocityEngine velocityEngine = new VelocityEngine(props);

		data.put("pics_dateTool", new DateTool());
		VelocityContext velocityContext = new VelocityContext(data);
		velocityEngine.evaluate(velocityContext, result, "pics-template-engine", template);

		return result.toString();
	}
}
