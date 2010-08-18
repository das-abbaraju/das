package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.quickbooks.qbxml.APAccountRef;

@SuppressWarnings("serial")
public class ManageAuditTypeHierarchy extends PicsActionSupport {

	private AuditTypeDAO auditTypeDAO;
	private AuditCategoryDAO auditCategoryDAO;
	private AuditQuestionDAO auditQuestionDAO;

	private List<AuditType> auditTypeList;
	private AuditType auditType;
	private int id;
	private String type;
	private int nodeID;
	private String parentType;
	private int parentID;
	private int number;

	private String[] types;
	private int[] ids;

	public ManageAuditTypeHierarchy(AuditTypeDAO auditTypeDAO, AuditCategoryDAO auditCategoryDAO,
			AuditQuestionDAO auditQuestionDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		auditType = auditTypeDAO.find(id);

		if ("json".equals(button)) {
			if ("category".equals(type)) {
				AuditCategory category = auditCategoryDAO.find(nodeID);
				JSONArray result = new JSONArray();
				for (final AuditQuestion q : category.getQuestions()) {
					result.add(new JSONObject() {
						{
							put("data", q.getNumber() + ". " + q.getName());
							put("attr", new JSONObject() {
								{
									put("id", "question_" + q.getId() + "_" + q.getNumber());
									put("rel", "question");
								}
							});
						}
					});
				}

				for (final AuditCategory cat : category.getSubCategories()) {
					result.add(new JSONObject() {
						{
							put("attr", new JSONObject() {
								{
									put("id", "category_" + cat.getId() + "_" + cat.getNumber());
									put("rel", "category");
								}
							});
							put("data", cat.getName());
							put("state", "closed");
						}
					});
				}

				json.put("result", result);
			} else {
				JSONObject result = new JSONObject();
				result.put("data", auditType.getAuditName());
				result.put("state", "open");
				result.put("attr", new JSONObject() {
					{
						put("id", "audit_" + auditType.getId() + "_" + auditType.getDisplayOrder());
						put("rel", "audit");
					}
				});
				JSONArray children = new JSONArray();
				for (final AuditCategory cat : auditType.getCategories()) {
					children.add(new JSONObject() {
						{
							put("attr", new JSONObject() {
								{
									put("id", "category_" + cat.getId() + "_" + cat.getNumber());
									put("rel", "category");
								}
							});
							put("data", cat.getName());
							put("state", "closed");
						}
					});
				}
				result.put("children", children);

				json.put("result", result);
			}

			return JSON;
		} else if ("move".equals(button)) {
			/*
			 * if ("category".equals(type)) { AuditCategory category =
			 * auditCategoryDAO.find(nodeID);
			 * 
			 * if ("audit".equals(parentType)) { category.setParent(null);
			 * category.setAuditType(auditTypeDAO.find(parentID)); } else if
			 * ("category".equals(parentType)) { category.setAuditType(null);
			 * category.setParent(auditCategoryDAO.find(parentID)); }
			 * 
			 * category.setNumber(number);
			 * 
			 * for (AuditCategory sibling : category.getSiblings()) { if
			 * (sibling.getNumber() >= number)
			 * sibling.setNumber(sibling.getNumber() + 1); }
			 * 
			 * auditCategoryDAO.save(category);
			 * 
			 * } else if ("question".equals(type)) { AuditQuestion question =
			 * auditQuestionDAO.find(nodeID); if ("category".equals(parentType))
			 * { question.setCategory(auditCategoryDAO.find(parentID));
			 * question.setNumber(number);
			 * 
			 * for (AuditQuestion sibling :
			 * question.getCategory().getQuestions()) { if (sibling.getNumber()
			 * >= number) sibling.setNumber(sibling.getNumber() + 1); }
			 * 
			 * auditQuestionDAO.save(question); } }
			 */

			if (types.length > 0 && types.length == ids.length) {

				json = new JSONObject();
				json.put("success", true);

				int questionNumber = 0;
				int categoryNumber = 0;

				List<AuditType> oldParentAudits = new ArrayList<AuditType>();
				List<AuditCategory> oldParentCategories = new ArrayList<AuditCategory>();

				for (int i = 0; i < types.length; i++) {
					if (types[i].equals("question")) {
						if ("category".equals(parentType)) {
							AuditQuestion question = auditQuestionDAO.find(ids[i]);
							question.setNumber(++questionNumber);
							oldParentCategories.add(question.getCategory());
							question.setCategory(auditCategoryDAO.find(parentID));

							auditQuestionDAO.save(question);
						}
					} else if (types[i].equals("category")) {
						AuditCategory category = auditCategoryDAO.find(ids[i]);
						category.setNumber(++categoryNumber);
						if (category.getParent() != null)
							oldParentCategories.add(category.getParent());
						else if (category.getAuditType() != null)
							oldParentAudits.add(category.getAuditType());

						if ("category".equals(parentType)) {
							category.setAuditType(null);
							category.setParent(auditCategoryDAO.find(parentID));
						} else if ("audit".equals(parentType)) {
							category.setParent(null);
							category.setAuditType(auditTypeDAO.find(parentID));
						}

						auditCategoryDAO.save(category);
					}
				}

				for (AuditType auditType : oldParentAudits) {
					int number = 0;
					for (AuditCategory category : auditType.getCategories()) {
						category.setNumber(++number);
					}

					auditTypeDAO.save(auditType);
				}

				for (AuditCategory category : oldParentCategories) {
					{
						int number = 0;
						for (AuditCategory sub : category.getSubCategories()) {
							sub.setNumber(++number);
						}
					}
					{
						int number = 0;
						for (AuditQuestion question : category.getQuestions()) {
							question.setNumber(++number);
						}
					}

					auditCategoryDAO.save(category);
				}

			} else {
				json.put("success", false);
			}

			return JSON;
		}

		return SUCCESS;
	}

	public List<AuditType> getAuditTypeList() {
		if (auditTypeList == null) {
			auditTypeList = auditTypeDAO.findAll();

		}
		return auditTypeList;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}
}
