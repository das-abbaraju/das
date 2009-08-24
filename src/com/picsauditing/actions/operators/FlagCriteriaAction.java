package com.picsauditing.actions.operators;

import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

public class FlagCriteriaAction extends OperatorActionSupport implements Preparable {

	private FlagQuestionCriteria red;
	private FlagQuestionCriteria amber;

	private FlagQuestionCriteriaDAO criteriaDao;
	private AuditQuestionDAO questionDao;
	private ContractorAccountDAO contractorAccountDAO;

	private AuditQuestion question = null;

	private String testValue;
	private FlagColor testResult;

	public FlagCriteriaAction(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao,
			AuditQuestionDAO questionDao, ContractorAccountDAO contractorAccountDAO) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
		this.questionDao = questionDao;
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteCategory = NoteCategory.Flags;
	}

	@Override
	public void prepare() throws Exception {
		// TODO load the red and amber criteria
		findOperator();
		int questionID = getParameter("question.id");
		if (questionID > 0) {
			Map<FlagColor, FlagQuestionCriteria> criteriaMap = criteriaDao.find(operator.getId(), questionID);
			if (criteriaMap != null) {
				red = criteriaMap.get(FlagColor.Red);
				if (red != null)
					question = red.getAuditQuestion();
				amber = criteriaMap.get(FlagColor.Amber);
				if (amber != null)
					question = amber.getAuditQuestion();
			}
		}
		if (question == null) {
			question = questionDao.find(getParameter("question.id"));
		}
	}

	@Override
	public String execute() throws Exception {
		// REQUIRE an operator record
		// REQUIRE a question record

		if (button != null) {
			if ("save".equals(button)) {
				if (red != null) {
					if (Strings.isEmpty(red.getComparison()) && Strings.isEmpty(red.getValue())) {
						if (red.getId() != 0) {
							criteriaDao.remove(red);
						}
					} else if (isValid(red)) {
						red.setFlagColor(FlagColor.Red);
						red.setAuditQuestion(question);
						red.setOperatorAccount(operator);
						criteriaDao.save(red);
					} else
						criteriaDao.refresh(red);
				}
				if (amber != null) {
					if (Strings.isEmpty(amber.getComparison()) && Strings.isEmpty(amber.getValue())) {
						if (amber.getId() != 0) {
							criteriaDao.remove(amber);
						}
					} else if (isValid(amber)) {
						amber.setFlagColor(FlagColor.Amber);
						amber.setAuditQuestion(question);
						amber.setOperatorAccount(operator);
						criteriaDao.save(amber);
					} else
						criteriaDao.refresh(amber);
				}
				this.addNote(operator, "Flag Criteria has been edited for "
						+ question.getSubCategory().getCategory().getAuditType().getAuditName() + " question "
						+ question.getSubCategory().getCategory().getNumber() + "."
						+ question.getSubCategory().getNumber() + "." + question.getNumber());
				contractorAccountDAO.updateContractorByOperator(operator);
				return BLANK;
			}
			if ("test".equals(button)) {
				if (isValidValue(testValue)) {
					if (isValid(amber)) {
						if (amber.getAuditQuestion() == null)
							amber.setAuditQuestion(getQuestion());

						if (amber.isFlagged(testValue))
							testResult = FlagColor.Amber;
						else
							testResult = FlagColor.Green;
					}

					if (isValid(red)) {
						if (red.getAuditQuestion() == null)
							red.setAuditQuestion(getQuestion());

						if (red.isFlagged(testValue))
							testResult = FlagColor.Red;
						else
							testResult = (testResult == null) ? FlagColor.Green : testResult;
					}

					if (testResult != null)
						return "test";
				}

				return BLANK;
			}
		}

		return SUCCESS;
	}

	private boolean isValidValue(String criteria) {
		if (Strings.isEmpty(criteria))
			return false;
		if ("Date".equals(question.getQuestionType())) {
			if (!"Today".equalsIgnoreCase(criteria) && DateBean.parseDate(criteria) == null) {
				addActionError("The value entered is not a valid date");
				return false;
			}
		}
		if ("Decimal Number".equals(question.getQuestionType()) || "Money".equals(question.getQuestionType())) {
			try {
				Float.parseFloat(criteria.replace(",", ""));
			} catch (Exception e) {
				addActionError("The value entered is not a valid number");
				return false;
			}
		}
		return true;
	}

	private boolean isValid(FlagQuestionCriteria criteria) {

		if (Strings.isEmpty(criteria.getValue()))
			return false;

		if (Strings.isEmpty(criteria.getComparison()))
			return false;

		return isValidValue(criteria.getValue());
	}

	public AuditQuestion getQuestion() {
		if (question == null) {
			if (red != null)
				question = red.getAuditQuestion();
			else if (amber != null)
				question = amber.getAuditQuestion();
		}
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public FlagQuestionCriteria getRed() {
		return red;
	}

	public void setRed(FlagQuestionCriteria red) {
		this.red = red;
	}

	public FlagQuestionCriteria getAmber() {
		return amber;
	}

	public void setAmber(FlagQuestionCriteria amber) {
		this.amber = amber;
	}

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String test) {
		this.testValue = test;
	}

	public FlagColor getTestResult() {
		return testResult;
	}

	public void setTestResult(FlagColor testResult) {
		this.testResult = testResult;
	}
	
	public MultiYearScope[] getMultiYearScopeList() {
		return MultiYearScope.values();
	}
}
