package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;

/**
 * Two level map of questionID and rowID
 * 
 * @author Trevor
 * 
 */
public class AnswerMap {
	//private Map<Integer, Map<Integer, List<AuditData>>> list = new HashMap<Integer, Map<Integer, List<AuditData>>>();
	private Map<Integer, List<AuditData>> list = new HashMap<Integer, List<AuditData>>();
	
	public AnswerMap(List<AuditData> result) {
		for (AuditData row : result)
			add(row);
	}
	
	public void add(AuditData answer) {
		if (answer == null)
			return;
		getDataList(answer.getQuestion().getId()).add(answer);
	}

	public AuditData get(int questionID) {
		List<AuditData> dataList = getDataList(questionID);
		if (dataList.size() > 0)
			return dataList.get(0);
		return null;
	}

	/**
	 * Return a List of AuditDatas. If no List exists, one will be created and
	 * returned. This is useful to avoid null errors.
	 */
	private List<AuditData> getDataList(int questionID) {
		List<AuditData> dataList = list.get(questionID);
		if (dataList == null) {
			dataList = new ArrayList<AuditData>();
			list.put(questionID, dataList);
		}
		return dataList;
	}
}
