package com.picsauditing.report.fields;

public class TypeFactory {
	
	public static ColumnType lookupColumnType(FieldType fieldType, SqlFunction sqlFunction) {
		if (sqlFunction == null) {
			return lookupColumnType(fieldType);
		}
		return ColumnType.RightAlign;
	}
	
	public static ColumnType lookupColumnType(FieldType fieldType) {
		if (fieldType == FieldType.String) {
			return ColumnType.LeftAlign;
		} else {
			return ColumnType.RightAlign;
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
