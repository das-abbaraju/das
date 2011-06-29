package com.picsauditing.actions.flags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageFlagCriteria extends PicsActionSupport {

	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditQuestionDAO questionDAO;
	@Autowired
	private FlagCriteriaDAO criteriaDAO;

	private FlagCriteria criteria;

	@Override
	public String execute() {

		return SUCCESS;
	}

	public String save() throws IOException {
		if (criteria != null) {
			if (criteria.getAuditType() == null && criteria.getQuestion() == null) {
				addActionError("Either a question or an audit type is required.");
			}

			if (Strings.isEmpty(criteria.getDataType())) {
				addActionError("DataType is a required field.");
			}

			if (criteria.getAuditType() != null && criteria.getAuditType().isAnnualAddendum()
					&& criteria.getRequiredStatus() == null) {
				addActionError("Audit Status cannot be null when Audit Type Annual Update is selected.");
			}

			if (Strings.isEmpty(criteria.getDefaultValue())) {
				addActionError("Default hurdle is a required field.");
			}

			if (hasActionErrors()) {
				if (criteriaDAO.isContained(criteria))
					criteriaDAO.refresh(criteria);
				return INPUT;
			}

			criteria.setAuditColumns(permissions);

			criteriaDAO.save(criteria);
			addActionMessage("Criteria saved successfully.");

			this.redirect("ManageFlagCriteria!edit.action?criteria=" + criteria.getId());
		}
		return SUCCESS;
	}

	public String edit() {
		if (criteria == null)
			criteria = new FlagCriteria();

		return INPUT;
	}

	public List<FlagCriteria> getCriteriaList() {
		return criteriaDAO.findAll();
	}

	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	public List<AuditType> getAuditTypes() {
		List<AuditType> auditTypes = auditTypeDAO.findAll();
		Collections.sort(auditTypes, new Comparator<AuditType>() {
			@Override
			public int compare(AuditType o1, AuditType o2) {
				if (o1.getName() == null || o1.getName().toString() == null)
					return -1;
				if (o2.getName() == null || o2.getName().toString() == null)
					return 1;
				return o1.getName().toString().compareTo(o2.getName().toString());
			}
		});
		return auditTypes;
	}

	public Map<AuditTypeClass, List<AuditType>> getAuditTypeMap() {
		return auditTypeDAO.getAuditTypeMap();
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

	public String[] getCriteriaCategory() {
		return new String[] { "Audits", "Insurance", "Insurance Criteria", "Paperwork", "Safety", "Statistics" };
	}

}
