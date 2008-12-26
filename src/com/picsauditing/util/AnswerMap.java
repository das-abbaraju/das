package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

public class AnswerMap {
	private Map<Integer, Map<Integer, AuditData>> list = new HashMap<Integer, Map<Integer,AuditData>>();
	
	public void add(AuditQuestion question, AuditData parent, AuditData answer) {
		if (parent == null)
			add(question.getId(), answer);
		else
			add(question.getId(), parent.getId(), answer);
	}

	public void add(AuditData answer) {
		if (answer == null)
			return;
		AuditQuestion question = answer.getQuestion();
		AuditData parent = answer.getParentAnswer();
		if (parent == null)
			add(question.getId(), answer);
		else
			add(question.getId(), parent.getId(), answer);
	}

	public void add(int questionID, int rowID, AuditData answer) {
		getRows(questionID).put(rowID, answer);
	}

	public void add(int questionID, AuditData answer) {
		add(questionID, 0, answer);
	}
	
	public AuditData get(int questionID) {
		return get(questionID, 0);
	}
	
	public AuditData get(int questionID, int rowID) {
		return getRows(questionID).get(rowID);
	}
	
	public List<AuditData> getAnswers(int questionID) {
		List<AuditData> orderedList = new ArrayList<AuditData>();
		
		for (AuditData childData : getRows(questionID).values()) {
			orderedList.add(childData);
		}
		return orderedList;
	}
	
	private Map<Integer, AuditData> getRows(int questionID) {
		Map<Integer, AuditData> row = list.get(questionID);
		if (row == null) {
			row = new HashMap<Integer, AuditData>();
			list.put(questionID, new HashMap<Integer, AuditData>());
		}
		return row;
	}
}
