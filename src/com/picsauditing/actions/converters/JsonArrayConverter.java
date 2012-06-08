package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.struts2.util.StrutsTypeConverter;
import org.jboss.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("rawtypes")
public class JsonArrayConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (ArrayUtils.isNotEmpty(values) && !Strings.isEmpty(values[0])) {
			try {
				JSONParser parser = new JSONParser();
				return parser.parse(values[0]);
			} catch (Exception e) {
				System.out.println("Error converting to request parameter 'jsonArray' to  JSONArray.");
			}
		}
		
		return null;
	}

	@Override
	public String convertToString(Map context, Object object) {
		if (object instanceof JSONArray) {
			try {
				return ((JSONArray) object).toJSONString();
			} catch (Exception ignoreThisException) {
				
			}
		}
		
		return null;
	}

}
