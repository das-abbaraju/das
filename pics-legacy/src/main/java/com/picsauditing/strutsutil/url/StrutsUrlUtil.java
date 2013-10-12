package com.picsauditing.strutsutil.url;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;

public class StrutsUrlUtil {

	private static final String encoding = "UTF-8";

	private static final Logger logger = LoggerFactory.getLogger(StrutsUrlUtil.class);

	public static String buildParameterSubstring(String name, String value) {
		return buildParameterSubstring(name, value, '=');
	}

	public static String buildParameterSubstring(String name, String value, char separator) {
		StringBuilder builder = new StringBuilder();
		builder.append(translateAndEncode(name));
		builder.append(separator);
		builder.append(translateAndEncode(value));
		return builder.toString();
	}

	public static String buildIdParameterSubstring(String value) {
		StringBuilder builder = new StringBuilder();
		builder.append(translateAndEncode(value));
		return builder.toString();
	}

	/**
	 * Append a trailing slash, if the URI is missing one.
	 * 
	 * @param uri
	 * @return
	 */
	public static StringBuilder appendTrailingSlash(StringBuilder uri) {
		if (uri == null || uri.length() == 0) {
			return uri;
		}

		if (uri.charAt(uri.length() - 1) != '/') {
			uri.append('/');
		}

		return uri;
	}

	public static String truncateLeadingSlash(String uri) {
		if (Strings.isNotEmpty(uri) && uri.startsWith("/")) {
			return uri.substring(1);
		}

		return uri;
	}

	public static StringBuilder appendTrailingQuestionMark(StringBuilder uri) {
		if (uri == null || uri.length() == 0) {
			return uri;
		}

		if (uri.charAt(uri.length() - 1) == '/') {
			uri.replace(uri.length() - 1, uri.length(), "?");
		} else if (uri.charAt(uri.length() - 1) != '?') {
			uri.append('?');
		}

		return uri;
	}

	/*
	 * The methods listed below were moved out of struts DefaultUrlHandler
	 */

	/**
	 * Translates any script expressions using
	 * {@link com.opensymphony.xwork2.util.TextParseUtil#translateVariables} and
	 * encodes the URL using {@link java.net.URLEncoder#encode} with the
	 * encoding specified in the configuration.
	 * 
	 * @param input
	 * @return the translated and encoded string
	 */
	public static String translateAndEncode(String input) {
		String translatedInput = translateVariable(input);
		try {
			return URLEncoder.encode(translatedInput, encoding);
		} catch (UnsupportedEncodingException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not encode URL parameter '" + input + "', returning value un-encoded");
			}
			return translatedInput;
		}
	}

	public static String translateAndDecode(String input) {
		String translatedInput = translateVariable(input);
		try {
			return URLDecoder.decode(translatedInput, encoding);
		} catch (UnsupportedEncodingException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not encode URL parameter '" + input + "', returning value un-encoded");
			}

			return translatedInput;
		}
	}

	private static String translateVariable(String input) {
		ValueStack valueStack = ServletActionContext.getContext().getValueStack();
		return TextParseUtil.translateVariables(input, valueStack);
	}

}
