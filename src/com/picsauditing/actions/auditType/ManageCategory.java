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
			id = category.getAuditType().getAuditTypeID();
		}
	}
	
	protected void delete() {
		try {
			if (category.getSubCategories().size() > 0) {
				addActionError("Can't delete - Sub Categories still exist");
				return;
			}
			
			id = category.getAuditType().getAuditTypeID();
			auditCategoryDao.remove(category.getId());
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
	}

}
