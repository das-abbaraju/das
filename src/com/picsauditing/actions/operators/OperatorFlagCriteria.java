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

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private FlagQuestionCriteriaDAO criteriaDao;

	public OperatorFlagCriteria(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
	}

	public String execute() throws Exception {
		findOperator();
		//FlagCriteria
		
		return SUCCESS;
	}
	
	public Collection<QuestionCriteria> getQuestionList() {
		List<FlagQuestionCriteria> criteriaList = criteriaDao.findByOperator(operator.getId());
		Map<AuditQuestion, QuestionCriteria> map = new TreeMap<AuditQuestion, QuestionCriteria>();
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
	
	
}
