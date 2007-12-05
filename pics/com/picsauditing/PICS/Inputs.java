package com.picsauditing.PICS;

import java.util.*;
// String escapeQuotes(String value)
// String escapeHTML(String value)
// boolean isValidInteger(String number)
// boolean isValidInteger2(String number, int min, int max)
// boolean isValidFloat(String number)
// boolean isValidFloat2(String number, float min, float max)
// boolean isValidEmail(String email)
// String printErrorMessages(Vector errorMessages)
// boolean arrayContains(String[] array, String value)

public class Inputs {
/*	History
	5/631/05 jj - added getCountrySelect()
	1/6/05 jj - added getBGColor()
	
*/
	static final boolean AUTO_SUBMIT = true;
	static final boolean NO_AUTO_SUBMIT = false;
	static final boolean ARRAY_WITH_VALUES = true;
	static final boolean ARRAY_WITHOUT_VALUES = false;
	static final boolean MULTIPLE = true;
	static final boolean SINGLE = false;
	static final String[] YES_NO_ARRAY = {"Yes","No"};
	public static final String[] YES_NO_NA_ARRAY = {"Yes","No","NA"};
	static final String[] COUNTRY_ARRAY = {"USA","Afganistan","Albania","Algeria","American Samoa","Andorra","Angola","Anguila","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerjaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belguim","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana",
			"Bouvet Island","Brazil","British Indian Ocean territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Congo","Cook Islands","Costa Rica","Cote d'Ivoire (Ivory Coast)","Croatia (Hrvatska)","Cuba","Cyprus",
			"Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","East Timor","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Greenland",
			"Grenada","Guadaloupe","Guam","Guatamala","Guinea-Bissau","Guinea","Guyana","Haiti","Heard and McDonald Islands","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Korea (north)","Korea (south)","Kuwait","Kyrgyzstan",
			"Laos","Latvia","Lebanon","Lesotho","Liberia","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malasia","Malawi","Maldives","Mali","Malta","Marshal Islands","Martinique","Mauritania","Maurritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montserrat","Morocco","Mozambique","Mynamar","Namibia",
			"Nauru","Nepal","Netherland Antilles","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","Northern Marianas Islands","Norway","Oman","Pakistan","Palau","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russian Federation",
			"Rwanda","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Seychelles","Sierra Leone","Singapore","Slovak Republic","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia","Spain","Sri Lanka","Sudan","Suriname",
			"Svalbard and Jan Mayen Islands","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Togo","Tokelau","Tonga","Trinidad and Tobego","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Vanuatu","Vatican City",
			"Venezuela","Vietnam","Virgin Islands (British)","Virgin Islands (US)","Wallis and Futuna Islands","Western Sahara","Yemen","Yugoslavia","Zaire","Zambia","Zimbabwe"};

	public 	static final String[] STATE_ARRAY = {
			"AL","Alabama","AK","Alaska","AB","Alberta","AZ", "Arizona","AR","Arkansas",
			"BC","British Columbia","CA","California","CO","Colorado","CT","Connecticut",
			"DE","Delaware","FL","Florida","GA","Georgia","GU","Guam","HI","Hawaii",
			"ID","Idaho","IL","Illinois","IN","Indiana","IA","Iowa","KS","Kansas","KY","Kentucky",
			"LA","Louisiana","ME","Maine","MB","Manitoba","MD","Maryland","MA","Massachusetts",
			"MI","Michigan","MN","Minnesota","MS","Mississippi","MO","Missouri","MT","Montana",
			"NE","Nebraska","NV","Nevada","NB","New Brunswick","NL","Newfoundland",
			"NH","New Hampshire","NJ","New Jersey","NM","New Mexico","NY","New York",
			"NC","North Carolina","ND","North Dakota","NS","Nova Scotia","NU","Nunavut","OH","Ohio",
			"OK","Oklahoma","ON","Ontario","OR","Oregon","PA","Pennsylvania",
			"PE","Prince Edward Is.","PR","Puerto Rico","QC","Quebec","RI","Rhode Island",
			"SK","Saskatchewan","SC","South Carolina","SD","South Dakota","TN","Tennessee",
			"TX","Texas","UT","Utah","VT","Vermont","VA","Virginia","WA","Washington",
			"DC","Washington D.C.","WV","West Virginia","WI","Wisconsin","WY","Wyoming","YT","Yukon"};

	public static ArrayList getStateArrayList() {
		ArrayList<String> tempAL = new ArrayList<String>();
		tempAL.addAll(Arrays.asList(Inputs.STATE_ARRAY));
		return tempAL;
	}//getStateArrayList
	
	public static String getStateSelect(String name, String classType, String selectedState) throws Exception {
		return inputSelect2(name, classType, selectedState, STATE_ARRAY);
	}//getStateSelect

