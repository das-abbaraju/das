package com.picsauditing.PICS;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.AuditType;

/**
 * @see Inputs not sure what the difference is
 */
public class Utilities {
	/*
	 * History 5/631/05 jj - added getCountrySelect() 1/6/05 jj - added
	 * getBGColor() 1/1/05 jj - moved getMenuTag method from ContractorBean.java
	 * to here and made static
	 * 
	 */
	static final String AUTO_SUBMIT = "submit();";
	static final String NO_ON_CHANGE_SCRIPT = "";
	static final boolean ARRAY_WITH_VALUES = true;
	static final boolean ARRAY_WITHOUT_VALUES = false;
	static final boolean MULTIPLE = true;
	static final boolean SINGLE = false;
	static final String[] COUNTRY_ARRAY = { "- Country -", "USA", "Afganistan", "Albania",
			"Algeria", "American Samoa", "Andorra", "Angola", "Anguila",
			"Antarctica", "Antigua and Barbuda", "Argentina", "Armenia",
			"Aruba", "Australia", "Austria", "Azerjaijan", "Bahamas",
			"Bahrain", "Bangladesh", "Barbados", "Belarus", "Belguim",
			"Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
			"Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil",
			"British Indian Ocean territory", "Brunei Darussalam", "Bulgaria",
			"Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada",
			"Cape Verde", "Cayman Islands", "Central African Republic", "Chad",
			"Chile", "China", "Christmas Island", "Cocos (Keeling) Islands",
			"Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica",
			"Cote d'Ivoire (Ivory Coast)", "Croatia (Hrvatska)", "Cuba",
			"Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica",
			"Dominican Republic", "East Timor", "Ecuador", "Egypt",
			"El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
			"Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland",
			"France", "French Guiana", "French Polynesia",
			"French Southern Territories", "Gabon", "Gambia", "Georgia",
			"Germany", "Ghana", "Greece", "Greenland", "Grenada", "Guadaloupe",
			"Guam", "Guatamala", "Guinea-Bissau", "Guinea", "Guyana", "Haiti",
			"Heard and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
			"Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland",
			"Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan",
			"Kenya", "Kiribati", "Korea (north)", "Korea (south)", "Kuwait",
			"Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia",
			"Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia",
			"Madagascar", "Malasia", "Malawi", "Maldives", "Mali", "Malta",
			"Marshal Islands", "Martinique", "Mauritania", "Maurritius",
			"Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
			"Montserrat", "Morocco", "Mozambique", "Mynamar", "Namibia",
			"Nauru", "Nepal", "Netherland Antilles", "Netherlands",
			"New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria",
			"Niue", "Norfolk Island", "Northern Marianas Islands", "Norway",
			"Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea",
			"Paraguay", "Peru", "Philippines", "Pitcairn", "Poland",
			"Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania",
			"Russian Federation", "Rwanda", "Saint Helena",
			"Saint Kitts and Nevis", "Saint Lucia",
			"Saint Pierre and Miquelon", "Saint Vincent and the Grenadines",
			"Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
			"Senegal", "Seychelles", "Sierra Leone", "Singapore",
			"Slovak Republic", "Slovenia", "Solomon Islands", "Somalia",
			"South Africa", "South Georgia and the South Sandwich Islands",
			"Spain", "Sri Lanka", "Sudan", "Suriname",
			"Svalbard and Jan Mayen Islands", "Swaziland", "Sweden",
			"Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania",
			"Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobego",
			"Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
			"Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
			"United Kingdom", "Uruguay", "USA", "Uzbekistan", "Vanuatu",
			"Vatican City", "Venezuela", "Vietnam", "Virgin Islands (British)",
			"Virgin Islands (US)", "Wallis and Futuna Islands",
			"Western Sahara", "Yemen", "Yugoslavia", "Zaire", "Zambia",
			"Zimbabwe" };

