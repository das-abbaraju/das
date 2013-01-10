package com.picsauditing.report.fields;

public class TypeFactory {
	
	public static DisplayType lookupColumnType(FieldType fieldType, SqlFunction sqlFunction) {
		if (sqlFunction != null && sqlFunction.getDisplayType() != null) {
			return sqlFunction.getDisplayType();
		}
		
		return lookupColumnType(fieldType);
	}
	
	public static DisplayType lookupColumnType(FieldType fieldType) {
		if (fieldType == FieldType.String) {
			return DisplayType.LeftAlign;
		} else {
			return DisplayType.RightAlign;
		}
	}
	
	public static FilterType lookupFilterType(FieldType fieldType, SqlFunction sqlFunction) {
		if (sqlFunction == null) {
			return lookupFilterType(fieldType);
		}
		
		return null;
	}
	
	public static FilterType lookupFilterType(FieldType fieldType) {
		return null;
	}

}
