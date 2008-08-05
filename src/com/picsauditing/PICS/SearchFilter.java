package com.picsauditing.PICS;

import java.util.HashMap;
import java.util.Map;

@Deprecated
/**
 * primarily used with certificates
 */
public class SearchFilter {
	String name = "";
	public static final String[] DEFAULTS = {
		"s_name","- Name - ",
		"s_accountName","- Name - ",
		"s_industry","- Industry -",
		"s_type","Contractor",
		"s_trade","0",
		"a_performedBy","- Performed By -",
		"s_zip","- Zip -",
		"s_city","- City -",
		"s_state","- State -",
		"s_opID",SearchBean.DEFAULT_GENERAL_VALUE
	};
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
