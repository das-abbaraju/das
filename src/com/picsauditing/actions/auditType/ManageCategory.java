package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;

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
	
	public boolean save() {
		if (category != null) {
			if (category.getCategory() == null || category.getCategory().length() == 0) {
				this.addActionError("Category name is required");
				return false;
			}
			if (category.getNumber() == 0) {
				int maxID = 0;
				for(AuditCategory sibling : auditType.getCategories()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				category.setNumber(maxID + 1);
			}
			category.setAuditColumns(permissions);
			category = auditCategoryDao.save(category);
			id = category.getAuditType().getId();
			return true;
		}
		return false;
	}
	
	protected boolean delete() {
		try {
			if (category.getSubCategories().size() > 0) {
				addActionError("Can't delete - Sub Categories still exist");
				return false;
			}
			
			id = category.getAuditType().getId();
			auditCategoryDao.remove(category.getId());
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

}
