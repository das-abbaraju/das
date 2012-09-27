package com.picsauditing.report.tables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.util.Strings;

public class JpaFieldExtractor {
	public static Set<Field> addFields(Class<?> clazz) {
		Set<Field> fields = new HashSet<Field>();

		for (Method method : clazz.getMethods()) {
			if (!clazz.equals(method.getDeclaringClass()))
				continue;

			ReportField fieldAnnotation = getReportFieldAnnotation(method);
			if (fieldAnnotation == null)
				continue;

			Field field = new Field(fieldAnnotation);
			field.setFieldClass(method.getReturnType());
			String fieldName = stripGetFromMethodName(method.getName());
			field.setName(fieldName);
			field.setType(fieldAnnotation.filterType().getFieldType());
			fields.add(field);

			if (!Strings.isEmpty(fieldAnnotation.sql())) {
				field.setDatabaseColumnName(fieldAnnotation.sql());
				continue;
			}

			Column columnAnnotation = getColumnAnnotation(method);
			if (columnAnnotation == null || Strings.isEmpty(columnAnnotation.name())) {
				field.setDatabaseColumnName(fieldName.toLowerCase());
			} else {
				field.setDatabaseColumnName(columnAnnotation.name());
			}
		}

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
