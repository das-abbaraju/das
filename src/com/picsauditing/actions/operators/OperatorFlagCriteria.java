package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;
	
	public OperatorFlagCriteria(OperatorAccountDAO operatorDao) {
		super(operatorDao);
	}

	public String execute() throws Exception {
		findOperator();
		//FlagCriteria
		
		return SUCCESS;
	}
	
	public List<QuestionCriteria> getQuestionCriteria() {
		// TODO query red/amber flag criteria
		return new ArrayList<QuestionCriteria>();
	}
	
	public class QuestionCriteria {
		public AuditQuestion question;
		public FlagQuestionCriteria red;
		public FlagQuestionCriteria amber;
	}
	
}
