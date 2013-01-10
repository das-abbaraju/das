package com.picsauditing.report.fields;

public class TypeFactory {
	
	public static ColumnType lookupColumnType(FieldType fieldType, SqlFunction sqlFunction) {
		if (sqlFunction == null) {
			return lookupColumnType(fieldType);
		}
		
		return null;
	}
	
	public static ColumnType lookupColumnType(FieldType fieldType) {
		return ColumnType.LeftAlign;
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
