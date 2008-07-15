package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;

public class ManageCategory extends ManageAuditType {

	
	protected AuditCategoryDAO auditCategoryDao = null;
	protected AuditCategory category = null;

	public ManageCategory(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao) {
		super(auditTypeDao);
		this.auditCategoryDao = auditCategoryDao;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load( auditCategoryDao.find(id));
		}
	}

	protected void load(AuditCategory newCategory) {
		this.category = newCategory;
		load(category.getAuditType());
	}
	
	public String execute() {
		return SUCCESS;
	}
	
	
	public String delete() {
		return SUCCESS;
	}
	
	
	
	
	public String save() {
		if( category != null ) {
			auditCategoryDao.save(category);
		}
		return SUCCESS;
	}

}
