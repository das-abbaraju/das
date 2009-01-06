package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;

/**
 * @see Inputs not sure what the difference is
 */
public class Utilities {
	
	static final String NO_ON_CHANGE_SCRIPT = "";
	static final boolean ARRAY_WITH_VALUES = true;
	static final boolean ARRAY_WITHOUT_VALUES = false;
	static final boolean MULTIPLE = true;
	static final boolean SINGLE = false;

	public static String escapeHTML(String value) {
		if (value == null)
			return "";
		StringBuffer strval = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			switch (ch) {
			case '\'':
				strval.append("\\'");
				break;
			case '"':
				strval.append("\"");
				break;
			case '&':
				strval.append("&");
				break;
			case '%':
				strval.append(" ");
				break;
			case '<':
				strval.append("<");
				break;
			case '>':
				strval.append(">");
				break;
			case '\n':
				strval.append("<br>");
				break;
			default:
				if (ch > 126)
					strval.append("&#" + String.valueOf(ch) + ";");
				else
					strval.append(ch);
				break;
			}// switch
		}// for
		// BJ 2-21-05 java can not reccognize ms apostrpohes, so must work
		// backwards
		// loop through to find where ms apostrpohes were converted to question
		// marks, change to standard apostrophes
		// int substart = strval.lastIndexOf("&#?;");
		// strval.replace(substart,substart+3,"'");

