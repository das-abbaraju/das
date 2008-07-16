package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
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

	public String execute() {
		if( button != null ) {
			
			if( button.equalsIgnoreCase("Save")) {
				save();
			}
			
		}
		
		return SUCCESS;
	}


	protected void load(int id) {
		if (id != 0) {
			load(auditTypeDao.find(id));
		}
	}
	
	protected void load( AuditType newType ) {
		this.auditType = newType;
	}

	
	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("id");

		if (ids != null && ids.length != 0) {
			
			int thisId = Integer.parseInt( ids[0] ); 
			load( thisId );
		}
	}
	
	public String save() {

		if( auditType != null ) {
			auditTypeDao.save(auditType);
		}
		return SUCCESS;
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