	public static String getLongStateSelect(String name, String classType, String selectedState) throws Exception {
		int length = STATE_ARRAY.length;
		ArrayList<String> tempAL = new ArrayList<String>();
		for (int i=1;i < length;i+=2)
			tempAL.add(STATE_ARRAY[i]);
		return inputSelect(name, classType, selectedState, (String[])tempAL.toArray(new String[0]));
	}//getStateSelect

	public static String getCountrySelect(String name, String classType, String selectedCountry) throws Exception {
		return inputSelect(name, classType, selectedCountry, COUNTRY_ARRAY);
	}//getStateCountry

	public static String getHourSelect(String name, String classType, String selectedOption) {
		ArrayList<String> tempAL = new ArrayList<String>();
		tempAL.add("--");
		for (int i=1;i<=12;i++) {
			String temp = Integer.toString(i);
			tempAL.add(temp);
			tempAL.add(temp+":30");
		}//for
		return inputSelect(name, classType, selectedOption,(String[])tempAL.toArray(new String[0]));
	}//getHourSelect

	public static String getAMPMSelect(String name, String classType, String selectedOption) {
		String[] AMPM_ARRAY = {"--","am","pm"};
		return inputSelect(name, classType, selectedOption,AMPM_ARRAY);
	}//getAMPMSelect

	public static boolean arrayContains(String[] array, String value) {
		boolean isIncluded = false;
		if (array == null || value == null) {
			return false;
		}//if
		for (int i = 0; i < array.length; i++) {
			if (value.equals(array[i])) {
				isIncluded = true;
				break;
			}//if
		}//for
		return isIncluded;
	}//arrayContains
	
	public static String checkSelected(String s1, String s2) {
		if ((null == s1) || (null == s2))
			return "";
		if (s1.equals(s2))
			return " selected";
		else
			return "";
	}//checkSelected
	
	public static String inputSelectAll(String name, String classType, String selectedOption, String[] optionsArray,
			String firstOption, String firstValue, boolean autoSubmit, boolean arrayWithValues, boolean multiple, 
			String size, String[] selectedOptions) {
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
		temp.append(" <select name=\"").append(name).append("\" class=").append(classType);
		if (autoSubmit)
			temp.append(" onChange=\"submit()\"");
		if (null != size)
			temp.append(" size=").append(size);
		if (multiple)
			temp.append(" multiple");
		temp.append(">\n");
		ListIterator li = tempAL.listIterator();
		while (li.hasNext()) {
			temp.append(" <option");
			if (arrayWithValues) {
				String value = (String)li.next();
				temp.append(" value=\"").append(value).append("\"");
				if (selectedOptionsAL.contains(value))
					temp.append(" selected");
			}//if
			String option = (String)li.next();
			if (!arrayWithValues)
				if (selectedOptionsAL.contains(option))
					temp.append(" selected");		
			
			temp.append(">").append(option).append("</option>\n");
		}//while
		temp.append("</select>\n");
		return temp.toString();
	}//inputSelectAll
	
	public static String inputSelect(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}//inputSelect

	public static String inputSelectSubmit(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}//inputSelect

	public static String inputSelectFirst(String name, String classType, String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}//inputSelectFist

	public static String inputSelectFirstSubmit(String name, String classType, String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,null,AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}//inputSelectFist

	public static String inputSelect2(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}//inputSelect2

	public static String inputSelect2First(String name, String classType, String selectedOption, String[] optionsArray, 
											String firstValue, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,firstValue,NO_AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}//inputSelect2First

	public static String inputSelect2FirstSubmit(String name, String classType, String selectedOption, String[] optionsArray, 
											String firstValue, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,firstValue,AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}//inputSelect2FirstSubmit

	public static String inputSelect2Submit(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}//inputSelect2Submit

