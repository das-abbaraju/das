package com.picsauditing.actions.operators;

import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;

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
			// TODO Add in the QuestionDAO
			question = questionDao.find(getParameter("question.id"));
		}
	}

	@Override
	public String execute() throws Exception {
		// TODO Check permissions to edit
		// REQUIRE an operator record
		// REQUIRE a question record

		if (button != null) {
			if ("delete".equals(button)) {
				// TODO delete both criteria
				addActionMessage("Successfully deleted criteria");
				return BLANK;
			}
			if ("save".equals(button)) {
				// TODO insert or update each criteria
				// TODO delete unused criteria
				addActionMessage("Successfully saved criteria");
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
			// TODO query the question here ???
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
