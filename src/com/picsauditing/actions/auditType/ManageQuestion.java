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
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionText;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageQuestion extends ManageSubCategory {

	protected AuditQuestionDAO auditQuestionDao;
	protected AuditDataDAO auditDataDAO;
	protected AuditQuestionTextDAO questionTextDAO;
	private int dependsOnQuestionID = 0;

	protected AuditQuestionText questionText;
	protected String defaultQuestion;

	public ManageQuestion(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao, AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO,
			CountryDAO countryDAO, AuditQuestionTextDAO questionTextDAO) {
		super(auditTypeDao, auditCategoryDao, auditSubCategoryDao, countryDAO);
		this.auditQuestionDao = auditQuestionDao;
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
			for (AuditQuestionText text : question.getQuestionTexts()) {
				if (text.getLocale().equals(questionText.getLocale()) && text.getId() != questionText.getId()) {
					found = true;
					break;
				}
			}
			if (found) {
				addActionError("There is already a translation for that language/country.");
				return SUCCESS;
			}
			questionText.setAuditQuestion(question);
			questionText.setAuditColumns(permissions);
			questionTextDAO.save(questionText);
			question.getQuestionTexts().add(questionText);
			// TODO: This is a temporary fix - it will be changed when there is more time.
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
			if (question.getId() == 0 && !Strings.isEmpty(defaultQuestion)) {
				question.setDefaultQuestion(defaultQuestion);
			}
			if (question.getQuestionTexts().size() == 0) {
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
			question.setAuditColumns(permissions);

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
			if (!Strings.isEmpty(countries))
				question.setCountriesArray(countries.split("\\|"), exclude);
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
					if (today.after(tempQuestion.getEffectiveDate()) && today.before(tempQuestion.getExpirationDate())) {
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

	@SuppressWarnings("unchecked")
	public JSONArray getInitialCountries() {
		JSONArray json = new JSONArray();

		for (String c : question.getCountriesArray()) {
			Country country = countryDAO.find(c);
			if (country != null) {
				JSONObject o = new JSONObject();
				o.put("id", country.getIsoCode());
				o.put("name", country.getName(permissions.getLocale()));
				json.add(o);
			}
		}

		return json;
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
