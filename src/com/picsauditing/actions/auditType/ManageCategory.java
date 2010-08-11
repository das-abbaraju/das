package com.picsauditing.actions.auditType;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ManageCategory extends ManageAuditType implements Preparable {

	protected AuditCategory categoryParent;
	private int targetCategoryID = 0;

	public ManageCategory(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao, AuditQuestionDAO auditQuestionDao) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao, auditQuestionDao);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();

		String[] categoryParents = (String[]) ActionContext.getContext().getParameters().get("categoryParent.id");

		if (categoryParents != null && categoryParents.length > 0) {
			int thisId = Integer.parseInt(categoryParents[0]);
			categoryParent = auditCategoryDAO.find(thisId);
		}
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
		List<AuditCategory> ancestors = category.getAncestors();
		// Assuming that the earliest ancestor has the general audit type.
		super.load(ancestors.get(0).getAuditType());
	}

	public boolean save() {
		if (category != null) {
			List<AuditCategory> ancestors = null;

			if (category.getName() == null || category.getName().length() == 0) {
				this.addActionError("Category name is required");
				return false;
			}

			if (categoryParent != null) {
				category.setParent(categoryParent);
				ancestors = categoryParent.getAncestors();
				id = ancestors.get(0).getAuditType().getId();
			} else
				id = category.getAuditType().getId();

			if (category.getNumber() == 0) {
				int maxID = 0;
				if (category.getAuditType() != null) {
					AuditType audit = auditTypeDAO.find(category.getAuditType().getId());
					for (AuditCategory sibling : audit.getCategories()) {
						if (sibling.getNumber() > maxID)
							maxID = sibling.getNumber();
					}
				} else if (categoryParent != null && ancestors.size() > 0) {
					// Ancestors should be loaded
					categoryParent = ancestors.get(ancestors.size() - 1);
					for (AuditCategory sibling : categoryParent.getSubCategories()) {
						if (sibling.getNumber() > maxID)
							maxID = sibling.getNumber();
					}
				}
				category.setNumber(maxID + 1);
			}

			category.setAuditColumns(permissions);
			category = auditCategoryDAO.save(category);

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

			if (category.getAuditType() != null)
				id = category.getAuditType().getId();
			else
				id = category.getAncestors().get(0).getAuditType().getId();

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
			if (targetID == 0 && targetCategoryID == 0) {
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
			AuditCategory ac = null;

			if (targetID > 0) {
				AuditType targetAudit = auditTypeDAO.find(targetID);
				ac = copyAuditCategory(category, targetAudit);

			}
			if (targetCategoryID > 0) {
				AuditCategory targetCategory = auditCategoryDAO.find(targetCategoryID);
				
				int number = 1;
				if (targetCategory.getSubCategories().size() > 0) {
					for (AuditCategory subCategory : targetCategory.getSubCategories()) {
						if (number < subCategory.getNumber())
							number = subCategory.getNumber() + 1;
					}
				}
				
				ac = new AuditCategory(category);
				ac.setNumber(number);
				ac.setAuditColumns(permissions);
				ac.setParent(targetCategory);
				ac = auditCategoryDAO.save(ac);
			}

			addActionMessage("Copied the Category only. <a href=\"ManageCategory.action?id=" + ac.getId()
					+ "\">Go to this Category?</a>");

			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected boolean copyAll() {
		try {
			if (targetID == 0 && targetCategoryID == 0) {
				addActionMessage("Please Select Category to copy to");
				return false;
			}

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
			if (targetID == 0 && targetCategoryID == 0) {
				addActionMessage("Please Select Category to move to");
				return false;
			}

			if (targetID > 0) {
				AuditType targetAudit = auditTypeDAO.find(targetID);
				category.setAuditType(targetAudit);
				auditCategoryDAO.save(category);
			}
			
			if (targetCategoryID > 0) {
				AuditCategory parent = auditCategoryDAO.find(targetCategoryID);
				category.setParent(parent);
				category.setAuditType(null);
				auditCategoryDAO.save(category);
			}

			addActionMessage("Moved Category Successfully. <a href=\"ManageCategory.action?id=" + category.getId()
					+ "\">Go to this Category?</a>");
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
		AuditCategory categoryCopy = null;

		if (targetID > 0) {
			AuditType targetAudit = auditTypeDAO.find(targetID);
			
			int number = 1;
			for (AuditCategory cat : targetAudit.getCategories()) {
				if (number < cat.getNumber())
					number = cat.getNumber() + 1;
			}
	
			// Copying Category
			categoryCopy = copyTree(originalAudit, null, number);
			categoryCopy.setAuditType(targetAudit);
			categoryCopy = auditCategoryDAO.save(categoryCopy);
		}
		
		if (targetCategoryID > 0) {
			AuditCategory parent = auditCategoryDAO.find(targetCategoryID);
			
			int number = 1;
			for (AuditCategory cat : parent.getSubCategories()) {
				if (number < cat.getNumber())
					number = cat.getNumber() + 1;
			}
			
			categoryCopy = copyTree(originalAudit, parent, number);
		}

		return categoryCopy.getId();
	}

	@Transactional
	protected AuditCategory copyTree(AuditCategory category, AuditCategory parent, int categoryNumber) {
		AuditCategory categoryCopy = new AuditCategory(category);
		categoryCopy.setParent(parent);
		categoryCopy.setNumber(categoryNumber);
		categoryCopy.setAuditColumns(permissions);
		categoryCopy = auditCategoryDAO.save(categoryCopy);
		
		for (AuditQuestion question : category.getQuestions()) {
			AuditQuestion questionCopy = new AuditQuestion(question, categoryCopy);
			questionCopy.setAuditColumns(permissions);
			auditQuestionDAO.save(questionCopy);
		}
		
		int number = 1;
		for (AuditCategory subCategory : category.getSubCategories()) {
			// categoryCopy is a brand new category with no subcategories
			copyTree(subCategory, categoryCopy, number);
			number++;
		}
		
		return categoryCopy;
	}

	@Override
	protected String getRedirectURL() {
		if (category.getAuditType() == null)
			return "ManageCategory.action?id=" + category.getParent().getId();
		else
			return "ManageAuditType.action?id=" + category.getParentAuditType().getId();
	}

	@Override
	protected String getDeletedRedirectURL() {
		return getRedirectURL();
	}

	public AuditCategory getCategoryParent() {
		return categoryParent;
	}

	public void setCategoryParent(AuditCategory categoryParent) {
		this.categoryParent = categoryParent;
	}

	public int getTargetCategoryID() {
		return targetCategoryID;
	}

	public void setTargetCategoryID(int targetCategoryID) {
		this.targetCategoryID = targetCategoryID;
	}

	public int getNumberRequired(AuditCategory category) {
		if (category.getNumRequired() <= 0 && category.getParent().getId() != category.getId())
			return getNumberRequired(category.getParent());

		return category.getNumRequired();
	}
}
