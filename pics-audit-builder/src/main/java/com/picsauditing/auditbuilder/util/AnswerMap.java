package com.picsauditing.auditbuilder.util;

import com.picsauditing.auditbuilder.entities.AuditData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class AnswerMap {
	private Map<Integer, List<AuditData>> list = new HashMap<>();

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

	private List<AuditData> getDataList(int questionID) {
		List<AuditData> dataList = list.get(questionID);
		if (dataList == null) {
			dataList = new ArrayList<>();
			list.put(questionID, dataList);
		}
		return dataList;
	}
}