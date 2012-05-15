package com.picsauditing.report.tables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

public class JpaFieldExtractor {
	static public Set<Field> addFields(@SuppressWarnings("rawtypes") Class clazz, String prefix, String alias) {
		Set<Field> fields = new HashSet<Field>();
		for (Method method : clazz.getMethods()) {
			if (clazz.equals(method.getDeclaringClass())) {
				ReportField fieldAnnotation = getReportFieldAnnotation(method);
				if (fieldAnnotation != null) {
					Field field = new Field(fieldAnnotation);
					String fieldName = stripGetFromMethodName(method.getName());
					field.setName(prefix + fieldName);
					fields.add(field);
					if (!Strings.isEmpty(fieldAnnotation.sql())) {
						field.setDatabaseColumnName(fieldAnnotation.sql().replace("{ALIAS}", alias));
					} else {
						Column columnAnnotation = getColumnAnnotation(method);
						if (columnAnnotation == null) {
							field.setDatabaseColumnName(alias + "." + fieldName.toLowerCase());
						} else {
							field.setDatabaseColumnName(alias + "." + columnAnnotation.name());
						}
					}
				}
			}
		}
		return fields;
	}

	static private String stripGetFromMethodName(String methodName) {
		if (methodName.startsWith("get"))
			return methodName.substring(3);
		if (methodName.startsWith("is"))
			return methodName.substring(2);
		return methodName;
	}

	static private ReportField getReportFieldAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(ReportField.class)) {
				return (ReportField) annotation;
			}
		}
		return null;
	}

	static private Column getColumnAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(Column.class)) {
				return (Column) annotation;
			}
		}
		return null;
	}

}
