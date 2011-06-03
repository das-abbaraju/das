package com.picsauditing.jpa.entities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.picsauditing.search.IndexOverrideIgnore;
import com.picsauditing.search.IndexOverrideWeight;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.util.IndexObject;

@SuppressWarnings("serial")
@MappedSuperclass
@EntityListeners(IndexableEntityListener.class)
public abstract class AbstractIndexableTable extends BaseTable implements Indexable {

	@Transient
	public List<IndexObject> getIndexValues() {
		List<IndexObject> indexValues = new ArrayList<IndexObject>();
		List<Method> methods = new ArrayList<Method>();
		Map<String, Integer> weightOverrides = new HashMap<String, Integer>();
		Set<String> ignoreOverrides = new HashSet<String>();

		Class<?> current = this.getClass();
		while (current != null) {
			methods.addAll(Arrays.asList(current.getDeclaredMethods()));
			if (current.isAnnotationPresent(IndexableOverride.class)) {
				IndexableOverride iOverride = current.getAnnotation(IndexableOverride.class);
				for (IndexOverrideWeight weight : iOverride.overrides()) {
					weightOverrides.put(weight.methodName(), weight.weight());
				}
				for (IndexOverrideIgnore method : iOverride.ignores()) {
					ignoreOverrides.add(method.methodName());
				}
			}
			current = current.getSuperclass();
		}

		for (Method method : methods) {
			if (method.isAnnotationPresent(IndexableField.class) && !ignoreOverrides.contains(method.getName())) {
				IndexableField iField = method.getAnnotation(IndexableField.class);
				indexValues.addAll(iField.type().getIndexValues(this, method,
						getWeight(iField, method, weightOverrides)));
			}
		}
		return indexValues;
	}

	@Transient
	private final int getWeight(IndexableField iField, Method method, Map<String, Integer> overrides) {
		if (overrides.get(method.getName()) == null)
			return iField.weight();
		else
			return overrides.get(method.getName());
	}

}
