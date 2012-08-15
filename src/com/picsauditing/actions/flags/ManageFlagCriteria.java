package com.picsauditing.actions.flags;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.i18n.RequiredLanguagesSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOptionCode;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageFlagCriteria extends RequiredLanguagesSupport {

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
			if (Strings.isEmpty(criteria.getCategory())) {
				addActionError("Category is a required field.");
			}
			if (criteria.getDisplayOrder() < 0) {
				addActionError("Display Order must be a positive number or 0.");
			}
			if (Strings.isEmpty(criteria.getLabel().toString())) {
				addActionError("Label is a required field.");
			}
			if (Strings.isEmpty(criteria.getDescription().toString())) {
				addActionError("Description is a required field.");
			}
			if (Strings.isEmpty(criteria.getComparison())) {
				criteria.setComparison("=");
			}
			
			if (Strings.isEmpty(criteria.getDataType())) {
				addActionError("DataType is a required field.");
			} else if (!"NOT EMPTY".equals(criteria.getComparison())){
				if ("boolean".equals(criteria.getDataType())) {
					if (!("true".equals(criteria.getDefaultValue())
							|| "false".equals(criteria.getDefaultValue())
							|| "".equals(criteria.getDefaultValue())))
						addActionError("Default hurdle must be true, false, or empty");
				}
				if ("date".equals(criteria.getDataType()) && Strings.isEmpty(criteria.getDefaultValue())) {
					addActionError("Default hurdle is a required field.");
				}
				if ("number".equals(criteria.getDataType())
						&& criteria.getCategory().indexOf("AMB") == -1) {
					try {
						BigDecimal number = new BigDecimal(
								criteria.getDefaultValue());
					} catch (Exception e) {
						addActionError("Default hurdle needs to be a valid number.");
					}
				}
			}

			if (criteria.getAuditType() != null && criteria.getAuditType().isAnnualAddendum()
					&& criteria.getRequiredStatus() == null) {
				addActionError("Audit Status cannot be null when Audit Type Annual Update is selected.");
			}

			if (criteria.getAuditType() != null && criteria.getQuestion() != null) {
				addActionError("Audit Type and Question cannot both be set.");
			}

			if (criteria.getOshaType() != null && criteria.getMultiYearScope() == null) {
				addActionError("Multi-Year Scope must be set.");
			}

			//if (criteria.getRequiredStatusComparison() != null && !criteria.getRequiredStatusComparison().equals("NOT EMPTY") && criteria.getRequiredStatus() != null && criteria.getAuditType() == null) {
			//	addActionError("Audit Type must be set.");
			//}

			if (criteria.hasMissingChildRequiredLanguages()) {
				addActionError("Changes to required languages must always have at least one language left. "
						+ "Make sure your flag criteria has at least one language.");
			}
			
			if (hasActionErrors()) {
				if (criteriaDAO.isContained(criteria))
					criteriaDAO.refresh(criteria);
				return INPUT;
			}

			criteria.setAuditColumns(permissions);

			criteriaDAO.save(criteria);
			addActionMessage("Criteria saved successfully.");

			return this.setUrlForRedirect("ManageFlagCriteria!edit.action?criteria=" + criteria.getId());
		}

		return SUCCESS;
	}

	public String edit() {
		if (criteria == null) {
			criteria = new FlagCriteria();
			addUserPreferredLanguage(criteria);
		}

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
		return new String[] { "Audits", "Insurance", "Insurance AMB Class", "Insurance AMB Rating",
				"Insurance Criteria", "Paperwork", "Safety", "Statistics" };
	}

	public String[] getOptionCodeList() {
		return new String[] { FlagCriteriaOptionCode.None.toString(),
				FlagCriteriaOptionCode.ExcessAggregate.toString(),
				FlagCriteriaOptionCode.ExcessEachOccurrence.toString() };
	}

	@Override
	protected void fillSelectedLocales() {
		if (criteria != null && !criteria.getLanguages().isEmpty()) {
			for (String language : criteria.getLanguages()) {
				selectedLocales.add(new Locale(language));
			}
		}
	}
}
