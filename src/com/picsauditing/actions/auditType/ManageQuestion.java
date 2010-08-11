package com.picsauditing.actions.auditType;

import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ManageQuestion extends ManageCategory {

	protected AuditDataDAO auditDataDAO;
	private int requiredQuestionID = 0;
	private int visibleQuestionID = 0;

	public ManageQuestion(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao, AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao, auditQuestionDao);
		this.auditDataDAO = auditDataDAO;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditQuestionDAO.find(id));
		}
	}

	@Override
	protected void loadParent(int id) {
		super.load(id);
	}

	protected void load(AuditQuestion o) {
		this.question = o;
		if (question.getRequiredQuestion() != null)
			requiredQuestionID = question.getRequiredQuestion().getId();
		load(question.getCategory());
	}

	public boolean save() {
		if (question != null) {
			if (question.getNumber() == 0) {
				int maxID = 0;
				for (AuditQuestion sibling : category.getQuestions()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				question.setNumber(maxID + 1);
			}
			question.setAuditColumns(permissions);

			if (question.getEffectiveDate() == null)
				question.setEffectiveDate(question.getCreationDate());
			if (question.getExpirationDate() == null)
				question.setExpirationDate(DateBean.getEndOfTime());

			if (requiredQuestionID == 0)
				question.setRequiredQuestion(null);
			else if (question.getRequiredQuestion() == null
					|| requiredQuestionID != question.getRequiredQuestion().getId()) {
				question.setRequiredQuestion(auditQuestionDAO.find(requiredQuestionID));
			}

			if (visibleQuestionID == 0)
				question.setRequiredQuestion(null);
			else if (question.getVisibleQuestion() == null
					|| visibleQuestionID != question.getVisibleQuestion().getId()) {
				question.setVisibleQuestion(auditQuestionDAO.find(visibleQuestionID));
			}

			if (question.getCategory() == null)
				question.setCategory(category);

			question = auditQuestionDAO.save(question);
			id = question.getCategory().getId();

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
			id = question.getCategory().getId();
			auditQuestionDAO.remove(question.getId());

			recalculateCategory();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected boolean copy() {
		try {
			if (targetID == 0) {
				addActionMessage("Please Select SubCategory to copy to");
				return false;
			}

			AuditCategory targetSubCategory = auditCategoryDAO.find(targetID);
			AuditQuestion aq = copyAuditQuestion(question, targetSubCategory);

			addActionMessage("Copied the Question only. <a href=\"ManageQuestion.action?id=" + aq.getId()
					+ "\">Go to this Question?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected boolean move() {
		try {
			if (targetID == 0) {
				addActionMessage("Please Select SubCategory to move to");
				return false;
			}

			AuditCategory targetSubCategory = auditCategoryDAO.find(targetID);
			question.setCategory(targetSubCategory);
			auditQuestionDAO.save(question);

			addActionMessage("Question Moved Successfully. <a href=\"ManageQuestion.action?id=" + question.getId()
					+ "\">Go to this Question?</a>");
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
			for (AuditCategory subCat : category.getSubCategories()) {
				for (AuditQuestion tempQuestion : subCat.getQuestions()) {
					if (today.after(tempQuestion.getEffectiveDate()) && today.before(tempQuestion.getExpirationDate())) {
						numQuestions++;
						if (tempQuestion.isRequired())
							numRequired++;
					}
				}
			}
			category.setNumQuestions(numQuestions);
			category.setNumRequired(numRequired);
			auditQuestionDAO.save(category);
		}
	}

	@Override
	protected String getRedirectURL() {
		return "ManageCategory.action?id=" + question.getCategory().getId();
	}

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}

	public int getRequiredQuestionID() {
		return requiredQuestionID;
	}

	public void setRequiredQuestionID(int requiredQuestionID) {
		this.requiredQuestionID = requiredQuestionID;
	}

	public int getVisibleQuestionID() {
		return visibleQuestionID;
	}

	public void setVisibleQuestionID(int visibleQuestionID) {
		this.visibleQuestionID = visibleQuestionID;
	}
}
