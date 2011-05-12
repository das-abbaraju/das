package com.picsauditing.util;

import javax.persistence.Table;

public final class ReflectUtil {
	public static String getTableName(Class<?> cls) {
		String tableName = cls.getSimpleName();
		Table table = cls.getAnnotation(Table.class);
		if (table != null)
			tableName = table.name();
		return tableName;
	}
}
