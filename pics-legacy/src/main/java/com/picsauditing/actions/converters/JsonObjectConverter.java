package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.util.StrutsTypeConverter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class JsonObjectConverter extends StrutsTypeConverter {
	private final Logger logger = LoggerFactory.getLogger(JsonArrayConverter.class);
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (ArrayUtils.isNotEmpty(values) && !Strings.isEmpty(values[0])) {
			try {
				JSONParser parser = new JSONParser();
				return parser.parse(values[0]);
			} catch (Exception e) {
				logger.error("Error converting to request parameter 'json' to  JSONObject.");
			}
		}
						
		return null;
	}
	
	@Override
	public String convertToString(Map context, Object object) {
		if (object instanceof JSONObject) {
			try {
				return ((JSONObject) object).toJSONString();
			} catch (Exception ignoreThisException) {
			}			
		}

		return null;
	}
}