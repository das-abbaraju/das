package com.picsauditing.actions.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;

public class ManageFlagCriteria extends PicsActionSupport implements Preparable {

	private AuditTypeDAO auditTypeDAO;
	private AuditQuestionDAO questionDAO;
	private FlagCriteriaDAO criteriaDAO;

	private FlagCriteria criteria;
	private JSONObject json;

	public ManageFlagCriteria(AuditTypeDAO auditTypeDAO, AuditQuestionDAO questionDAO, FlagCriteriaDAO criteriaDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.questionDAO = questionDAO;
		this.criteriaDAO = criteriaDAO;
	}

	@Override
	public void prepare() throws Exception {
		int criteriaID = getParameter("criteria.id");
		if (criteriaID > 0)
			criteria = criteriaDAO.find(criteriaID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if ("load".equals(button)) {
			if (criteria != null) {
				json = criteria.toJSON();
			}

			return JSON;
		}

		if ("save".equals(button)) {
			// TODO - is validation required?
			if (criteria != null) {
				criteriaDAO.save(criteria);
			}

			json = new JSONObject() {
				{
					put("gritter", new JSONObject() {
						{
							put("title", "Criteria Saved");
							put("text", "Flag Criteria " + criteria.getLabel() + " saved successfully.");
						}
					});
					put("result", "success");
					put("data", criteria.toJSON());
				}
			};

			return JSON;
		}

		return SUCCESS;
	}

	public List<BaseTable> getCriteriaList() {
		return criteriaDAO.findAll(FlagCriteria.class);
	}

	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
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

	public List<AuditQuestion> getFlagQuestionList() {
		return questionDAO.findWhere("isRedFlagQuestion = 'Yes'");
	}

	@SuppressWarnings("unchecked")
	public Map getFlagQuestionMap() {
		Map<AuditTypeClass, Map<AuditType, Map<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>>> flagQuestionMap = new TreeMap<AuditTypeClass, Map<AuditType, Map<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>>>();

		for (AuditQuestion question : questionDAO.findWhere("isRedFlagQuestion = 'Yes'")) {
			if (flagQuestionMap.get(question.getAuditType().getClassType()) == null) {
				flagQuestionMap.put(question.getAuditType().getClassType(),
						new TreeMap<AuditType, Map<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>>());
			}

			if (flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()) == null) {
				flagQuestionMap.get(question.getAuditType().getClassType()).put(question.getAuditType(),
						new TreeMap<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>());
			}

			if (flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).get(
					question.getSubCategory().getCategory()) == null) {
				flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).put(
						question.getSubCategory().getCategory(), new TreeMap<AuditSubCategory, List<AuditQuestion>>());
			}

			if (flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).get(
					question.getSubCategory().getCategory()).get(question.getSubCategory()) == null) {
				flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).get(
						question.getSubCategory().getCategory()).put(question.getSubCategory(), new ArrayList<AuditQuestion>());
			}

			flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).get(
					question.getSubCategory().getCategory()).get(question.getSubCategory()).add(question);
		}

		return flagQuestionMap;
	}

	public MultiYearScope[] getMultiYearScopeList() {
		return MultiYearScope.values();
	}

	public List<String> getComparisonList() {
		return new ArrayList<String>() {
			{
				add("<");
				add("=");
				add(">");
			}
		};
	}
}
