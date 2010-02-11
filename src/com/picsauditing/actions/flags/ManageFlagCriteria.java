package com.picsauditing.actions.flags;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;

public class ManageFlagCriteria extends PicsActionSupport implements Preparable {

	private AuditTypeDAO auditTypeDAO;
	private AuditQuestionDAO questionDAO;
	private FlagCriteriaDAO criteriaDAO;

	private FlagCriteria criteria;
	private JSONObject json;

	public ManageFlagCriteria(AuditTypeDAO auditTypeDAO,
			AuditQuestionDAO questionDAO, FlagCriteriaDAO criteriaDAO) {
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
							put("text", "Flag Criteria " + criteria.getLabel()
									+ " saved successfully.");
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

	public List<FlagCriteria> getCriteriaList() {
		return criteriaDAO.findAll();
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

	public List<AuditType> getAuditTypeList() {
		return auditTypeDAO.findAll();
	}

	public List<AuditQuestion> getFlagQuestionList() {
		return questionDAO.findWhere("isRedFlagQuestion = 'Yes'");
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
