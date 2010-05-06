package com.picsauditing.util.chart;

abstract public class AbstractElement {
	
	/**
	 * Append ( name="value") to the xml StringBuilder
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, String value) {
		// Escaping the single quote doesn't work when it's wrapped by single quotes.
		if (value != null)
			//xml.append(" ").append(name).append("='").append(value.replaceAll("'", "\\'")).append("'");
			xml.append(" ").append(name).append("=\"").append(value).append("\"");
	}

	/**
	 * Append ( name="value") to the xml StringBuilder
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
	 * @param xml
	 * @param name
	 * @param value
	 */
	protected static void append(StringBuilder xml, String name, Integer value) {
		if (value != null)
			xml.append(" ").append(name).append("='").append(value).append("'");
	}
}
