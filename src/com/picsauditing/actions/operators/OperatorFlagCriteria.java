package com.picsauditing.actions.operators;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private FlagQuestionCriteriaDAO criteriaDao;
	private List<OperatorAccount> inheritsFlagCriteria = null;
	private List<OperatorAccount> inheritsInsuranceCriteria = null;

	public OperatorFlagCriteria(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
	}

	public String execute() throws Exception {
		findOperator();
		// TODO check permissions
		//FlagCriteria
		
		return SUCCESS;
	}
	
	public Collection<QuestionCriteria> getQuestionList() {
		Map<AuditQuestion, QuestionCriteria> map = new TreeMap<AuditQuestion, QuestionCriteria>();
		
		List<FlagQuestionCriteria> criteriaList = criteriaDao.findByOperator(operator);
		for(FlagQuestionCriteria criteria : criteriaList) {
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

	public class QuestionCriteria {
		public AuditQuestion question;
		public FlagQuestionCriteria red;
		public FlagQuestionCriteria amber;
		public QuestionCriteria(AuditQuestion q) {
			question = q;
		}
	}

	public List<OperatorAccount> getInheritsFlagCriteria() {
		if (inheritsFlagCriteria == null) {
			inheritsFlagCriteria = operatorDao.findWhere(true, "a.inheritFlagCriteria.id = " + operator.getId());
			inheritsFlagCriteria.remove(operator);
		}
		return inheritsFlagCriteria;
	}

	public List<OperatorAccount> getInheritsInsuranceCriteria() {
		if (inheritsInsuranceCriteria == null) {
			inheritsInsuranceCriteria = operatorDao.findWhere(true, "a.inheritInsuranceCriteria.id = " + operator.getId());
			inheritsInsuranceCriteria.remove(operator);
		}
		return inheritsInsuranceCriteria;
	}
	
	
	
	
}
