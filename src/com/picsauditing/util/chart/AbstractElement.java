package com.picsauditing.util.chart;

import org.apache.commons.lang3.StringEscapeUtils;

abstract public class AbstractElement {

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * 
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, String value) {
		if (value != null) {
			value = StringEscapeUtils.escapeXml(value);

			xml.append(" ").append(name).append("='").append(value).append("'");
		}
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * 
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, Boolean value) {
		if (value != null)
			xml.append(" ").append(name).append("='").append(value ? 1 : 0).append("'");
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * 
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, Float value) {
		if (value != null)
			xml.append(" ").append(name).append("='").append(value).append("'");
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * 
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, Integer value) {
		if (value != null)
			xml.append(" ").append(name).append("='").append(value).append("'");
	}
}
