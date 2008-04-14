package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.jpa.entities.YesNo;

public class YesNoConverter extends StrutsTypeConverter
{
	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {

		Object response = null;
		
		if( arg2.equals(YesNo.class))
		{
			if( arg1.length > 0 )
			{
				response = YesNo.valueOf(arg1[0]);
			}
			else
			{
				response = null;
			}
		}
		return response;
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {

		String response = null;
		
		if( arg1 instanceof YesNo )
		{
			response = ( (YesNo) arg1 ).name();
		}
		
		return response;
	}
	

}
