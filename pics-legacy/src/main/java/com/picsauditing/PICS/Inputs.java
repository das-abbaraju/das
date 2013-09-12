package com.picsauditing.PICS;

import java.util.*;

/**
 * Old list of HTML <input> methods
 * @deprecated use struts now
 */
public class Inputs {

	static final boolean AUTO_SUBMIT = true;
	static final boolean NO_AUTO_SUBMIT = false;
	static final boolean ARRAY_WITH_VALUES = true;
	static final boolean ARRAY_WITHOUT_VALUES = false;
	static final boolean MULTIPLE = true;
	static final boolean SINGLE = false;
	public static final String[] COUNTRY_ARRAY = {"- Country -", "USA","Canada","Afganistan","Albania","Algeria","American Samoa","Andorra","Angola","Anguila","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerjaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belguim","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana",
			"Bouvet Island","Brazil","British Indian Ocean territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Congo","Cook Islands","Costa Rica","Cote d'Ivoire (Ivory Coast)","Croatia (Hrvatska)","Cuba","Cyprus",
			"Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","East Timor","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Greenland",
			"Grenada","Guadaloupe","Guam","Guatamala","Guinea-Bissau","Guinea","Guyana","Haiti","Heard and McDonald Islands","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kiribati","Korea (north)","Korea (south)","Kuwait","Kyrgyzstan",
			"Laos","Latvia","Lebanon","Lesotho","Liberia","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malasia","Malawi","Maldives","Mali","Malta","Marshal Islands","Martinique","Mauritania","Maurritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montserrat","Morocco","Mozambique","Mynamar","Namibia",
			"Nauru","Nepal","Netherland Antilles","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","Northern Marianas Islands","Norway","Oman","Pakistan","Palau","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russian Federation",
			"Rwanda","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Seychelles","Sierra Leone","Singapore","Slovak Republic","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia","Spain","Sri Lanka","Sudan","Suriname",
			"Svalbard and Jan Mayen Islands","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Togo","Tokelau","Tonga","Trinidad and Tobego","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Vanuatu","Vatican City",
			"Venezuela","Vietnam","Virgin Islands (British)","Virgin Islands (US)","Wallis and Futuna Islands","Western Sahara","Yemen","Yugoslavia","Zaire","Zambia","Zimbabwe"};

	public static String getHourSelect(String name, String classType, String selectedOption) {
		ArrayList<String> tempAL = new ArrayList<String>();
		tempAL.add("--");
		for (int i=1;i<=12;i++) {
			String temp = Integer.toString(i);
			tempAL.add(temp);
			tempAL.add(temp+":30");
		}
		return inputSelect(name, classType, selectedOption,(String[])tempAL.toArray(new String[0]));
	}

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
		ListIterator<String> li = tempAL.listIterator();
		while (li.hasNext()) {
			temp.append(" <option");
			if (arrayWithValues) {
				String value = (String)li.next();
				temp.append(" value=\"").append(value).append("\"");
				if (selectedOptionsAL.contains(value))
					temp.append(" selected");
			}
			String option = (String)li.next();
			if (!arrayWithValues)
				if (selectedOptionsAL.contains(option))
					temp.append(" selected");		
			
			temp.append(">").append(option).append("</option>\n");
		}
		temp.append("</select>\n");
		return temp.toString();
	}
	
	public static String inputSelect(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}

	public static String inputSelectSubmit(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}

	public static String inputSelectFirst(String name, String classType, String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,null,NO_AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}

	public static String inputSelectFirstSubmit(String name, String classType, String selectedOption, String[] optionsArray, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,null,AUTO_SUBMIT,
			ARRAY_WITHOUT_VALUES,SINGLE,null,null);
	}

	public static String inputSelect2(String name, String classType, String selectedOption, String[] optionsArray) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,null,null,NO_AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}

	public static String inputSelect2First(String name, String classType, String selectedOption, String[] optionsArray, 
											String firstValue, String firstOption) {
		return inputSelectAll(name,classType,selectedOption,optionsArray,firstOption,firstValue,NO_AUTO_SUBMIT,
			ARRAY_WITH_VALUES,SINGLE,null,null);
	}
	
}