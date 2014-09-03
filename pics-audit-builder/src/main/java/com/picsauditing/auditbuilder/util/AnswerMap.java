package com.picsauditing.auditbuilder.util;

import com.picsauditing.auditbuilder.entities.DocumentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class AnswerMap {
	private Map<Integer, List<DocumentData>> list = new HashMap<>();

	public AnswerMap(List<DocumentData> result) {
		for (DocumentData row : result)
			add(row);
	}

	public void add(DocumentData answer) {
		if (answer == null)
			return;
		getDataList(answer.getQuestion().getId()).add(answer);
	}

	public DocumentData get(int questionID) {
		List<DocumentData> dataList = getDataList(questionID);
		if (dataList.size() > 0)
			return dataList.get(0);
		return null;
	}

	private List<DocumentData> getDataList(int questionID) {
		List<DocumentData> dataList = list.get(questionID);
		if (dataList == null) {
			dataList = new ArrayList<>();
			list.put(questionID, dataList);
		}
		return dataList;
	}
}