package com.picsauditing.forms.binding;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionFormBindingAnnotationCache {
	
	private ActionFormBindingAnnotationCache() {
	}
	
	private static final Map<Class<?>, Map<String, String>> actionClassFormObjectAnnotationCache = Collections
			.synchronizedMap(new HashMap<Class<?>, Map<String, String>>());
	
	public static Map<String, String> getFormMappingForAction(Object action) {
		Class<?> clazz = action.getClass();
		if (actionClassFormObjectAnnotationCache.containsKey(clazz)) {
			return actionClassFormObjectAnnotationCache.get(clazz);
		}

		Map<String, String> newMapping = buildMapping(clazz);
		// check in case another thread put it into the cache already
		if (!actionClassFormObjectAnnotationCache.containsKey(clazz)) {
			actionClassFormObjectAnnotationCache.put(clazz, newMapping);
		}

		return actionClassFormObjectAnnotationCache.get(clazz);
	}
	
	private static Map<String, String> buildMapping(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (ArrayUtils.isEmpty(fields)) {
			return Collections.emptyMap();
		}

		Map<String, String> mapping = new HashMap<>();
		for (Field field : fields) {
			FormBinding formBinding = field.getAnnotation(FormBinding.class);
			if (formBinding != null) {
				for (String formName : formBinding.value()) {
					mapping.put(formName, field.getName());
				}
			}
		}

		return mapping;
	}

}
