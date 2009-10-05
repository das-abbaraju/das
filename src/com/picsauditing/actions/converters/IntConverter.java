package com.picsauditing.actions.converters;

import java.util.Map;

@SuppressWarnings("unchecked")
public class IntConverter extends EnumConverter {
	public IntConverter() {
		enumClass = int.class;
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		return (String) performFallbackConversion(arg0, arg1, String.class);
	}
	
	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {
		Object test = null;
		try {
			test = super.convertFromString(arg0, arg1, arg2);
		} catch (Exception itllJustStayNull) { }

		if( test == null ) {
			if( arg1.length > 0 ) {
				String temp = arg1[0];
				temp = temp.replaceAll( "\\." , "" );
				temp = temp.replaceAll( "\\," , "" );
				try {
					test = Integer.parseInt(temp);
				}
				catch( Exception e ) {
					test = performFallbackConversion(arg0, arg1, arg2);
				}
			}
		}

		if( test == null ) {
			test = performFallbackConversion(arg0, arg1, arg2);
		}
		
		return test;
	}

}
