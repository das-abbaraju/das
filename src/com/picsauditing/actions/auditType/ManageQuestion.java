package com.picsauditing.actions.auditType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionTextDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionText;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageQuestion extends ManageCategory {

	protected AuditDataDAO auditDataDAO;
	protected AuditQuestionTextDAO questionTextDAO;
	private int requiredQuestionID = 0;

	protected AuditQuestionText questionText;
	protected String defaultQuestion;
	protected String defaultRequirement;

	public ManageQuestion(EmailTemplateDAO emailTemplateDAO,
			AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCategoryDao,
			AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO,
			CountryDAO countryDAO, AuditQuestionTextDAO questionTextDAO) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao,
				auditQuestionDao, questionTextDAO);
		this.auditDataDAO = auditDataDAO;
		this.questionTextDAO = questionTextDAO;
	}

	@Override
	public void prepare() throws Exception {
		int textID = getParameter("questionText.id");
		if (textID > 0)
			questionText = questionTextDAO.find(textID);

		super.prepare();
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ManageAudits);

		if ("text".equals(button)) {
			return SUCCESS;
		}
		if ("saveText".equals(button)) {
			permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
			boolean found = false;
			/*for (AuditQuestionText text : question.getQuestionTexts()) {
				if (text.getLocale().equals(questionText.getLocale())
						&& text.getId() != questionText.getId()) {
					found = true;
					break;
				}
			}*/
			if (found) {
				addActionError("There is already a translation for that language/country.");
				return SUCCESS;
			}
			questionText.setAuditQuestion(question);
			questionText.setAuditColumns(permissions);
			questionTextDAO.save(questionText);
			//question.getQuestionTexts().add(questionText);
			// TODO: This is a temporary fix - it will be changed when there is
			// more time.
			if (save()) {
				addActionMessage("Successfully saved"); // default message
			}
			id = question.getId();
			return SUCCESS;
		}
		if ("removeText".equals(button)) {
			questionTextDAO.remove(questionText.getId());
			button = "save";
		}

		return super.execute();
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
		load(question.getAuditCategory());
	}

	public boolean save() {
		if (question != null) {
			/*if (question.getId() == 0) {
				if (!Strings.isEmpty(defaultQuestion))
					question.setDefaultQuestion(defaultQuestion);
				if (!Strings.isEmpty(defaultRequirement)) {
					if (question.getQuestionText(AuditQuestion.DEFAULT_LOCALE) == null) {
						addActionError("You cannot add a requirement without adding a question as well.");
						return false;
					}
					question.setDefaultRequirement(defaultRequirement);
				}
			}*/
			/*if (question.getQuestionTexts().size() == 0) {
				this.addActionError("Question is required");
				return false;
			}*/
			if (question.getNumber() == 0) {
				int maxID = 0;
				for (AuditQuestion sibling : subCategory.getQuestions()) {
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
					|| requiredQuestionID != question.getRequiredQuestion()
							.getId()) {
				// dependsOnQuestionID has changed
				question.setRequiredQuestion(new AuditQuestion());
				question.getRequiredQuestion().setId(requiredQuestionID);
			}
			subCategory.getQuestions().add(question);
			if (question.getAuditCategory() == null)
				question.setAuditCategory(subCategory);
			question = auditQuestionDAO.save(question);
			id = question.getAuditCategory().getId();

			recalculateCategory();
			return true;
		}
		return false;
	}

	protected boolean delete() {
		try {
			// TODO check to see if AuditData exists for this question first
			List<AuditData> list = auditDataDAO.findByQuestionID(question
					.getId());
			if (list.size() > 0) {
				addActionError("Deleting Questions is not supported just yet.");
				return false;
			}
			subCategory.getQuestions().remove(question);
			id = question.getAuditCategory().getId();
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

			AuditCategory targetSubCategory = auditCategoryDAO
					.find(targetID);
			AuditQuestion aq = copyAuditQuestion(question, targetSubCategory);

			addActionMessage("Copied the Question only. <a href=\"ManageQuestion.action?id="
					+ aq.getId() + "\">Go to this Question?</a>");
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

			AuditCategory targetSubCategory = auditCategoryDAO
					.find(targetID);
			question.setAuditCategory(targetSubCategory);
			auditQuestionDAO.save(question);

			addActionMessage("Question Moved Successfully. <a href=\"ManageQuestion.action?id="
					+ question.getId() + "\">Go to this Question?</a>");
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
					if (today.after(tempQuestion.getEffectiveDate())
							&& today.before(tempQuestion.getExpirationDate())) {
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

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}

	public int getRequiredQuestionID() {
		return requiredQuestionID;
	}

	public void setRequiredQuestionID(int requiredQuestionID) {
		this.requiredQuestionID = requiredQuestionID;
	}

	public AuditQuestionText getQuestionText() {
		return questionText;
	}

	public void setQuestionText(AuditQuestionText questionText) {
		this.questionText = questionText;
	}

	public String getDefaultQuestion() {
		return defaultQuestion;
	}

	public void setDefaultQuestion(String defaultQuestion) {
		this.defaultQuestion = defaultQuestion;
	}

	public String getDefaultRequirement() {
		return defaultRequirement;
	}

	public void setDefaultRequirement(String defaultRequirement) {
		this.defaultRequirement = defaultRequirement;
	}

	public Locale[] getLocaleList() {
		Locale[] locales = Locale.getAvailableLocales();
		Arrays.sort(locales, new Comparator<Locale>() {
			@Override
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		return locales;
	}
}
