package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.util.AuditTypeCache;

@SuppressWarnings("serial")
public class ManageAuditType extends PicsActionSupport implements Preparable {

	protected int id = 0;
	protected AuditType auditType = null;
	protected AuditCategory category = null;
	protected AuditSubCategory subCategory = null;
	protected AuditQuestion question = null;

	private List<AuditType> auditTypes = null;

	protected AuditTypeDAO auditTypeDao = null;

	public ManageAuditType(AuditTypeDAO auditTypeDAO) {
		this.auditTypeDao = auditTypeDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ManageAudits);

		if (button != null) {
			if (button.equalsIgnoreCase("save")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
				if (save()) {
					addActionMessage("Successfully saved"); // default message
					new AuditTypeCache();
					return "saved";
				}
			}
			if (button.equalsIgnoreCase("delete")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Delete);
				if (delete()) {
					addActionMessage("Successfully removed"); // default message
					new AuditTypeCache();					 
					return "deleted";
				}
			}
			if(button.equalsIgnoreCase("updateAllAudits")) {
				auditTypeDao.updateAllAudits(id);
				return "saved";
			}
			if(button.equalsIgnoreCase("updateAllAuditsCategories")) {
				auditTypeDao.updateAllCategories(auditType.getId(), id);
				return "saved";
			}
		}

		if ("Add New".equals(button)) {
			auditType = new AuditType();
			return SUCCESS;
		}
		if (auditType != null) {
			return SUCCESS;
		}

		return "top";
	}

	protected void load(int id) {
		if (id != 0) {
			load(auditTypeDao.find(id));
		}
	}

	protected void loadParent(int id) {
		// do nothing
	}

	protected void load(AuditType newType) {
		this.auditType = newType;
	}

	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("id");

		String[] parentIds = (String[]) ActionContext.getContext().getParameters().get("parentID");

		if (ids != null && ids.length > 0) {
			int thisId = Integer.parseInt(ids[0]);
			if (thisId > 0) {
				load(thisId);
				return; // don't try to load the parent too
			}
		}

		if (parentIds != null && parentIds.length > 0) {
			int thisId = Integer.parseInt(parentIds[0]);
			loadParent(thisId);
		}
	}

	public boolean save() {
		try {
			if (auditType == null)
				return false;
			if (auditType.getAuditName() == null || auditType.getAuditName().length() == 0) {
				addActionError("Audit name is required");
				return false;
			}
			auditType.setAuditColumns(permissions);
			auditType = auditTypeDao.save(auditType);
			id = auditType.getId();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	protected boolean delete() {
		try {
			if (auditType.getCategories().size() > 0) {
				addActionError("Can't delete - Categories still exist");
				return false;
			}

			auditTypeDao.remove(auditType.getId());
			id = auditType.getId();
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	// GETTERS && SETTERS

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AuditType> getAuditTypes() {
		if (auditTypes == null) {
			auditTypes = auditTypeDao.findAll();
		}
		return auditTypes;
	}

	public void setAuditTypes(List<AuditType> auditTypes) {
		this.auditTypes = auditTypes;
	}

	public AuditType getAuditType() {
		return auditType;
	}
	
	public AuditTypeClass[] getClassList() {
		return AuditTypeClass.values();
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	public AuditSubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(AuditSubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public void setParentID(int parentID) {
		// Do nothing here...we use this in the prepare statement
	}
}
