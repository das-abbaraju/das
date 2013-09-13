package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SuperMap<T> {
	protected Map<Object, Object> data = new HashMap<Object, Object>();

	public T get(Object... k) {
		T response = null;

		Object current = data;

		for (int i = 0; i < k.length; i++) {
			if (current instanceof Map && (i + 1) <= k.length) {
				current = ((Map) current).get(k[i]);
			}

			if ((i + 1) == k.length) {
				response = (T) current;
			}
		}
		return response;
	}

	public boolean put(Object... objects) {
		Object current = data;

		for (int i = 0; i < objects.length - 2; i++) {
			if (current instanceof Map && (i + 2) <= objects.length) {
				Object temp = ((Map) current).get(objects[i]);

				if (temp == null) {
					temp = new HashMap<Object, Object>();
					((Map) current).put(objects[i], temp);
				}
				current = temp;
			}
		}

		if (current instanceof Map) {
			((Map) current).put(objects[objects.length - 2], objects[objects.length - 1]);
		}

		return true;
	}
}