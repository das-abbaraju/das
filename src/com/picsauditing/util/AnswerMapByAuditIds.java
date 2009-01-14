package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

public class AnswerMapByAuditIds<T> {
	
	private Map<T, AnswerMap> data = new HashMap<T, AnswerMap>();

	
	public AnswerMapByAuditIds() {}
	
	public AnswerMapByAuditIds( AnswerMapByAuditIds<T> toCopy ) {
		for( T audit : toCopy.data.keySet() ) {
			AnswerMap mapCopy = new AnswerMap(toCopy.get( audit ));
			put( audit, mapCopy );
		}
	}
	
	public AnswerMap get( T audit ) {
		return data.get( audit );
	}
	
	public void put( T audit, AnswerMap answers) {
		data.put(audit, answers);
	}
}
