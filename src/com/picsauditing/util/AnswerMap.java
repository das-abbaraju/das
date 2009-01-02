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
	
	/**
	 * Return the answer for this question. If no answer exists for this tuple, 
	 * then try to return the answer to this question from the 0 tuple if it exists.
	 * This is useful when trying to access "parent" data when on a child row.
	 * @param questionID question.id
	 * @param rowID Parent answer.id that is the anchor for this tuple
	 * @return
	 */
	public AuditData get(int questionID, int rowID) {
		if (rowID > 0 && getRows(questionID).get(rowID) == null)
			return get(questionID);
		return getRows(questionID).get(rowID);
	}

	public AuditData get(AuditQuestion question, AuditData parent) {
		if (parent == null)
			return get(question.getId());
		return get(question.getId(), parent.getId());
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
			list.put(questionID, row);
		}
		return row;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for(Integer questionID : list.keySet()) {
			for(Integer rowID : list.get(questionID).keySet()) {
				output.append("Q:").append(questionID).append(" ");
				output.append("R:").append(rowID).append(" ");
				output.append("Answer:").append(list.get(questionID).get(rowID)).append("\n");
			}
		}
		return output.toString();
	}
}