	public static String inputMultipleSelect(String name, String classType, String size, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,MULTIPLE,size,null);
	}//inputMultipleSelect

	public static String inputMultipleSelectMultiples(String name, String classType, String size, String[] selectedOptions, String[] optionsArray) {
		return inputSelectAll(name,classType,null,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,MULTIPLE,size,selectedOptions);
	}//inputMultipleSelectMulitples

	public static String inputMultipleSelect2Multiples(String name, String classType, String size, String[] selectedOptions, String[] optionsArray) {
		return inputSelectAll(name,classType,null,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITH_VALUES,MULTIPLE,size,selectedOptions);
	}//inputMultipleSelect2Multiples
	
	public static String checkedBox(String s1) {
		if ((s1 != null) && (s1.equals("Y")))
			return "checked";
		else
			return "";
	}//checkedBox

	public static String getChecked(String s1, String s2) {
		if ((s1 != null) && (s1.equals(s2)))
			return "checked";
		else
			return "";
	}//getChecked

	public static String getYesNoRadio(String name, String classType, String selected) {
		return getRadioInput(name, classType, selected, YES_NO_ARRAY);
	}//getYesNoReadio
	
	public static String getYesNoRadioWithEvent(String name, String classType, String selected,
			String event, String handler, String params) {
		return getRadioInputWithEvent(name, classType, selected, YES_NO_ARRAY, event, handler, params);
	}//getYesNoReadio

	public static String getYesNoNARadio(String name, String classType, String selected) {
		return getRadioInput(name, classType, selected, YES_NO_NA_ARRAY);
	}//getYesNoNAReadio

	public static String getRadioInput(String name, String classType, String selected, String[] optionsArray) {
		StringBuffer temp = new StringBuffer();
		ArrayList<String> optionsAL = new ArrayList<String>();
		if (null != optionsArray)
			optionsAL.addAll(Arrays.asList(optionsArray));
		ListIterator li = optionsAL.listIterator();
		while (li.hasNext()) {
			String option=(String)li.next();
			temp.append("<nobr><input name=").append(name).append(" class=").append(classType);
			temp.append(" type=radio value=\"").append(option).append("\"");
			if (option.equals(selected))
				temp.append(" checked");
			temp.append(">").append(option).append("</nobr>");
		}//while
		return temp.toString();
	}//getReadioInput
	
	public static String getRadioInputWithEvent(String name, String classType, String selected, String[] optionsArray,
			String event, String handler, String params) {
		StringBuffer temp = new StringBuffer();
		ArrayList<String> optionsAL = new ArrayList<String>();
		if (null != optionsArray)
			optionsAL.addAll(Arrays.asList(optionsArray));
		ListIterator li = optionsAL.listIterator();
		while (li.hasNext()) {
			String option=(String)li.next();
			temp.append("<nobr><input name=").append(name).append(" class=").append(classType);
			temp.append(" type=radio value=\"").append(option).append("\"");
			if (option.equals(selected))
				temp.append(" checked ");
			temp.append(event + "=" + handler + "(" + params + ") ");
			temp.append(">").append(option).append("</nobr>");
		}//while
		return temp.toString();
	}//getReadioInput

	public static String getRadioInputWithOptions(String name, String classType, String selected, String[] optionsArray, String[] valueArray) {
		StringBuffer temp = new StringBuffer();
		ArrayList<String> optionsAL = new ArrayList<String>();
		if (null != optionsArray)
			optionsAL.addAll(Arrays.asList(optionsArray));
		ListIterator li = optionsAL.listIterator();
		int i = 0;
		while (li.hasNext()) {
			String option=(String)li.next();
			temp.append("<nobr><input name=").append(name).append(" class=").append(classType);
			temp.append(" type=radio value=\"").append(option).append("\"");
			if (option.equals(selected))
				temp.append(" checked");
			temp.append(">").append((String)valueArray[i++]).append("</nobr>");
		}//while
		return temp.toString();
	}//getReadioInput
	
	
	public static String getTextAreaInput(String name, String classType, String value, String cols, String rows) {
		return "<textarea class="+classType+" name="+name+" rows="+rows+" cols="+cols+">"+
			value+"</textarea>";
	}//getTextAreaInput

	public static String getCheckBoxInput(String name, String classType, String value, String checkedValue) {
		return "<input type=checkbox class="+classType+" name="+name+" value=\""+checkedValue+"\" "+getChecked(value,checkedValue)+">";
	}//getCheckBoxInput
 
	public static String getDateInput(String name, String classType, String value, String formName) {
		return "<nobr><input name=\""+name+"\" id=\""+name+"\" type=text class=\""+classType+"\" size=8 onClick=\"cal1.select(document.forms('"+formName+"')."+name+",'"+name+"','M/d/yy','"+value+"'); return false;\" value=\""+value+"\">\n"+
			"                          <input type=image src=\"images/icon_calendar.gif\" width=18 height=15 onClick=\"cal1.select(document.forms('"+formName+"')."+name+",'"+name+"','M/d/yy','"+value+"'); return false;\"></nobr>\n";
	}//getDateInput

	public static String getDateInput2(String name, String classType, String value, String formName) {
		return "<nobr><input name=\""+name+"\" id=\""+name+"\" type=text class=\""+classType+"\" size=8 value=\""+value+"\">\n"+
			"                          <input type=image src=/images/icon_calendar.gif width=18 height=15 onClick=\"cal1.select(document.forms('"+formName+"')."+name+",'"+name+"','M/d/yy','"+value+"'); return false;\"></nobr>\n";
	}//getDateInput2

//	public static String gteStateLong(String shortState) throws Exception{
//		 int i = STATE_ARRAY.indexOf(shortState);
//		 if (1 == i)
//		 	throw new Exception("Invalid state abr.:"+shortState);
//		return STATE_ARRAY.
//	}//getStateLong
}//utilities