package com.picsauditing.PICS;

import java.util.HashMap;
import java.util.Map;

public class SearchFilter {
	String name = "";
	public static final String[] DEFAULTS = {
		"s_name","- Name - ",
		"s_accountName","- Name - ",
		"s_industry","- Industry -",
		"s_type","Contractor",
		"s_trade",TradesBean.DEFAULT_SELECT_TRADE_ID,
		"a_performedBy",TradesBean.DEFAULT_PERFORMED_BY,
		"s_zip","- Zip -",
		"s_city","- City -",
		"s_state","- State -",
		"s_opID",SearchBean.DEFAULT_GENERAL_VALUE
	};

	/*	public static final String DEFAULT_AUDIT_STATUS = "- Audit Status -";
	public static final String DEFAULT_AUDITOR = "- Auditor -";
	public static final String DEFAULT_AUDITOR_ID = "0";
	public static final String DEFAULT_STATUS = "- Status -";
	public static final String DEFAULT_GENERAL = "- Operator -";
	public static final String DEFAULT_AUDIT_LOCATION = "- Audit Location -";
	public static final String DEFAULT_VISIBLE = "- Visible -";
	public static final String DEFAULT_GENERAL_VALUE = "-2";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_INVOICED_STATUS = "- Invoiced Status -";
	public static final String DEFAULT_LICENSED_IN = "- Licensed In -";
	public static final String DEFAULT_LICENSED_IN_ID = com.picsauditing.PICS.pqf.QuestionTypeList.DEFAULT_SELECT_QUESTION_ID;
	public static final String DEFAULT_FLAG_STATUS = "- Flag Status -";
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
*/
		Map<String, String> params = null;
	
	public HashMap<String,String> getDefaultMap(){
		HashMap<String,String> defaultMap = new HashMap<String,String>();
		for (int i=0;i<DEFAULTS.length;i+=2)
			defaultMap.put(DEFAULTS[i],DEFAULTS[i+1]);
		return defaultMap;
	}

	public boolean isSet(String param, String value){
		if (value == null || "".equals(value))
			return false;
		if (getDefaultMap().containsKey(param))
			return (!getDefaultMap().get(param).equals(value));
		return true;
	}

	public void setParams(Map<String,String> requestParams){
		params = new HashMap<String,String>();
		for (String name : requestParams.keySet()){
			if (name.startsWith("s_")){
				String value = requestParams.get(name);
				if (isSet(name,value))
					params.put(name, value);
			}
			if (("orderBy".equals(name)))
				params.put(name, requestParams.get(name));
		}
	}
	public String get(String paramName){
		return params.get(paramName);
	}
	public String getInputValue(String paramName){
		if (has(paramName))
			return params.get(paramName);
		if (getDefaultMap().containsKey(paramName))
			return getDefaultMap().get(paramName);
		return "";
	}
	public void set(String paramName, String value){
		if (null==params)
			params = new HashMap<String,String>();
		params.put(paramName,value);
	}
	public boolean has(String paramName){
		return params.containsKey(paramName);
	}
	public String getURLQuery(){
		String returnString = "";
		for (String param : params.keySet()){
			String value = get(param);
			returnString += "&"+param+"="+value;
		}
		return returnString;
	}
}