	static final String[] STATE_ARRAY = { "AL", "Alabama", "AK", "Alaska",
			"AZ", "Arizona", "AR", "Arkansas", "CA", "California", "CO",
			"Colorado", "CT", "Connecticut", "DE", "Delaware", "FL", "Florida",
			"GA", "Georgia", "HI", "Hawaii", "ID", "Idaho", "IL", "Illinois",
			"IN", "Indiana", "IA", "Iowa", "KS", "Kansas", "KY", "Kentucky",
			"LA", "Louisiana", "ME", "Maine", "MD", "Maryland", "MA",
			"Massachusetts", "MI", "Michigan", "MN", "Minnesota", "MS",
			"Mississippi", "MO", "Missouri", "MT", "Montana", "NE", "Nebraska",
			"NV", "Nevada", "NH", "New Hampshire", "NJ", "New Jersey", "NM",
			"New Mexico", "NY", "New York", "NC", "North Carolina", "ND",
			"North Dakota", "OH", "Ohio", "OK", "Oklahoma", "OR", "Oregon",
			"PA", "Pennsylvania", "RI", "Rhode Island", "SC", "South Carolina",
			"SD", "South Dakota", "TN", "Tennessee", "TX", "Texas", "UT",
			"Utah", "VT", "Vermont", "VA", "Virginia", "WA", "Washington",
			"DC", "Washington D.C.", "WV", "West Virginia", "WI", "Wisconsin",
			"WY", "Wyoming" };

	// public static String getStateSelect(String name, String classType, String
	// selectedState) throws Exception {
	// return inputSelect2(name, classType, selectedState, STATE_ARRAY);
	// }//getStateSelect

	public static String convertStateLongToShort(String state) {
		if (state.length() <= 2)
			return state;
		ArrayList<String> tempAL = new ArrayList<String>();
		tempAL.addAll(Arrays.asList(STATE_ARRAY));
		for (int i = 1; i < tempAL.size(); i += 2) {
			if (state.equalsIgnoreCase((String) tempAL.get(i)))
				return (String) tempAL.get(i - 1);
		}// for
		return state;
	}// convertStateLongToShort

	public static String convertStateShortToLong(String state) {
		state = convertStateLongToShort(state);
		if (state.length() > 2)
			return state;
		ArrayList<String> tempAL = new ArrayList<String>();
		tempAL.addAll(Arrays.asList(STATE_ARRAY));
		for (int i = 0; i < tempAL.size(); i += 2) {
			if (state.equalsIgnoreCase((String) tempAL.get(i)))
				return (String) tempAL.get(i + 1);
		}// for
		return state;
	}// convertStateShortToLong

	public static String getCity(String address) {
		try {
			int i = address.indexOf('\r');
			int j = address.indexOf(',', i);
			return address.substring(i, j);
		} catch (Exception e) {
			return "";
		}// catch
	}// getCity

	public static String getState(String address) {
		try {
			int i = address.lastIndexOf(' ');
			int j = address.lastIndexOf(' ', i - 1);
			return address.substring(j + 1, i);
		} catch (Exception e) {
			return "";
		}// catch
	}// getState

	public static String getZip(String address) {
		try {
			int i = address.lastIndexOf(' ');
			return address.substring(i + 1);
		} catch (Exception e) {
			return "";
		}// catch
	}// getZip

