package com.picsauditing.actions.auditType;

import java.net.URLEncoder;
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
				message = "Successfully saved"; // default message
				save();
			}
			if (button.equalsIgnoreCase("delete")) {
				permissions.tryPermission(OpPerms.ManageAudits, OpType.Delete);
				message = "Successfully removed"; // default message
				delete();
			}
		}
		
		if (auditType == null && !"Add New".equals(button))
			return "top";
		
		return SUCCESS;
	}


	protected void load(int id) {
		if (id != 0) {
			load(auditTypeDao.find(id));
		}
	}
	
	protected void loadParent(int id) {
		// do nothing
	}
	
	protected void load( AuditType newType ) {
		this.auditType = newType;
	}

	
	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("id");

		String[] parentIds = (String[]) ActionContext.getContext().getParameters()
		.get("parentID");

		if (ids != null && ids.length > 0) {
			int thisId = Integer.parseInt( ids[0] ); 
			load( thisId );
		}
		
		if (parentIds != null && parentIds.length > 0) {
			int thisId = Integer.parseInt( parentIds[0] ); 
			loadParent( thisId );
		}
	}
	
	public void save() {
		try {
			auditType = auditTypeDao.save(auditType);
			load(auditType);

		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
	}

	protected void delete() {
		try {
			if (auditType.getCategories().size() > 0) {
				message = "Can't delete - Categories still exist";
				return;
			}
			
			auditTypeDao.remove(auditType.getAuditTypeID());
			auditType = null;
		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
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

}
