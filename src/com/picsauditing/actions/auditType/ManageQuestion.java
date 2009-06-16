package com.picsauditing.actions.auditType;

import java.util.Date;
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

public class ManageQuestion extends ManageSubCategory {

	protected AuditQuestionDAO auditQuestionDao;
	protected AuditDataDAO auditDataDAO;
	private int dependsOnQuestionID = 0;

	public ManageQuestion(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao, AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO) {
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
			dependsOnQuestionID = question.getDependsOnQuestion().getId();
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
				for (AuditQuestion sibling : subCategory.getQuestions()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				question.setNumber(maxID + 1);
			}
			question.setAuditColumns(getUser());

			if (question.getEffectiveDate() == null)
				question.setEffectiveDate(question.getCreationDate());
			if (question.getExpirationDate() == null)
				question.setExpirationDate(DateBean.getEndOfTime());

			if (dependsOnQuestionID == 0)
				question.setDependsOnQuestion(null);
			else if (question.getDependsOnQuestion() == null
					|| dependsOnQuestionID != question.getDependsOnQuestion().getId()) {
				// dependsOnQuestionID has changed
				question.setDependsOnQuestion(new AuditQuestion());
				question.getDependsOnQuestion().setId(dependsOnQuestionID);
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
			List<AuditData> list = auditDataDAO.findByQuestionID(question.getId());
			if (list.size() > 0) {
				addActionError("Deleting Questions is not supported just yet.");
				return false;
			}
			subCategory.getQuestions().remove(question);
			id = question.getSubCategory().getId();
			auditQuestionDao.remove(question.getId());

			recalculateCategory();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	private void recalculateCategory() {
		Date today = new Date();
		if (category != null && category.getId() > 0) {

			// Renumber the category
			int numQuestions = 0;
			int numRequired = 0;
			for (AuditSubCategory subCat : category.getSubCategories()) {
				for (AuditQuestion tempQuestion : subCat.getQuestions()) {
					if (today.after(tempQuestion.getEffectiveDate())
							&& today.before(tempQuestion.getExpirationDate())) {
						numQuestions++;
						if ("Yes".equals(tempQuestion.getIsRequired()))
							numRequired++;
					}
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

	public int getDependsOnQuestionID() {
		return dependsOnQuestionID;
	}

	public void setDependsOnQuestionID(int dependsOnQuestionID) {
		this.dependsOnQuestionID = dependsOnQuestionID;
	}
}
