package com.picsauditing.actions.auditType;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionTextDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ManageCategory extends ManageAuditType {

	protected Integer applyOnQuestionID;

	public ManageCategory(EmailTemplateDAO emailTemplateDAO,
			AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCategoryDao,
			AuditSubCategoryDAO auditSubCategoryDao,
			AuditQuestionDAO auditQuestionDao,
			AuditQuestionTextDAO auditQuestionTextDao) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao,
				auditSubCategoryDao, auditQuestionDao, auditQuestionTextDao);
	}

	@Override
	protected void load(int id) {
		if (id != 0) {
			load(auditCategoryDAO.find(id));
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
			if (category.getCategory() == null
					|| category.getCategory().length() == 0) {
				this.addActionError("Category name is required");
				return false;
			}
			if (category.getNumber() == 0) {
				int maxID = 0;
				for (AuditCategory sibling : auditType.getCategories()) {
					if (sibling.getNumber() > maxID)
						maxID = sibling.getNumber();
				}
				category.setNumber(maxID + 1);
			}
			category.setAuditColumns(permissions);
			category = auditCategoryDAO.save(category);
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
			auditCategoryDAO.remove(category.getId());
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

			AuditType targetAudit = auditTypeDAO.find(targetID);
			AuditCategory ac = copyAuditCategory(category, targetAudit);

			addActionMessage("Copied the Category only. <a href=\"ManageCategory.action?id="
					+ ac.getId() + "\">Go to this Category?</a>");
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

			addActionMessage("Copied Category, and all related Subcategories and Questions. <a href=\"ManageCategory.action?id="
					+ id + "\">Go to this Category?</a>");
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

			AuditType targetAudit = auditTypeDAO.find(targetID);
			category.setAuditType(targetAudit);
			auditCategoryDAO.save(category);

			addActionMessage("Moved Category Successfully. <a href=\"ManageCategory.action?id="
					+ category.getId() + "\">Go to this Category?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Transactional
	@Override
	protected int copyAllRecursive() {
		AuditCategory originalAudit = auditCategoryDAO.find(originalID);
		AuditType targetAudit = auditTypeDAO.find(targetID);

		// Copying Category
		AuditCategory categoryCopy = copyAuditCategory(category, targetAudit);

		// Copying Subcategories
		if (originalAudit.getSubCategories() == null)
			return categoryCopy.getId();
		for (AuditSubCategory subcategory : originalAudit.getSubCategories()) {
			AuditSubCategory subcategoryCopy = copyAuditSubCategory(
					subcategory, categoryCopy);

			// Copying Questions
			for (AuditQuestion question : subcategory.getQuestions())
				copyAuditQuestion(question, subcategoryCopy);
		}

		auditCategoryDAO.save(categoryCopy);

		return categoryCopy.getId();
	}
}
