package com.picsauditing.actions.auditType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.importpqf.ImportStopAt;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTransformOption;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageQuestion extends ManageCategory implements Preparable {
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected AuditOptionValueDAO auditOptionValueDAO;

	private Integer requiredQuestionID;
	private Integer visibleQuestionID;
	private List<AuditOptionGroup> optionTypes;
	private int groupID;
	private boolean extractOptionDefined;
	private boolean startAtBeginning;
	private String startingPoint = "";
	private boolean collectAsLines;
	private String stoppingPoint = "";
	private String stopAt = ImportStopAt.None.toString();

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
		extractOptionDefined = (question.getExtractOption() != null);
		if (extractOptionDefined) {
			stopAt = question.getExtractOption().getStopAt().toString();
			startAtBeginning = question.getExtractOption().isStartAtBeginning();
			startingPoint = question.getExtractOption().getStartingPoint();
			collectAsLines = question.getExtractOption().isCollectAsLines();
			stoppingPoint = question.getExtractOption().getStoppingPoint();
			if (startingPoint == null)
				startingPoint = "";
			if (stoppingPoint == null)
				stoppingPoint = "";
		}
		load(question.getCategory());
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();

		groupID = getParameter("groupID");
		if (groupID > 0)
			question.setOption(auditOptionValueDAO.findOptionGroup(groupID));
	}

	public boolean save() {
		if (question != null) {
			if (Strings.isEmpty(question.getName().toString())) {
				addActionError("Question name is required");
				return false;
			}

			if (question.getQuestionType().equals("MultipleChoice") && question.getOption() == null) {
				addActionError("Option type is required when 'Multiple Choice' is used as the question type");
				return false;
			}
			
			if (question.getLanguages().isEmpty() && category.getLanguages().size() >= 1) {
				question.setLanguages(category.getLanguages());
			}

			if (question.hasMissingChildRequiredLanguages()) {
				addActionError("Changes to required languages must always have at least one language left. "
						+ "Make sure your question has at least one language.");
				return false;
			}

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
				cal.setTime(new Date());
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

			if (question.getScoreWeight() > 0)
				question.setRequired(true);

			if (extractOptionDefined) {
				AuditExtractOption option = question.getExtractOption();
				if (option == null) {
					option = new AuditExtractOption();
					option.setQuestion(question);
					dao.save(option);
				}
				option.setCollectAsLines(collectAsLines);
				option.setStopAt(ImportStopAt.valueOf(stopAt));
				option.setStartAtBeginning(startAtBeginning);
				startingPoint = startingPoint.trim();
				if (startingPoint.length() == 0)
					option.setStartingPoint(null);
				else
					option.setStartingPoint(startingPoint);
				stoppingPoint = stoppingPoint.trim();
				if (stoppingPoint.length() == 0)
					option.setStoppingPoint(null);
				else
					option.setStoppingPoint(stoppingPoint);
				question.setExtractOption(option);
			} else {
				AuditExtractOption option = question.getExtractOption();
				if (option != null) {
					option.setQuestion(null);
					dao.remove(option);
				}
				question.setExtractOption(null);
			}

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
				addActionError("This Question has existing Data. It cannot be deleted.");
				return false;
			}
			id = question.getCategory().getId();
			auditQuestionDAO.deleteData(AuditCategoryRule.class, "question.id = " + question.getId());
			auditQuestionDAO.deleteData(AuditTypeRule.class, "question.id = " + question.getId());
			if (question.getExtractOption() != null) {
				dao.remove(question.getExtractOption());
			}
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

	public String[] getQuestionTypes() {
		return AuditQuestion.TYPE_ARRAY;
	}

	public List<AuditOptionGroup> getOptionTypes() {
		if (optionTypes == null) {
			// Get common
			String uniqueCodes = Strings.implodeForDB(AuditOptionGroup.COMMON_TYPES, ",");
			optionTypes = auditOptionValueDAO.findOptionTypeWhere("o.uniqueCode IN (" + uniqueCodes
					+ ") ORDER BY o.name");
		}

		return optionTypes;
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

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public boolean isExtractable() {
		if (question != null && question.getId() != 0 && question.getAuditType().isExtractable())
			return true;
		return false;
	}

	public boolean isExtractOptionDefined() {
		return extractOptionDefined;
	}

	public void setExtractOptionDefined(boolean extractOptionDefined) {
		this.extractOptionDefined = extractOptionDefined;
	}

	public boolean isStartAtBeginning() {
		return startAtBeginning;
	}

	public void setStartAtBeginning(boolean startAtBeginning) {
		this.startAtBeginning = startAtBeginning;
	}

	public String getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(String startingPoint) {
		this.startingPoint = startingPoint;
	}

	public boolean isCollectAsLines() {
		return collectAsLines;
	}

	public void setCollectAsLines(boolean collectAsLines) {
		this.collectAsLines = collectAsLines;
	}

	public String getStoppingPoint() {
		return stoppingPoint;
	}

	public void setStoppingPoint(String stoppingPoint) {
		this.stoppingPoint = stoppingPoint;
	}

	public String getStopAt() {
		return stopAt.toString();
	}

	public void setStopAt(String stopAt) {
		this.stopAt = stopAt;
	}

	public ImportStopAt[] getStopAtOptions() {
		return ImportStopAt.values();
	}

	public List<AuditTransformOption> getTransformOptions() {
		return question.getTransformOptions();
	}

	@Override
	protected void fillSelectedLocales() {
		if (question != null && !question.getLanguages().isEmpty()) {
			for (String locale : question.getLanguages()) {
				selectedLocales.add(new Locale(locale));
			}
		}
	}
}
