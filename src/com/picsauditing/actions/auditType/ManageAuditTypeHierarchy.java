package com.picsauditing.actions.auditType;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

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
					result.add(q.getNumber() + ". " + q.getName());
				}

				for (final AuditCategory cat : category.getSubCategories()) {
					result.add(new JSONObject() {
						{
							put("attr", new JSONObject() {
								{
									put("id", "category_" + cat.getId());
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
				JSONArray children = new JSONArray();
				for (final AuditCategory cat : auditType.getCategories()) {
					children.add(new JSONObject() {
						{
							put("attr", new JSONObject() {
								{
									put("id", "category_" + cat.getId());
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
}
