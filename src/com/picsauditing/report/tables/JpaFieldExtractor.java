package com.picsauditing.report.tables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.util.Strings;

public class JpaFieldExtractor {
	static public Set<QueryField> addFields(@SuppressWarnings("rawtypes") Class clazz, String prefix, String alias) {
		Set<QueryField> fields = new HashSet<QueryField>();
		for (Method method : clazz.getMethods()) {
			if (clazz.equals(method.getDeclaringClass())) {
				ReportField fieldAnnotation = getReportFieldAnnotation(method);
				if (fieldAnnotation != null) {
					QueryField queryField = new QueryField(fieldAnnotation);
					String fieldName = stripGetFromMethodName(method.getName());
					queryField.setName(prefix + fieldName);
					fields.add(queryField);
					if (!Strings.isEmpty(fieldAnnotation.sql())) {
						queryField.setSql(fieldAnnotation.sql().replace("{ALIAS}", alias));
					} else {
						Column columnAnnotation = getColumnAnnotation(method);
						if (columnAnnotation == null) {
							queryField.setSql(alias + "." + fieldName.toLowerCase());
						} else {
							queryField.setSql(alias + "." + columnAnnotation.name());
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