		return strval.toString();
	}// escapeHTML

	public static String escapeNewLines(String value) {
		if (value == null)
			return "";
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if ('\n' == value.charAt(i))
				temp.append("<br>");
			else
				temp.append(value.charAt(i));
		}// for
		return temp.toString();
	}

	/**
	 * Replaces single quotes in a string with two single quotes. This formats
	 * it properly for use in a SQL statement.
	 * 
	 * @param value
	 *            the string to format.
	 * @return the formatted string.
	 */
	public static String escapeQuotes(String value) {
		if (value == null)
			return "";
		StringBuffer strval = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			// Found a single quote; replace it with 2 for the SQL statement
			if ((ch == 146) || (ch == '\'') || (ch == '%'))
				strval.append("''");
			// if find double quote, change to single quote since html input
			// can't handle double quotes BJ 3-7-05
			else if (ch == 92)
				strval.append("\\\\");
			else if (ch == '"')
				strval.append("''");
			else
				// Just append the char to the strval for the complete string
				strval.append(ch);
		}// for
		return strval.toString();
	}// escapeQuotes

	public static boolean isValidEmail(String email) {
		boolean result = false;
		if (null == email)
			return false;
		int index = email.indexOf("@");
		if (index > 0) {
			int pindex = email.indexOf(".", index);
			if ((pindex > index + 1) && (email.length() > pindex + 1))
				result = true;
		}// if
		return result;
	}// isValidEmail

	public static boolean arrayContains(String[] array, String value) {
		boolean isIncluded = false;
		if (array == null || value == null) {
			return false;
		}// if
		for (int i = 0; i < array.length; i++) {
			if (value.equals(array[i])) {
				isIncluded = true;
				break;
			}// if
		}// for
		return isIncluded;
	}

	@SuppressWarnings("unchecked")
	public static String inputSelectAll(String name, String classType,
			String selectedOption, String[] optionsArray, String firstOption,
			String firstValue, String onChangeScript, boolean arrayWithValues,
			boolean multiple, String size, String[] selectedOptions) {
		ArrayList<String> tempAL = new ArrayList<String>();
		ArrayList<String> selectedOptionsAL = new ArrayList<String>();
		if (arrayWithValues && (null != firstValue))
			tempAL.add(firstValue);
		if (null != firstOption)
			tempAL.add(firstOption);
		if (null != selectedOptions)
			selectedOptionsAL.addAll(Arrays.asList(selectedOptions));
		if ((null != selectedOption) && (selectedOption.length() != 0))
			selectedOptionsAL.add(selectedOption);
		tempAL.addAll(Arrays.asList(optionsArray));
		StringBuffer temp = new StringBuffer();
		temp.append(" <select id=\"").append(name).append("\" name=\"").append(
				name).append("\" class=").append(classType);
		if (!NO_ON_CHANGE_SCRIPT.equals(onChangeScript))
			temp.append(" onChange=\"" + onChangeScript + "\"");
		if (null != size)
			temp.append(" size=").append(size);
		if (multiple)
			temp.append(" multiple");
		temp.append(">\n");
		ListIterator li = tempAL.listIterator();
		while (li.hasNext()) {
			temp.append(" <option");
			if (arrayWithValues) {
				String value = (String) li.next();
				temp.append(" value=\"").append(value).append("\"");
				if (selectedOptionsAL.contains(value))
					temp.append(" selected");
			}// if
			String option = (String) li.next();
			if (!arrayWithValues)
				if (selectedOptionsAL.contains(option))
					temp.append(" selected");
			temp.append(">").append(option).append("</option>\n");
		}// while
		temp.append("</select>\n");
		return temp.toString();
	}// inputSelectAll

	public static String inputSelect(String name, String classType,
			String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES, SINGLE,
				null, null);
	}// inputSelect

	public static String inputSelectFirst(String name, String classType,
			String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				firstOption, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES,
				SINGLE, null, null);
	}

	public static String inputSelect2(String name, String classType,
			String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITH_VALUES, SINGLE,
				null, null);
	}

	public static String inputSelect2First(String name, String classType,
			String selectedOption, String[] optionsArray, String firstValue,
			String firstOption) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				firstOption, firstValue, NO_ON_CHANGE_SCRIPT,
				ARRAY_WITH_VALUES, SINGLE, null, null);
	}

	public static String inputMultipleSelect(String name, String classType,
			String size, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES,
				MULTIPLE, size, null);
	}

	public static String inputMultipleSelect2Multiples(String name,
			String classType, String size, String[] selectedOptions,
			String[] optionsArray) {
		return inputSelectAll(name, classType, null, optionsArray, null, null,
				NO_ON_CHANGE_SCRIPT, ARRAY_WITH_VALUES, MULTIPLE, size,
				selectedOptions);
	}

	public static String inputMultipleSelect2MultiplesScript(String name,
			String classType, String size, String[] selectedOptions,
			String[] optionsArray, String onChangeScript) {
		return inputSelectAll(name, classType, null, optionsArray, null, null,
				onChangeScript, ARRAY_WITH_VALUES, MULTIPLE, size,
				selectedOptions);
	}

	public static String checkedBox(String s1) {
		if ("Yes".equals(s1) || "Y".equals(s1))
			return "checked";
		else
			return "";
	}// checkedBox

	public static String checked(String s1, String s2) {
		if ((s1 != null) && (s1.equals(s2)))
			return "checked";
		else
			return "";
	}// checked

	public static String getBGColor(int count) {
		if ((count % 2) == 0)
			return " bgcolor=\"#FFFFFF\"";
		else
			return "";
	}

	public static String getYesNoRadio(String name, String selected) {
		StringBuffer temp = new StringBuffer();
		temp.append("<label><input name=").append(name).append(
				" type=radio value=Yes");
		if ("Yes".equals(selected))
			temp.append(" checked");
		temp.append(">Yes</label><label><input name=").append(name).append(
				" type=radio value=No");
		if ("No".equals(selected))
			temp.append(" checked");
		temp.append(">No</label>\n");
		return temp.toString();
	}// getYesNoReadio

	@SuppressWarnings("unchecked")
	public static String getRadioInput(String name, String classType,
			String selected, String[] optionsArray) {
		StringBuffer temp = new StringBuffer();
		ArrayList<String> optionsAL = new ArrayList<String>();
		if (null != optionsArray)
			optionsAL.addAll(Arrays.asList(optionsArray));
		ListIterator li = optionsAL.listIterator();
		while (li.hasNext()) {
			String option = (String) li.next();
			temp.append("<input name=").append(name).append(" class=").append(
					classType);
			temp.append(" type=radio value=\"").append(option).append("\"");
			if (option.equals(selected))
				temp.append(" checked");
			temp.append("><nobr>").append(option).append("</nobr>");
		}// while
		return temp.toString();
	}// getReadioInput

	public static String getCheckBoxInput(String name, String classType,
			String value) {
		return "<input type=checkbox class='" + classType + "' name='" + name
				+ "' value='Y' " + checked(value, "Y") + ">";
	}

	public static String getCheckBoxInput(String name, String classType,
			boolean value) {
		return "<input type=checkbox class=" + classType + " name=" + name
				+ (value ? " checked" : "") + ">";
	}

	public static String intToDB(String num) {
		try {
			Integer.parseInt(num);
		} catch (Exception ex) {
			return "0";
		}// catch
		return num;
	}// intToDB

	public static String getIsChecked(String checkBox) {
		if ("Yes".equals(checkBox))
			return "Yes";
		return "No";
	}// getIsChecked

	public static String convertNullString(String str, String def) {
		if (str == null)
			return def;
		else
			return str;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> requestParamsToMap(
			HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			String value = request.getParameter(name);
			params.put(name, value);
		}
		return params;
	}

	public static int getInt(BasicDynaBean row, String columnName) {
		Object columnValue = row.get(columnName);
		if (columnValue == null){
			return 0;
		}
		return Integer.parseInt(columnValue.toString());

	}

	public static float getAverageEMR(String year1, String year2, String year3, String year4) {
		Float avgRateFloat = convertToFloat(year1)+ convertToFloat(year2)+ convertToFloat(year3)+ convertToFloat(year4)/ 3;
		avgRateFloat = (float) Math.round(1000 * avgRateFloat) / 1000;
		return avgRateFloat;
	}
	
	public static float convertToFloat(String year1) {
		if(year1 == null)
			return 0.0f;
		return Float.valueOf(year1).floatValue();
		
	}
}
