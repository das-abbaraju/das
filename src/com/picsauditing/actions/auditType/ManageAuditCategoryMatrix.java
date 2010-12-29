package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryMatrixDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class ManageAuditCategoryMatrix extends PicsActionSupport {
	protected int auditTypeID;
	protected AuditType auditType;
	protected AuditTypeDAO auditTypeDAO;
	protected AuditCategoryMatrixDAO auditCategoryMatrixDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	private OperatorCompetencyDAO operatorCompetencyDAO;
	private List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	
	public ManageAuditCategoryMatrix(AuditTypeDAO auditTypeDAO, AuditCategoryMatrixDAO auditCategoryMatrixDAO, AuditQuestionDAO auditQuestionDAO, OperatorCompetencyDAO operatorCompetencyDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryMatrixDAO = auditCategoryMatrixDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.operatorCompetencyDAO = operatorCompetencyDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		// TODO we need 3 filters
		// 1) AuditType Filter with a option to pick either Desktop Audit or Shell HSE Audit. They need to pick a audit 
		// before looking at any data
		// 2) if they picked Shell HSE we need to show them a filter of operator Competencies
		// 3) if they selected Desktop we need to show 3 filters 
		// a)  Services Performed, b) Industries Working In, c) Main Type Of Work, d)Canadian Main Type Of Work
		// e) Canadian  Industries Working In 
		// These filters should have all the questions in the above categories
		// for now I am hard coding it to be Shell HSE Audit
		auditTypeID = 175;
		if(auditTypeID == 0) {
			addActionError("Please select a audit");
			return SUCCESS;
		}
		
		auditType = auditTypeDAO.find(auditTypeID);
		
		if(auditType.getId() == AuditType.DESKTOP) {
//		 questions.add(auditQuestionDAO.findByCategory("Services Performed"));
//		 questions.add(auditQuestionDAO.findByCategory("Industries Working In"));
//		 questions.add(auditQuestionDAO.findByCategory("Main Type Of Work"));
//		 questions.add(auditQuestionDAO.findByCategory("Canadian Main Type Of Work"));
//		 questions.add(auditQuestionDAO.findByCategory("Canadian  Industries Working In"));

		}

		// the above data just gives us all the categories but we need to find the ones with the mapping in the 
		//auditCategoryMatrix
		// TODO find of all the mappings of the questions or competencies with the categories
		
		//TODO The jsp page should have all the filters and a pivot table with categories as rows and competencies as columns.
		//  The user can pick the categories needed for any competency with a check box. It is a many to many relationship between category and competency
		// For example look at desktop Matrix on V4.picsorganizer.com
		return SUCCESS;
	}

	public List<OperatorCompetency> getOperatorCompetencies() {
		return operatorCompetencyDAO.findAll();
	}

	public AuditType getAuditType() {
		return auditType;
	}
}
