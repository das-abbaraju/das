package com.picsauditing.actions.operators;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private FlagQuestionCriteriaDAO criteriaDao;
	private AuditTypeClass classType = AuditTypeClass.PQF;

	public OperatorFlagCriteria(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
		subHeading = "Manage Flag Criteria";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		// TODO check permissions
		//tryPermissions(OpPerms.EditFlagCriteria);

		findOperator();

		return SUCCESS;
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
	
	/**
	 * Get a list of operators that inherit their criteria from this account
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

}
