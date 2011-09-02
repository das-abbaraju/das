package com.picsauditing.actions.converters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.OshaType;

public class OshaTypeConverter {

	protected final static Map<Integer, OshaType> catToType;
	protected final static Map<OshaType, Integer> typeToCat;

	static {
		Map<Integer, OshaType> catToTypeTemp = new HashMap<Integer, OshaType>();
		catToTypeTemp.put(AuditCategory.OSHA_AUDIT, OshaType.OSHA);
		catToTypeTemp.put(AuditCategory.MSHA, OshaType.MSHA);
		catToTypeTemp.put(AuditCategory.CANADIAN_STATISTICS, OshaType.COHS);

		catToType = Collections.unmodifiableMap(catToTypeTemp);

		Map<OshaType, Integer> typeToCatTemp = new HashMap<OshaType, Integer>();
		typeToCatTemp.put(OshaType.OSHA, AuditCategory.OSHA_AUDIT);
		typeToCatTemp.put(OshaType.MSHA, AuditCategory.MSHA);
		typeToCatTemp.put(OshaType.COHS, AuditCategory.CANADIAN_STATISTICS);

		typeToCat = Collections.unmodifiableMap(typeToCatTemp);
	}

	public static OshaType getTypeFromCategory(int categoryId) {
		return catToType.get(categoryId);
	}

	public static int getCategoryFromType(OshaType oshaType) {
		return typeToCat.get(oshaType);
	}


}