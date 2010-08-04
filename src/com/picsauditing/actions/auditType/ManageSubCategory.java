package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionTextDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Country;

@SuppressWarnings("serial")
public class ManageSubCategory extends ManageCategory {

	protected CountryDAO countryDAO;
	protected String countries;
	protected boolean exclude = false;
	protected int auditTypeID;

	public ManageSubCategory(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao, AuditSubCategoryDAO auditSubCategoryDao,
			AuditQuestionDAO auditQuestionDao, CountryDAO countryDAO, AuditQuestionTextDAO auditQuestionTextDao) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao, auditSubCategoryDao, auditQuestionDao,
				auditQuestionTextDao);
		this.countryDAO = countryDAO;
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditSubCategoryDAO.find(id));
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
				for (AuditSubCategory sibling : category.getSubCategories()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				subCategory.setNumber(maxID + 1);
			}
			subCategory.setAuditColumns(permissions);
			subCategory.setCountriesArray(countries.split("\\|"), exclude);
			subCategory = auditSubCategoryDAO.save(subCategory);
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
			auditSubCategoryDAO.remove(subCategory.getId());
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}
	
	@Override
	protected boolean copy() {
		try {
			if (targetID == 0) {
				addActionMessage("Please Select Category to copy to");
				return false;
			}
			// if (auditTypeDAO.findWhere(
			// // ADD CHECK FOR EXISTING CATEGORY!!
			// "auditName LIKE '" + auditType.getAuditName() + "'")
			// .size() > 0) {
			// addActionMessage("The Category Name is not Unique");
			// return false;
			// }

			AuditCategory targetCategory = auditCategoryDAO.find(targetID);
			AuditSubCategory asc = copyAuditSubCategory(subCategory, targetCategory);

			addActionMessage("Copied the SubCategory only. <a href=\"ManageSubCategory.action?id="
					+ asc.getId() + "\">Go to this SubCategory?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected boolean copyAll() {
		try {
			if (targetID == 0) {
				addActionMessage("Please Select Category to copy to");
				return false;
			}
			// if (auditTypeDAO.findWhere(
			// // ADD CHECK FOR EXISTING CATEGORY!!
			// "auditName LIKE '" + auditType.getAuditName() + "'")
			// .size() > 0) {
			// addActionMessage("The Category Name is not Unique");
			// return false;
			// }

			int id = copyAllRecursive();

			addActionMessage("Copied all related Subcategories and Questions. <a href=\"ManageSubCategory.action?id="
					+ id + "\">Go to this SubCategory?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected boolean move() {
		try {
			if (targetID == 0) {
				addActionMessage("Please Select Category to move to");
				return false;
			}

			AuditCategory targetCategory = auditCategoryDAO.find(targetID);
			subCategory.setCategory(targetCategory);
			auditSubCategoryDAO.save(subCategory);

			addActionMessage("Moved SubCategory Successfully. <a href=\"ManageSubCategory.action?id="
					+ subCategory.getId() + "\">Go to this SubCategory?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Transactional
	@Override
	protected int copyAllRecursive() {
		AuditSubCategory originalSubCategory = auditSubCategoryDAO.find(originalID);
		AuditCategory targetCategory = auditCategoryDAO.find(targetID);

		// Copying SubCategory
		AuditSubCategory categoryCopy = copyAuditSubCategory(subCategory, targetCategory);

			// Copying Questions
			for (AuditQuestion question : originalSubCategory.getQuestions())
				copyAuditQuestion(question, categoryCopy);

		auditCategoryDAO.save(categoryCopy);

		return categoryCopy.getId();
	}
	
	public List<AuditCategory> getCategories(){
		if(auditTypeID > 0) {
			AuditType targetAudit = auditTypeDAO.find(auditTypeID);
			return targetAudit.getCategories();
		} else
			return new ArrayList<AuditCategory>();
	}

	@SuppressWarnings("unchecked")
	public JSONArray getData() {
		JSONArray json = new JSONArray();

		for (Country country : countryDAO.findAll()) {
			JSONObject o = new JSONObject();
			o.put("id", country.getIsoCode());
			o.put("name", country.getName(permissions.getLocale()));
			json.add(o);
		}

		return json;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getInitialCountries() {
		JSONArray json = new JSONArray();

		for (String c : subCategory.getCountriesArray()) {
			Country country = countryDAO.find(c);
			if (country != null) {
				JSONObject o = new JSONObject();
				o.put("id", country.getIsoCode());
				o.put("name", country.getName(permissions.getLocale()));
				json.add(o);
			}
		}

		return json;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

}