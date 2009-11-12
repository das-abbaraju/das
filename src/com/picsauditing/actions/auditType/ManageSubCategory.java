package com.picsauditing.actions.auditType;

import org.json.simple.JSONArray;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.Country;

@SuppressWarnings("serial")
public class ManageSubCategory extends ManageCategory {

	protected AuditSubCategoryDAO auditSubCategoryDao;
	protected CountryDAO countryDAO;
	protected String countries;

	public ManageSubCategory(AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao, CountryDAO countryDAO) {
		super(auditTypeDao, auditCategoryDao);
		this.auditSubCategoryDao = auditSubCategoryDao;
		this.countryDAO = countryDAO;
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
			StringBuilder newCountries = new StringBuilder("|");
			for (String country : countries.split("\\|")) {
				newCountries.append(country).append("|");
			}
			subCategory.setCountries(newCountries.toString());
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

	@SuppressWarnings("unchecked")
	public JSONArray getData() {
		JSONArray json = new JSONArray();

		for (Country country : countryDAO.findAll()) {
			json.add(country.getEnglish());
		}

		return json;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}
}