	public static String getCountrySelect(String name, String classType,
			String selectedCountry) throws Exception {
		return inputSelect(name, classType, selectedCountry, COUNTRY_ARRAY);
	}// getStateCountry

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
	}// escapeNewLines

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

	public static boolean isValidInteger(String number) {
		try {
			Integer.parseInt(number);
			return true;
		}// try
		catch (Exception e) {
			return false;
		}// catch
	}// isValidInteger

	public static boolean isValidInteger2(String number, int min, int max) {
		try {
			int i = Integer.parseInt(number);
			return ((i >= min) && (i <= max));
		}// try
		catch (Exception e) {
			return false;
		}// catch
	}// isValidInteger2

	public static boolean isValidFloat(String number) {
		try {
			Float.parseFloat(number);
			return true;
		}// try
		catch (Exception e) {
			return false;
		}// catch
	}// isValidFloat

	public static boolean isValidFloat2(String number, float min, float max) {
		try {
			float f = Float.parseFloat(number);
			return ((f >= min) && (f <= max));
		}// try
		catch (Exception e) {
			return false;
		}// catch
	}// isValidFloat2

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

	public static String printErrorMessages(java.util.Vector errorMessages) {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < errorMessages.size(); i++) {
			out.append(errorMessages.elementAt(i) + "<br>");
		}// for
		return out.toString();
	}// printErrorMessages

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
	}// arrayContains

	public static String checkSelected(String s1, String s2) {
		if ((null == s1) || (null == s2))
			return "";
		if (s1.equals(s2))
			return " selected";
		else
			return "";
	}// checkSelected

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

	public static String inputSelectSubmit(String name, String classType,
			String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, AUTO_SUBMIT, ARRAY_WITHOUT_VALUES, SINGLE, null,
				null);
	}// inputSelect

	public static String inputSelectFirst(String name, String classType,
			String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				firstOption, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES,
				SINGLE, null, null);
	}// inputSelectFist

	public static String inputSelect2(String name, String classType,
			String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITH_VALUES, SINGLE,
				null, null);
	}// inputSelect2

	public static String inputSelect2First(String name, String classType,
			String selectedOption, String[] optionsArray, String firstValue,
			String firstOption) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				firstOption, firstValue, NO_ON_CHANGE_SCRIPT,
				ARRAY_WITH_VALUES, SINGLE, null, null);
	}

	public static String inputSelect2FirstSubmit(String name, String classType,
			String selectedOption, String[] optionsArray, String firstValue,
			String firstOption) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				firstOption, firstValue, AUTO_SUBMIT, ARRAY_WITH_VALUES,
				SINGLE, null, null);
	}

	public static String inputMultipleSelect(String name, String classType,
			String size, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name, classType, selectedOption, optionsArray,
				null, null, NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES,
				MULTIPLE, size, null);
	}

	public static String inputMultipleSelectMultiples(String name,
			String classType, String size, String[] selectedOptions,
			String[] optionsArray) {
		return inputSelectAll(name, classType, null, optionsArray, null, null,
				NO_ON_CHANGE_SCRIPT, ARRAY_WITHOUT_VALUES, MULTIPLE, size,
				selectedOptions);
	}

	public static String inputMultipleSelect2Multiples(String name,
			String classType, String size, String[] selectedOptions,
			String[] optionsArray) {
		return inputSelectAll(name, classType, null, optionsArray, null, null,
				NO_ON_CHANGE_SCRIPT, ARRAY_WITH_VALUES, MULTIPLE, size,
				selectedOptions);
	}// inputMultipleSelect2Multiples

	public static String inputMultipleSelect2MultiplesScript(String name,
			String classType, String size, String[] selectedOptions,
			String[] optionsArray, String onChangeScript) {
		return inputSelectAll(name, classType, null, optionsArray, null, null,
				onChangeScript, ARRAY_WITH_VALUES, MULTIPLE, size,
				selectedOptions);
	}// inputMultipleSelect2Multiples

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

	public static String getMenuTag(HttpServletRequest req, String thisLink,
			String thisPage, String id, String queryString, String thisQuery,
			String title) {
		if (null == thisLink)
			thisLink = "";
		if (null == thisQuery)
			thisQuery = "";
		String ctxPath = req.getContextPath();
		thisLink = ctxPath + "/" + thisLink;
		if (null != id)
			if ("".equals(queryString))
				queryString = "id=" + id;
			else
				queryString += "&id=" + id;
		if (thisPage.equals(thisLink) && thisQuery.equals(queryString))
			return "<span class=redMain>" + title + "</span>";
		if ("".equals(queryString))
			return "<a class=blueMain href=" + thisLink + ">" + title + "</a>";
		return "<a class=blueMain href=" + thisLink + "?" + queryString + ">"
				+ title + "</a>";
	}// getMenuTag

	public static String getBGColor(int count) {
		if ((count % 2) == 0)
			return " bgcolor=\"#FFFFFF\"";
		else
			return "";
	}// getBGColor

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

	public static String getTextAreaInput(String name, String classType,
			String value, String cols, String rows) {
		return "<textarea class=" + classType + " name=" + name + " rows="
				+ rows + " cols=" + cols + ">" + value + "</textarea>";
	}// getTextAreaInput

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

	public static String convertPercentToDecimal(String num) {
		float temp = 0;
		try {
			if (-1 != num.indexOf("%"))
				num = num.substring(0, num.indexOf("%") - 1);
			temp = Float.parseFloat(num);
		} catch (Exception e) {
			temp = 0;
		}// catch
		if (temp > 2)
			temp = temp / 100;
		return Float.toString(temp);
	}// convertPercentToDecimal

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

	public static String printRequest(
			javax.servlet.http.HttpServletRequest request) {
		Enumeration e = request.getParameterNames();
		StringBuffer temp = new StringBuffer();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String value = request.getParameter(name);
			temp.append(name).append("=").append(value).append(",");
		}// while
		return temp.toString();
	}// printRequest

	public static String convertNullString(String str, String def) {
		if (str == null)
			return def;
		else
			return str;
	}

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
}
