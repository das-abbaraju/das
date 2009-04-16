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
	
	public boolean save() {
		if (subCategory != null) {
			if (subCategory.getSubCategory() == null || subCategory.getSubCategory().length() == 0) {
				this.addActionError("Subcategory name is required");
				return false;
			}
			if (subCategory.getNumber() == 0) {
				int maxID = 0;
				for(AuditSubCategory sibling : category.getSubCategories()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				subCategory.setNumber(maxID + 1);
			}
			subCategory.setAuditColumns(permissions);
			subCategory = auditSubCategoryDao.save(subCategory);
			id = subCategory.getCategory().getId();
			return true;
		}
		return false;
	}
	
	protected boolean delete() {
		try {
			if (subCategory.getQuestions().size() > 0) {
				addActionError("Can't delete - Questions still exist");
				return false;
			}
			
			id = subCategory.getCategory().getId();
			auditSubCategoryDao.remove(subCategory.getId());
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}	

	
}
