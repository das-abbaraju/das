package com.picsauditing.actions.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageFlagCriteria extends PicsActionSupport implements Preparable {

	private AuditTypeDAO auditTypeDAO;
	private AuditQuestionDAO questionDAO;
	private FlagCriteriaDAO criteriaDAO;

	private int id;
	private FlagCriteria criteria;

	private int auditTypeID;
	private int questionID;

	public ManageFlagCriteria(AuditTypeDAO auditTypeDAO, AuditQuestionDAO questionDAO, FlagCriteriaDAO criteriaDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.questionDAO = questionDAO;
		this.criteriaDAO = criteriaDAO;
	}

	public void prepare() throws Exception {
		int criteriaID = getParameter("id");
		if (criteriaID > 0)
			criteria = criteriaDAO.find(criteriaID);

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if (criteria == null) {
			criteria = new FlagCriteria();
		}

		if (button != null) {
			if ("Save".equals(button)) {
				if (criteria != null) {
					List<String> errors = new ArrayList<String>();
					if (auditTypeID == 0 && questionID == 0) {
						// clear anything that was put in here
						criteriaDAO.refresh(criteria);
						errors.add("Either a question or an audit type is required.");
					}

					if (Strings.isEmpty(criteria.getDataType())) {
						criteriaDAO.refresh(criteria);
						errors.add("DataType is a required field.");
					}

					if (errors.size() > 0) {
						for (String e : errors) {
							addActionError(e);
						}
						return SUCCESS;
					}

					// set the auditType or the question based on the incoming
					// value
					if (criteria.getAuditType() == null || criteria.getAuditType().getId() != auditTypeID)
						criteria.setAuditType(auditTypeDAO.find(auditTypeID));

					if (criteria.getQuestion() == null || criteria.getQuestion().getId() != questionID)
						criteria.setQuestion(questionDAO.find(questionID));

					criteria.setAuditColumns(permissions);

					try {
						criteriaDAO.save(criteria);
						addActionMessage("Criteria saved successfully.");
					} catch (final Exception e) {
						addActionError("Something happened during save:<br/>" + e.getMessage());
						return SUCCESS;
					}
				}

			}

			if ("Delete".equals(button)) {
				if (criteria != null) {
					criteriaDAO.remove(criteria);
					criteria = null;
					addActionMessage("Criteria successfully deleted.");
				}
			}
		}

		return SUCCESS;
	}

	public List<FlagCriteria> getCriteriaList() {
		return criteriaDAO.findAll();
	}

	public FlagCriteria getCriteria() {
		return criteria;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public Map<AuditTypeClass, List<AuditType>> getAuditTypeMap() {
		Map<AuditTypeClass, List<AuditType>> auditTypeMap = new TreeMap<AuditTypeClass, List<AuditType>>();

		for (AuditType auditType : auditTypeDAO.findAll()) {
			if (auditTypeMap.get(auditType.getClassType()) == null)
				auditTypeMap.put(auditType.getClassType(), new ArrayList<AuditType>());
			auditTypeMap.get(auditType.getClassType()).add(auditType);
		}
		return auditTypeMap;
	}

	public Map<AuditType, List<AuditQuestion>> getQuestionMap() {
		Map<AuditType, List<AuditQuestion>> questionMap = new TreeMap<AuditType, List<AuditQuestion>>();

		for (AuditQuestion question : questionDAO.findFlaggableQuestions()) {
			if (questionMap.get(question.getAuditType()) == null)
				questionMap.put(question.getAuditType(), new ArrayList<AuditQuestion>());

			questionMap.get(question.getAuditType()).add(question);
		}

		return questionMap;
	}

	public String[] getComparisonList() {
		return new String[] { "=", "!=", "NOT EMPTY", "CONTAINS", "<", ">" };
	}

	public String[] getDatatypeList() {
		return new String[] { FlagCriteria.BOOLEAN, FlagCriteria.DATE, FlagCriteria.NUMBER, FlagCriteria.STRING };
	}

	public MultiYearScope[] getScopeList() {
		return new MultiYearScope[] { MultiYearScope.LastYearOnly, MultiYearScope.TwoYearsAgo,
				MultiYearScope.ThreeYearsAgo, MultiYearScope.ThreeYearAverage };
	}
}
