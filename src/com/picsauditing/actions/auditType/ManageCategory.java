package com.picsauditing.actions.auditType;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;

public class ManageCategory extends ManageAuditType {

	protected AuditCategoryDAO auditCategoryDao;

	public ManageCategory(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao) {
		super(auditTypeDao);
		this.auditCategoryDao = auditCategoryDao;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditCategoryDao.find(id));
		}
	}

	@Override
	protected void loadParent(int id) {
		super.load(id);
	}
	
	protected void load(AuditCategory newCategory) {
		this.category = newCategory;
		load(category.getAuditType());
	}
	
	public void save() {
		if (category != null) {
			category = auditCategoryDao.save(category);
			load(category);
		}
	}
	
	private void delete() {
		try {
			if (category.getSubCategories().size() > 0) {
				message = "Can't delete - Sub Categories still exist";
				return;
			}
			
			auditCategoryDao.remove(category.getId());
			category = null;
		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
	}

}
