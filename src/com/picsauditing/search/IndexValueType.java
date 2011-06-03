package com.picsauditing.search;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.IsoCode;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Strings;

/**
 * Extensible enum handling how to treat retrieving the value from a method that
 * is to be indexed
 * 
 * @author David Tomberlin
 * 
 */
public enum IndexValueType {

	STRINGTYPE { // Treat the value as a simple string
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				indexValues.add(new IndexObject(strResult, weight));
			}

			return indexValues;
		}
	},
	MULTISTRINGTYPE {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				String[] strArray = strResult.replaceAll(NAME_REGEX, " ").split("\\s+");
				for (String str : strArray) {
					if (!Strings.isEmpty(str))
						indexValues.add(new IndexObject(str, weight));
				}
			}

			return indexValues;
		}
	},
	CLEANSTRING {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				indexValues.add(new IndexObject(strResult.replaceAll(NAME_REGEX, ""), weight));
			}

			return indexValues;
		}
	},
	ISOTYPE {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			IsoCode place = (IsoCode) getValue(method, record);

			if (place != null) {
				if (!Strings.isEmpty(place.getIsoCode()))
					indexValues.add(new IndexObject(place.getIsoCode().toUpperCase(), weight));
				if (!Strings.isEmpty(place.getEnglish()))
					indexValues.add(new IndexObject(place.getEnglish().toUpperCase(), weight));
			}

			return indexValues;
		}
	},
	PHONETYPE {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				strResult = com.picsauditing.util.Strings.stripPhoneNumber(strResult);
				if (strResult.length() >= 10 && !strResult.matches("\\W"))
					indexValues.add(new IndexObject(strResult.replaceAll(NAME_REGEX, ""), weight));
			}

			return indexValues;
		}
	},
	URLTYPE {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				indexValues.add(new IndexObject(strResult.replaceAll(URL_REGEX, ""), weight));
			}

			return indexValues;
		}
	},
	EMAILTYPE {
		@Override
		public List<IndexObject> getIndexValues(Indexable record, Method method, int weight) {
			List<IndexObject> indexValues = new ArrayList<IndexObject>();
			String strResult = getString(getValue(method, record));

			if (!Strings.isEmpty(strResult)) {
				String[] strArray = strResult.split("@");
				for (String str : strArray) {
					if (!Strings.isEmpty(str))
						indexValues.add(new IndexObject(str.replaceAll("\\W", ""), weight));
				}
			}

			return indexValues;
		}
	};

	private static final String NAME_REGEX = "[^a-zA-Z0-9\\s]";
	private static final String URL_REGEX = "^(HTTP://)(W{3})|^(W{3}.)|\\W";

	static Object getValue(Method method, Indexable record) {
		Object result = null;
		try {
			result = method.invoke(record);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return result;
	}

	static String getString(Object o) {
		if (o instanceof TranslatableString) {
			return o.toString() == null ? null : o.toString().toUpperCase();
		} else
			return o == null ? null : String.valueOf(o).toUpperCase();
	}

	public abstract List<IndexObject> getIndexValues(Indexable record, Method method, int weight);
}
