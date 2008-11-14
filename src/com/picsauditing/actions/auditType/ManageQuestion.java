package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.util.Strings;

public class ManageQuestion extends ManageSubCategory {

	protected AuditQuestionDAO auditQuestionDao;
	protected AuditDataDAO auditDataDAO;
	private String dependsOnQuestionID = null;

	public ManageQuestion(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao,
			AuditQuestionDAO auditQuestionDao,
			AuditDataDAO auditDataDAO) {
		super(auditTypeDao, auditCategoryDao, auditSubCategoryDao);
		this.auditQuestionDao = auditQuestionDao;
		this.auditDataDAO = auditDataDAO;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditQuestionDao.find(id));
		}
	}

	@Override
	protected void loadParent(int id) {
		super.load(id);
	}
	
	protected void load(AuditQuestion o) {
		this.question = o;
		if (question.getDependsOnQuestion() != null)
			dependsOnQuestionID = Integer.toString(question.getDependsOnQuestion().getQuestionID());
		load(question.getSubCategory());
	}
	
	public boolean save() {
		if (question != null) {
			if (question.getQuestion() == null || question.getQuestion().length() == 0) {
				this.addActionError("Question is required");
				return false;
			}
			if (question.getNumber() == 0) {
				int maxID = 0;
				for(AuditQuestion sibling : subCategory.getQuestions()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				question.setNumber(maxID + 1);
			}
			question.setLastModified(new java.util.Date());
			
			if (question.getDateCreated() == null)
				question.setDateCreated(new java.util.Date());
			if (question.getEffectiveDate() == null)
				question.setEffectiveDate(question.getDateCreated());
			if (question.getExpirationDate() == null)
				question.setExpirationDate(DateBean.getEndOfTime());
			
			if (Strings.isEmpty(dependsOnQuestionID) || dependsOnQuestionID.equals("0"))
				question.setDependsOnQuestion(null);
			else {
				try {
					int questionId = Integer.parseInt(dependsOnQuestionID);
					if (question.getDependsOnQuestion() == null || questionId != question.getDependsOnQuestion().getQuestionID()) {
						question.setDependsOnQuestion(new AuditQuestion());
						question.getDependsOnQuestion().setQuestionID(questionId);
					}
				} catch (NumberFormatException e) {
					addActionError("Depends on Question must be a number");
					return false;
				}
			}
			
			subCategory.getQuestions().add(question);
			question = auditQuestionDao.save(question);
			id = question.getSubCategory().getId();
			
			recalculateCategory();
			return true;
		}
		return false;
	}
	
	protected boolean delete() {
		try {
			// TODO check to see if AuditData exists for this question first
			List<AuditData> list = auditDataDAO.findByQuestionID(question.getQuestionID());
			if (list.size() > 0) {
				addActionError("Deleting Questions is not supported just yet.");
				return false;
			}
			subCategory.getQuestions().remove(question);
			id = question.getSubCategory().getId();
			auditQuestionDao.remove(question.getQuestionID());
			
			recalculateCategory();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}
	
	private void recalculateCategory() {
		if (category != null && category.getId() > 0) {
			// Renumber the category
			int numQuestions = 0;
			int numRequired = 0;
			for(AuditSubCategory subCat : category.getSubCategories()) {
				for(AuditQuestion tempQuestion : subCat.getQuestions()) {
					numQuestions++;
					if (tempQuestion.isRequired())
						numRequired++;
				}
			}
			category.setNumQuestions(numQuestions);
			category.setNumRequired(numRequired);
			auditCategoryDao.save(category);
		}
	}

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}

	public String getDependsOnQuestionID() {
		return dependsOnQuestionID;
	}

	public void setDependsOnQuestionID(String dependsOnQuestionID) {
		this.dependsOnQuestionID = dependsOnQuestionID;
	}
	
}
