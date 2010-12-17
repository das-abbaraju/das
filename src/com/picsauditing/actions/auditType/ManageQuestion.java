package com.picsauditing.actions.auditType;

import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ManageQuestion extends ManageCategory {

	protected AuditDataDAO auditDataDAO;
	private Integer requiredQuestionID;
	private Integer visibleQuestionID;

	public ManageQuestion(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao, AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO,
			AuditDecisionTableDAO ruleDAO, WorkFlowDAO wfDAO) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao, auditQuestionDao, ruleDAO, wfDAO);
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
		if (question.getVisibleQuestion() != null)
			visibleQuestionID = question.getVisibleQuestion().getId();
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

			if (question.getEffectiveDate() == null) {
				Calendar cal = Calendar.getInstance();
				cal.set(2000, Calendar.JANUARY, 1);
				question.setEffectiveDate(cal.getTime());
			}
			if (question.getExpirationDate() == null)
				question.setExpirationDate(DateBean.getEndOfTime());

			if (requiredQuestionID == null || requiredQuestionID == 0)
				question.setRequiredQuestion(null);
			else if (question.getRequiredQuestion() == null
					|| requiredQuestionID != question.getRequiredQuestion().getId()) {
				question.setRequiredQuestion(auditQuestionDAO.find(requiredQuestionID));
			}

			if (visibleQuestionID == null || visibleQuestionID == 0)
				question.setVisibleQuestion(null);
			else if (question.getVisibleQuestion() == null
					|| visibleQuestionID != question.getVisibleQuestion().getId()) {
				question.setVisibleQuestion(auditQuestionDAO.find(visibleQuestionID));
			}

			if (question.getCategory() == null)
				question.setCategory(category);
			
			if(question.getScoreWeight()>0)
				question.setRequired(true);

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
	protected boolean move() {
		try {
			if (targetID == 0)
				addActionMessage("Please Select SubCategory to move to");
			else {
				AuditCategory targetSubCategory = auditCategoryDAO.find(targetID);
				question.setCategory(targetSubCategory);
				question.setAuditColumns(permissions);
				
				int number = 0;
				for (AuditQuestion q : targetSubCategory.getQuestions()) {
					if (q.getNumber() > number)
						number = q.getNumber();
				}
				question.setNumber(number + 1);
				
				auditQuestionDAO.save(question);
				recalculateCategory(question.getCategory());
				return true;
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		
		return false;
	}
	
	@Override
	protected boolean copy() {
		try {
			if (targetID == 0)
				addActionMessage("Please Select SubCategory to copy to");
			else {
				AuditCategory targetSubCategory = auditCategoryDAO.find(targetID);
				AuditQuestion copy = new AuditQuestion(question, targetSubCategory);
				copy.setAuditColumns(permissions);
				
				int number = 0;
				for (AuditQuestion q : targetSubCategory.getQuestions()) {
					if (q.getNumber() > number)
						number = q.getNumber();
				}
				copy.setNumber(number + 1);
				
				question = auditQuestionDAO.save(copy);
				recalculateCategory(question.getCategory());
				return true;
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		
		return false;
	}

	private void recalculateCategory() {
		if (category != null && category.getId() > 0) {
			category.recalculateQuestions();
			auditQuestionDAO.save(category);
		}
	}
	
	private void recalculateCategory(AuditCategory cat) {
		cat.recalculateQuestions();
		auditQuestionDAO.save(cat);
	}

	@Override
	protected String getRedirectURL() {
		return "ManageCategory.action?id=" + question.getCategory().getId();
	}
	
	@Override
	protected String getCopyMoveURL() {
		return "ManageQuestion.action?id=" + question.getId();
	}

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}

	public Integer getRequiredQuestionID() {
		return requiredQuestionID;
	}

	public void setRequiredQuestionID(Integer requiredQuestionID) {
		this.requiredQuestionID = requiredQuestionID;
	}

	public Integer getVisibleQuestionID() {
		return visibleQuestionID;
	}

	public void setVisibleQuestionID(Integer visibleQuestionID) {
		this.visibleQuestionID = visibleQuestionID;
	}
}
