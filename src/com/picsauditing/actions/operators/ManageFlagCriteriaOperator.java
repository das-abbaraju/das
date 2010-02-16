package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.FlagOshaCriteriaDAO;
import com.picsauditing.dao.FlagQuestionCriteriaDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ManageFlagCriteriaOperator extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private FlagQuestionCriteriaDAO criteriaDao;
	private FlagCriteriaOperatorDAO opCriteriaDAO;
	private AuditQuestionDAO questionDao;
	private FlagOshaCriteriaDAO flagOshaCriteriaDAO;
	private ContractorAccountDAO contractorAccountDAO;
	private AuditTypeClass classType = AuditTypeClass.Audit;
	private Integer contractorsNeedingRecalculation = null;

	private Collection<QuestionCriteria> questionList = null;
	private List<AuditQuestion> addableQuestions = null;

	public ManageFlagCriteriaOperator(OperatorAccountDAO operatorDao, FlagQuestionCriteriaDAO criteriaDao,
			FlagOshaCriteriaDAO flagOshaCriteriaDAO, ContractorAccountDAO contractorAccountDAO,
			AuditQuestionDAO questionDao, FlagCriteriaOperatorDAO opCriteriaDAO) {
		super(operatorDao);
		this.criteriaDao = criteriaDao;
		this.opCriteriaDAO = opCriteriaDAO;
		this.questionDao = questionDao;
		this.flagOshaCriteriaDAO = flagOshaCriteriaDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		subHeading = "Manage Flag Criteria";
		noteCategory = NoteCategory.Flags;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		// TODO check permissions
		tryPermissions(OpPerms.EditFlagCriteria);

		findOperator();
		
		if(button != null) {
			return button;
		}

		return SUCCESS;
	}

	public FlagOshaCriteria getOshaRedFlagCriteria() {
		return flagOshaCriteriaDAO.findByOperatorFlag(operator, "t.flagColor = 'Red'");
	}

	public FlagOshaCriteria getOshaAmberFlagCriteria() {
		return flagOshaCriteriaDAO.findByOperatorFlag(operator, "t.flagColor = 'Amber'");
	}

	public Collection<QuestionCriteria> getQuestionList() {
		if (questionList == null) {
			Map<AuditQuestion, QuestionCriteria> map = new TreeMap<AuditQuestion, QuestionCriteria>();
			List<FlagQuestionCriteria> criteriaList = new ArrayList<FlagQuestionCriteria>();
			
			if(classType.isPolicy())
			  criteriaList = criteriaDao.findByOperator(operator.getInheritInsuranceCriteria());
			else
				criteriaList = criteriaDao.findByOperator(operator.getInheritFlagCriteria());
			
			for (FlagQuestionCriteria criteria : criteriaList) {
				AuditQuestion q = criteria.getAuditQuestion();
				if (q.isVisible()) {
					AuditTypeClass qClassType = q.getSubCategory().getCategory().getAuditType().getClassType();
					if (!qClassType.isPolicy())
						// Convert IM and any "other" audit type to Audit
						qClassType = AuditTypeClass.Audit;
					if (qClassType.equals(classType)) {
						if (!map.containsKey(q)) {
							map.put(q, new QuestionCriteria(q));
						}
						if (criteria.getFlagColor().equals(FlagColor.Amber))
							map.get(q).amber = criteria;
						if (criteria.getFlagColor().equals(FlagColor.Red))
							map.get(q).red = criteria;
					}
				}
			}

			questionList = map.values();
		}

		return questionList;
	}

	public List<AuditQuestion> getQuestions() {
		if (addableQuestions == null) {
			addableQuestions = questionDao.findWhere("isRedFlagQuestion = 'Yes' AND isVisible = 'Yes'");

			Iterator<AuditQuestion> questions = addableQuestions.iterator();
			Set<AuditType> visibleAudits = new HashSet<AuditType>();
			Set<OperatorAccount> children = new HashSet<OperatorAccount>();

			if (classType.isPolicy())
				children.addAll(getInheritsInsuranceCriteria());
			else
				children.addAll(getInheritsFlagCriteria());

			children.add(operator);

			for (OperatorAccount o : children) {
				for (AuditOperator ao : o.getInheritAudits().getVisibleAudits()) {
					if ((ao.getAuditType().getClassType().isPolicy() && classType.isPolicy())
							|| !ao.getAuditType().getClassType().isPolicy() && !classType.isPolicy())
						visibleAudits.add(ao.getAuditType());
				}
			}
			while (questions.hasNext()) {
				AuditQuestion question = questions.next();
				if (!visibleAudits.contains(question.getAuditType()))
					questions.remove();
				else
					for (QuestionCriteria cq : getQuestionList()) {
						if (cq.question.equals(question)) {
							questions.remove();
							break;
						}
					}
			}

			Collections.sort(addableQuestions, new Comparator<AuditQuestion>() {
				@Override
				public int compare(AuditQuestion o1, AuditQuestion o2) {
					return o1.getSubCategory().getCategory().getAuditType().compareTo(
							o2.getSubCategory().getCategory().getAuditType());
				}
			});
		}
		return addableQuestions;
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
		if (this.classType.isPolicy())
			this.subHeading = "Manage Flag Criteria - InsureGUARD&trade;";
		else
			this.subHeading = "Manage Flag Criteria - PQF/Audits";
	}

	public static String getTime(int time) {
		if (time == 1)
			return "Individual Yrs";
		if(time == 2)
			return "Last Year Only";
		if (time == 3)
			return "ThreeYearAverage";
		return "";
	}

	public int getContractorsNeedingRecalculation() {
		if (contractorsNeedingRecalculation == null)
			contractorsNeedingRecalculation = contractorAccountDAO.findContractorsNeedingRecalculation(operator);
		return contractorsNeedingRecalculation;
	}
	
	public List<FlagCriteriaOperator> getCriteriaList() {
		// Filter out here?
		List<FlagCriteriaOperator> list = opCriteriaDAO.findByOperator(operator.getId());
		List<FlagCriteriaOperator> valid = new ArrayList<FlagCriteriaOperator>();
		
		for (FlagCriteriaOperator item : list) {
			FlagCriteria criteria = item.getCriteria();
			
			if (criteria.getCategory().equals("InsureGUARD"))
				continue;
			
			if (criteria.getQuestion() != null) {
				int questionID = criteria.getQuestion().getId();
				
				if (questionID == 401 || questionID == 755)
					continue;
			}

			if (criteria.getOshaType() != null) {
				if (!criteria.getOshaType().equals(operator.getOshaType()))
					continue;
			}
			
			valid.add(item);
		}
		
		return valid;
	}
}
