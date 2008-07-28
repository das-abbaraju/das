package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditSubCategory;

public class ManageSubCategory extends ManageCategory {

	protected AuditSubCategoryDAO auditSubCategoryDao;

	public ManageSubCategory(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao) {
		super(auditTypeDao, auditCategoryDao);
		this.auditSubCategoryDao = auditSubCategoryDao;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditSubCategoryDao.find(id));
		}
	}

	@Override
	protected void loadParent(int id) {
		super.load(id);
	}
	
	protected void load(AuditSubCategory newSubCategory) {
		this.subCategory = newSubCategory;
		load(subCategory.getCategory());
	}
	
	public void save() {
		if (subCategory != null) {
			subCategory = auditSubCategoryDao.save(subCategory);
			load(subCategory);
		}
	}
	
	protected void delete() {
		try {
			if (subCategory.getQuestions().size() > 0) {
				message = "Can't delete - Questions still exist";
				return;
			}
			
			auditSubCategoryDao.remove(subCategory.getId());
			subCategory = null;
		} catch (Exception e) {
			message = "Error - " + e.getMessage();
		}
	}	

	
}
