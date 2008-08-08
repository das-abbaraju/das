package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCatOperatorDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.DesktopMatrixDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.DesktopMatrix;

public class ManageDesktopMatrix extends PicsActionSupport {
	private int[] questionIDs = null;
	
	protected AuditTypeDAO auditDAO;
	protected AuditCatOperatorDAO auditCatOperatorDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected DesktopMatrixDAO desktopMatrixDAO;
	
	private List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	private List<AuditCategory> categories = new ArrayList<AuditCategory>();
	/**
	 * category.question.DesktopMatrix
	 */
	private Map<Integer, Map<Integer, Boolean>> data;
	
	public ManageDesktopMatrix(AuditTypeDAO auditDAO, AuditCatOperatorDAO auditCatOperatorDAO,
			AuditQuestionDAO auditQuestionDAO, DesktopMatrixDAO desktopMatrixDAO) {
		this.auditDAO = auditDAO;
		this.auditCatOperatorDAO = auditCatOperatorDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.desktopMatrixDAO = desktopMatrixDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		if (questionIDs == null || questionIDs.length == 0)
			return SUCCESS;

		for(int questionID : questionIDs) {
			// Query all the questions at once
			AuditQuestion question = auditQuestionDAO.find(questionID);
			questions.add(question);
		}
		categories = auditDAO.find(AuditType.DESKTOP).getCategories();
		
		// Build the data now
		data = new HashMap<Integer, Map<Integer, Boolean>>();
		for(AuditCategory category : categories) {
			data.put(category.getId(), new HashMap<Integer, Boolean>());
		}
		List<DesktopMatrix> matrixData = desktopMatrixDAO.findByQuestions(questionIDs);
		for(DesktopMatrix row : matrixData) {
			data.get(row.getCategory().getId()).put(row.getQuestion().getQuestionID(), true);
		}

		return SUCCESS;
	}

	// GETTERS && SETTERS

	public int[] getQuestionIDs() {
		return questionIDs;
	}

	public void setQuestionIDs(int[] questionIDs) {
		this.questionIDs = questionIDs;
	}

	public List<AuditCategory> getCategories() {
		return categories;
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}
	
	public List<AuditQuestion> getTypeOfWork() {
		return auditQuestionDAO.findBySubCategory(85);
	}
	
	public List<AuditQuestion> getIndustries() {
		return auditQuestionDAO.findBySubCategory(84);
	}
	
	public List<AuditQuestion> getServicesPerformed() {
		return auditQuestionDAO.findBySubCategory(40);
	}

	public Map<Integer, Map<Integer, Boolean>> getData() {
		return data;
	}

	public void setData(Map<Integer, Map<Integer, Boolean>> data) {
		this.data = data;
	}
}
