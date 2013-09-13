package com.picsauditing.model.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.util.Strings;

/**
 * The purpose of this class is to walk through the Action class object
 * hierarchy and build a translation key for the property in the ActionClass.
 */
public class ObjectPropertyKeyBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ObjectPropertyKeyBuilder.class);

	public static Map<String, Class<?>> mapNameToType(Class<?> c, String property) throws SecurityException {
		Map<String, Class<?>> result = new LinkedHashMap<String, Class<?>>();
		String[] hierarchy = property.split("\\.");
		Class<?> type = getTypeFromInheritedClasses(c, hierarchy[0]);
		result.put(hierarchy[0], type);

		for (int i = 1; i < hierarchy.length; i++) {
			type = getTypeFromInheritedClasses(type, hierarchy[i]);
			result.put(hierarchy[i], type);
		}

		return result;
	}

	public static Class<?> getTypeFromInheritedClasses(Class<?> type, String field) throws SecurityException {
		Class<?> result = null;
		do {
			try {
				result = type.getDeclaredField(field).getType();
			} catch (NoSuchFieldException e) {
				type = type.getSuperclass();
			}
		} while (result == null && type != null);

		return result;
	}

	public static String getDefaultValueFromType(Class<?> c, String property) throws SecurityException {
		Map<String, Class<?>> typeMap = mapNameToType(c, property);

		Class<?> clazz = typeMap.values().toArray(new Class<?>[0])[typeMap.size() - 1];

		if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)) {
			return "0";
		}

		return Strings.EMPTY_STRING;
	}

	public static boolean isTranslatable(Class<?> c) {
		if (c == null) {
			return false;
		}

		for (Class<?> i : c.getInterfaces()) {
			if (i.equals(Translatable.class)) {
				return true;
			}
		}

		return isTranslatable(c.getSuperclass());
	}

	public static String getTranslationName(Class<?> c, String property) throws SecurityException {
		Map<String, Class<?>> typeMap = mapNameToType(c, property);
		Class<?> type = null;
		Iterator<Entry<String, Class<?>>> iter = typeMap.entrySet().iterator();
		List<Entry<String, Class<?>>> nonTranslatables = new ArrayList<Map.Entry<String, Class<?>>>();
		do {
			Entry<String, Class<?>> next = iter.next();
			if (isTranslatable(next.getValue())) {
				type = next.getValue();
			} else {
				nonTranslatables.add(next);
			}

		} while (iter.hasNext() && isTranslatable(type));

		if (type == null) {
			logger.warn("The field [{}] is not translatable. Please add a label for this field.", property);
		} else {
			StringBuilder result = new StringBuilder(type.getSimpleName());
			for (Entry<String, Class<?>> entry : nonTranslatables) {
				result.append(".").append(entry.getKey());
			}
			return result.toString();
		}

		return Strings.EMPTY_STRING;
	}

}
