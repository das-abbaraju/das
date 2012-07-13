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
	public static Set<Field> addFields(Class<?> clazz, String prefix, String alias) {
		Set<Field> fields = new HashSet<Field>();
		
		for (Method method : clazz.getMethods()) {
			if (!clazz.equals(method.getDeclaringClass()))
				continue;

			ReportField fieldAnnotation = getReportFieldAnnotation(method);
			if (fieldAnnotation == null)
				continue;

			Field field = new Field(fieldAnnotation);
			String fieldName = stripGetFromMethodName(method.getName());
			field.setFieldClass(method.getReturnType());
			field.setName(prefix + fieldName);
			fields.add(field);

			if (!Strings.isEmpty(fieldAnnotation.sql())) {
				field.setDatabaseColumnName(fieldAnnotation.sql().replace("{ALIAS}", alias));
				continue;
			}

			Column columnAnnotation = getColumnAnnotation(method);
			if (columnAnnotation == null || Strings.isEmpty(columnAnnotation.name())) {
				field.setDatabaseColumnName(alias + "." + fieldName.toLowerCase());
			} else {
				field.setDatabaseColumnName(alias + "." + columnAnnotation.name());
			}
		}

		// TODO: Revise this to allow for super class methods
//		Class<?> superclass = clazz.getSuperclass();
//		if (superclass != null)
//			fields.addAll(addFields(superclass, prefix, alias));

		return fields;
	}

	private static String stripGetFromMethodName(String methodName) {
		if (methodName.startsWith("get"))
			return methodName.substring(3);

		if (methodName.startsWith("is"))
			return methodName.substring(2);

		return methodName;
	}

	private static ReportField getReportFieldAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(ReportField.class))
				return (ReportField) annotation;
		}

		return null;
	}

	private static Column getColumnAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(Column.class))
				return (Column) annotation;
		}

		return null;
	}
}
