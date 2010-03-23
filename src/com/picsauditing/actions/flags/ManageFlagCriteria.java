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
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageFlagCriteria extends PicsActionSupport implements Preparable {

	private AuditTypeDAO auditTypeDAO;
	private AuditQuestionDAO questionDAO;
	private FlagCriteriaDAO criteriaDAO;

	private FlagCriteria criteria;
	private JSONObject json;

	private AuditType auditType;
	private AuditQuestion question;

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

		int auditTypeID = getParameter("auditType.id");
		if (auditTypeID > 0) {
			auditType = auditTypeDAO.find(auditTypeID);
		}

		int questionID = getParameter("question.id");
		if (questionID > 0) {
			question = questionDAO.find(questionID);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if ("load".equals(button)) {
			if (criteria != null) {
				json = new JSONObject() {
					{
						put("result", "success");
						put("criteria", criteria.toJSON());
					}
				};
			} else {
				json = new JSONObject() {
					{
						put("result", "failure");
						put("gritter", new JSONObject() {
							{
								put("title", "Criteria Not Loaded!");
								put("text", "There was no criteria to load.");
							}
						});
					}
				};
			}

			return JSON;
		}

		if ("save".equals(button)) {
			if (criteria != null) {
				if (auditType != null && question != null) {
					// clear anything that was put in here
					criteriaDAO.refresh(criteria);
					json = new JSONObject() {
						{
							put("result", "failure");
							put("gritter", new JSONObject() {
								{
									put("title", "Criteria Not Saved!");
									put("text", "Cannot save a flag criteria with both Audit and Question selected.");
								}
							});
							put("criteria", criteria.toJSON());
						}
					};

					return JSON;
				}

				// set the auditType or the question based on the incoming value
				if (criteria.getAuditType() == null || !criteria.getAuditType().equals(auditType)) {
					criteria.setAuditType(auditType);
				} else if (criteria.getQuestion() == null || !criteria.getQuestion().equals(question)) {
					criteria.setQuestion(question);
				}

				if (Strings.isEmpty(criteria.getDataType())) {
					json = new JSONObject() {
						{
							put("result", "failure");
							put("gritter", new JSONObject() {
								{
									put("title", "Criteria Not Saved!");
									put("text", "Data Type is a required field.");
								}
							});
						}
					};
					criteriaDAO.refresh(criteria);
					return JSON;
				}

				try {
					criteriaDAO.save(criteria);

					json = new JSONObject() {
						{
							put("result", "success");
							put("gritter", new JSONObject() {
								{
									put("title", "Criteria Saved");
									put("text", "Flag Criteria " + criteria.getLabel() + " saved successfully.");
								}
							});
							put("criteria", criteria.toJSON());
						}
					};
				} catch (final Exception e) {
					json = new JSONObject() {
						{
							put("result", "failure");
							put("gritter", new JSONObject() {
								{
									put("title", "Criteria Not Saved!");
									put("text", "Flag Criteria " + criteria.getLabel() + " not saved. "
											+ e.getMessage());
								}
							});
							put("criteria", criteria.toJSON());
						}
					};
					
					return JSON;
				}
			} else {
				json = new JSONObject() {
					{
						put("result", "failure");
						put("gritter", new JSONObject() {
							{
								put("title", "Criteria Not Saved!");
								put("text", "There was no criteria to save.");
							}
						});
					}
				};
			}

			return JSON;
		}

		if ("delete".equals(button)) {
			if (criteria != null) {
				final int criteriaID = criteria.getId();

				criteriaDAO.remove(criteria);
				json = new JSONObject() {
					{
						put("result", "success");
						put("gritter", new JSONObject() {
							{
								put("title", "Criteria Deleted");
								put("text", "Flag Criteria " + criteria.getLabel() + " removed successfully.");
							}
						});
						put("id", criteriaID);
					}
				};
			} else {
				json = new JSONObject() {
					{
						put("result", "failure");
						put("gritter", new JSONObject() {
							{
								put("title", "Criteria Not Deleted!");
								put("text", "Flag criteria does not exist");
							}
						});
					}
				};
			}

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

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
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
		return questionDAO.findFlaggableQuestions();
	}

	@SuppressWarnings("unchecked")
	public Map getFlagQuestionMap() {
		Map<AuditTypeClass, Map<AuditType, Map<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>>> flagQuestionMap = new TreeMap<AuditTypeClass, Map<AuditType, Map<AuditCategory, Map<AuditSubCategory, List<AuditQuestion>>>>>();

		for (AuditQuestion question : questionDAO.findFlaggableQuestions()) {
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
						question.getSubCategory().getCategory()).put(question.getSubCategory(),
						new ArrayList<AuditQuestion>());
			}

			flagQuestionMap.get(question.getAuditType().getClassType()).get(question.getAuditType()).get(
					question.getSubCategory().getCategory()).get(question.getSubCategory()).add(question);
		}

		return flagQuestionMap;
	}

	public String[] getComparisonList() {
		return new String[] { "=", "!=", "NOT EMPTY", "CONTAINS", "<", ">" };
	}

	public String[] getDatatypeList() {
		return new String[] { FlagCriteria.BOOLEAN, FlagCriteria.DATE, FlagCriteria.NUMBER, FlagCriteria.STRING };
	}
}
