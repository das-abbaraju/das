package com.picsauditing.actions.operators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.FlagOshaCriteriaDAO;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private FlagQuestionCriteriaDAO criteriaDao;
	private AuditQuestionDAO questionDao;
	private FlagOshaCriteriaDAO flagOshaCriteriaDAO;
	private AuditTypeClass classType = AuditTypeClass.PQF;

	public OperatorFlagCriteria(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao,
			FlagOshaCriteriaDAO flagOshaCriteriaDAO, AuditQuestionDAO questionDao) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
		this.questionDao = questionDao;
		this.flagOshaCriteriaDAO = flagOshaCriteriaDAO;
		subHeading = "Manage Flag Criteria";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		// TODO check permissions
		// tryPermissions(OpPerms.EditFlagCriteria);

		findOperator();

		return SUCCESS;
	}

	public FlagOshaCriteria getOshaRedFlagCriteria() {
		return flagOshaCriteriaDAO.findByOperatorFlag(operator, "t.flagColor = 'Red'");
	}

	public FlagOshaCriteria getOshaAmberFlagCriteria() {
		return flagOshaCriteriaDAO.findByOperatorFlag(operator, "t.flagColor = 'Amber'");
	}

	public Collection<QuestionCriteria> getQuestionList() {
		Map<AuditQuestion, QuestionCriteria> map = new TreeMap<AuditQuestion, QuestionCriteria>();

		List<FlagQuestionCriteria> criteriaList = criteriaDao.findByOperator(operator);
		for (FlagQuestionCriteria criteria : criteriaList) {
			AuditQuestion q = criteria.getAuditQuestion();
			if (!map.containsKey(q)) {
				map.put(q, new QuestionCriteria(q));
			}
			if (criteria.getFlagColor().equals(FlagColor.Amber))
				map.get(q).amber = criteria;
			if (criteria.getFlagColor().equals(FlagColor.Red))
				map.get(q).red = criteria;
		}

		return map.values();
	}

	public List<AuditQuestion> getQuestions() {
		List<AuditQuestion> result = questionDao.findWhere("isRedFlagQuestion = 'Yes'");

		Iterator<AuditQuestion> questions = result.iterator();

		while (questions.hasNext()) {
			AuditQuestion question = questions.next();
			if (!question.getSubCategory().getCategory().getAuditType().getClassType().equals(classType))
				questions.remove();
		}

		for (QuestionCriteria qc : getQuestionList()) {
			result.remove(qc.question);
		}

		Collections.sort(result, new Comparator<AuditQuestion>() {
			@Override
			public int compare(AuditQuestion o1, AuditQuestion o2) {
				return o1.getSubCategory().getCategory().getAuditType().compareTo(
						o2.getSubCategory().getCategory().getAuditType());
			}
		});

		return result;
	}

	/**
	 * Get a list of operators that inherit their criteria from this account
	 * 
	 * @return
	 */
	public Collection<OperatorAccount> getInheritingOperators() {
		if (classType.isPolicy())
			return getInheritsInsuranceCriteria();
		return getInheritsFlagCriteria();
	}

	public class QuestionCriteria {
		public AuditQuestion question;
		public FlagQuestionCriteria red;
		public FlagQuestionCriteria amber;

		public QuestionCriteria(AuditQuestion q) {
			question = q;
		}
	}

	public AuditTypeClass getClassType() {
		return classType;
	}

	public void setClassType(AuditTypeClass classType) {
		this.classType = classType;
	}

	public static String getTime(int time) {
		if (time == 1)
			return "Individual Yrs";
		if (time == 3)
			return "ThreeYearAverage";
		return "";
	}
}
