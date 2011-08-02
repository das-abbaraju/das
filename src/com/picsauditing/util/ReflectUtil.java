package com.picsauditing.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.persistence.Table;

public final class ReflectUtil {
	/**
	 * Method to get the name of the JPA table on an entity class.
	 * 
	 * @param cls
	 * @return tableName
	 */
	public static String getTableName(Class<?> cls) {
		String tableName = cls.getSimpleName();
		Table table = cls.getAnnotation(Table.class);
		if (table != null)
			tableName = table.name();
		return tableName;
	}

	/**
	 * Finds and returns the annotation from the provided class, and all direct super classes.
	 * 
	 * This does not look at interfaces.
	 * 
	 * @param cls
	 * @param annotation
	 * @return
	 */
	public static <T extends Annotation> T getApplicableClassLevelAnnotation(Class<?> cls, Class<T> annotation) {
		if (cls != null) {
			if (cls.isAnnotationPresent(annotation))
				return cls.getAnnotation(annotation);

			return getApplicableClassLevelAnnotation(cls.getSuperclass(), annotation);
		}

		return null;
	}

	/**
	 * Finds a method level annotation (using inheritance) to find the annotation for a given method on a class.
	 * 
	 * @param inClass
	 * @param methodName
	 * @param annotation
	 * @return
	 */
	public static <T extends Annotation> T getApplicableMethodLevelAnnotation(Class<?> inClass, String methodName,
			Class<T> annotation) {
		T returnAnnotation = null;
		Class<?> cls = inClass;
		while (cls != null) {
			try {
				Method method = cls.getDeclaredMethod(methodName);
				if (method.isAnnotationPresent(annotation)) {
					returnAnnotation = method.getAnnotation(annotation);
					break;
				}
				cls = cls.getSuperclass();
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}

		return returnAnnotation;
	}

	public static <T extends Annotation> T getApplicableMethodLevelAnnotation(Method method, Class<T> annotation) {
		if (method != null) {
			if (method.isAnnotationPresent(annotation))
				return method.getAnnotation(annotation);

			return getApplicableMethodLevelAnnotation(method.getDeclaringClass().getSuperclass(), method.getName(),
					annotation);
		}

		return null;
	}

}
