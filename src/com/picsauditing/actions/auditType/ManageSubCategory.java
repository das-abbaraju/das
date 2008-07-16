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

	protected void load(AuditSubCategory o) {
		this.subCategory = o;
		load(subCategory.getCategory());
	}
	
	public void save() {
		if( subCategory != null ) {
			auditSubCategoryDao.save(subCategory);
		}
	}

	
}
