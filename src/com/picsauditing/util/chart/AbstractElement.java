package com.picsauditing.util.chart;

abstract public class AbstractElement {
	
	/**
	 * Append ( name="value") to the xml StringBuilder
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, String value) {
		if (value != null)
			xml.append(" ").append(name).append("=\"").append(value).append("\"");
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, boolean value) {
		xml.append(" ").append(name).append("=\"").append(value ? 1 : 0).append("\"");
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, float value) {
		xml.append(" ").append(name).append("=\"").append(value).append("\"");
	}
}
