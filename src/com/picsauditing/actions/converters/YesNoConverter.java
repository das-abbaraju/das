package com.picsauditing.actions.converters;

import java.util.Map;

import com.picsauditing.jpa.entities.YesNo;

public class YesNoConverter extends EnumConverter {
	public YesNoConverter() {
		enumClass = YesNo.class;
	}

	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {
		Object test = null;
		try {
			test = super.convertFromString(arg0, arg1, arg2);
		} catch (Exception itllJustStayNull) { }

		if( test == null ) {
			if( arg1.length > 0 ) {
				boolean b = Boolean.parseBoolean(arg1[0]);
				if( b ) {
					test = YesNo.Yes;
				}
				else {
					test = YesNo.No;
				}
			}
		}

		return test;
	}

}
