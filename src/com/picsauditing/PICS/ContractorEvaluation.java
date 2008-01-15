package com.picsauditing.PICS;

import java.util.*;

public class ContractorEvaluation {
	private boolean highlighted;
	private ArrayList<ContractorEvaluationCategory> categories;
	private int currentCategory;
	
	public void addCategory(String name){
		currentCategory++;
		this.categories.add(new ContractorEvaluationCategory(name, currentCategory+1));
	}
	public ArrayList<ContractorEvaluationCategory> getCategories() {
		return this.categories;
	}
	
	public ContractorEvaluation() {
		categories = new ArrayList<ContractorEvaluationCategory>();
		currentCategory = -1;
	}
	public String getColor(){
		highlighted = !highlighted;
		if (highlighted) return "#FFFFFF";
		return "";
	}
	
	/**
	 * Add a question to the last category in the list
	 * @param question
	 * @return
	 */
	public boolean addQuestion(ContractorEvaluationQuestion question) {
		if (currentCategory < 0) return false;
		return categories.get(currentCategory).addQuestion(question);
	}
}
