package com.picsauditing.actions.operators;

import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public class FlagCriteriaAction extends OperatorActionSupport implements Preparable {

	private FlagQuestionCriteria red;
	private FlagQuestionCriteria amber;

	private FlagQuestionCriteriaDAO criteriaDao;
	private AuditQuestionDAO questionDao;

	private AuditQuestion question = null;

	public FlagCriteriaAction(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao,
			AuditQuestionDAO questionDao) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
		this.questionDao = questionDao;
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
					} else if (Strings.isEmpty(red.getComparison()) || Strings.isEmpty(red.getValue())) {
						addActionError("Cannot save Red Flag Criteria without a comparison and a value");
					} else { // Both comparison and value exist
						red.setFlagColor(FlagColor.Red);
						red.setAuditQuestion(question);
						red.setChecked(YesNo.Yes);
						red.setOperatorAccount(operator);
						criteriaDao.save(red);
					}
				}
				if (amber != null) {
					if (Strings.isEmpty(amber.getComparison()) && Strings.isEmpty(amber.getValue())) {
						if (amber.getId() != 0) {
							criteriaDao.remove(amber);
						}
					} else if (Strings.isEmpty(amber.getComparison()) || Strings.isEmpty(amber.getValue())) {
						addActionError("Cannot save Amber Flag Criteria without a comparison and a value");
					} else { // Both comparison and value exist
						amber.setFlagColor(FlagColor.Amber);
						amber.setAuditQuestion(question);
						amber.setChecked(YesNo.Yes);
						amber.setOperatorAccount(operator);
						criteriaDao.save(amber);
					}
				}

				return BLANK;
			}
			if ("add".equals(button)) {
				// TODO insert each criteria
			}
		}

		return SUCCESS;
	}

	public AuditQuestion getQuestion() {
		if (question == null) {
			question = red.getAuditQuestion();
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
}
