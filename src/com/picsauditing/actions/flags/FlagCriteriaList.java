package com.picsauditing.actions.flags;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.FlagCriteria;

@SuppressWarnings("serial")
public class FlagCriteriaList extends PicsActionSupport {

	private FlagCriteriaDAO criteriaDAO;

	private int auditTypeID;
	private int questionID;

	public FlagCriteriaList(FlagCriteriaDAO criteriaDAO) {
		this.criteriaDAO = criteriaDAO;
	}

	public void prepare() throws Exception {
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<FlagCriteria> getCriteriaList() {
		if(auditTypeID > 0)
			return criteriaDAO.findWhere("auditType.id = "+auditTypeID);
		else if(questionID > 0)
			return criteriaDAO.findWhere("question.id = "+questionID);
		else return new ArrayList<FlagCriteria>();
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}
}
