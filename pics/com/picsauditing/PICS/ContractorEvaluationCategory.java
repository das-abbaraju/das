package com.picsauditing.PICS;

import java.util.ArrayList;

public class ContractorEvaluationCategory {
	private String name;
	private int displayOrder;
	private ArrayList<ContractorEvaluationQuestion> questions;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public ContractorEvaluationCategory(String name, int displayOrder) {
		this.name = name;
		this.displayOrder = displayOrder;
		this.questions = new ArrayList<ContractorEvaluationQuestion>();
	}

	public boolean addQuestion(ContractorEvaluationQuestion question) {
		question.setId(questions.size()+1);
		question.setCatID(this.displayOrder);
		return questions.add(question);
	}
	public ArrayList<ContractorEvaluationQuestion> getQuestions() {
		return questions;
	}
}
