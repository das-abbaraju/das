package com.picsauditing.actions.converters;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.OshaType;

public class OshaTypeConverter extends EnumConverter {

	protected static Map<Integer, OshaType> catToType = new HashMap<Integer, OshaType>();
	protected static Map<OshaType, Integer> typeToCat = new HashMap<OshaType,Integer>();
	
	static {
		
		catToType.put(AuditCategory.OSHA_AUDIT, OshaType.OSHA);
		catToType.put(AuditCategory.MSHA, OshaType.MSHA);
		catToType.put(AuditCategory.CANADIAN_STATISTICS, OshaType.COHS);
		
		typeToCat.put(OshaType.OSHA, AuditCategory.OSHA_AUDIT);
		typeToCat.put(OshaType.MSHA, AuditCategory.MSHA);
		typeToCat.put(OshaType.COHS, AuditCategory.CANADIAN_STATISTICS);
	}
	
	
	public OshaTypeConverter() {
		enumClass = OshaType.class;
	}
	
	public static OshaType getTypeFromCategory( int categoryId ) {
		return catToType.get( categoryId );
	}
	
	public static int getCategoryFromType( OshaType oshaType ) {
		return typeToCat.get( oshaType );
	}
	
}
