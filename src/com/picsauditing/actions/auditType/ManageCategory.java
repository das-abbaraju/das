package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ManageCategory extends ManageAuditType implements Preparable {

	protected AuditCategory categoryParent;
	private int targetCategoryID = 0;

	public ManageCategory(EmailTemplateDAO emailTemplateDAO, AuditTypeDAO auditTypeDao,
			AuditCategoryDAO auditCategoryDao, AuditQuestionDAO auditQuestionDao, AuditDecisionTableDAO ruleDAO,
			WorkFlowDAO wfDAO) {
		super(emailTemplateDAO, auditTypeDao, auditCategoryDao, auditQuestionDao, ruleDAO, wfDAO);
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
			} else {
				id = category.getAuditType().getId();
			}

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

			id = category.getAuditType().getId();

			auditCategoryDAO.remove(category.getId());
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

			AuditType targetAudit = category.getAuditType();
			if (targetID > 0) {
				// Moving to top level
				targetAudit = auditTypeDAO.find(targetID);
				category.setParent(null);
			}

			AuditCategory parent = category.getParent();
			if (targetCategoryID > 0)
				parent = auditCategoryDAO.find(targetCategoryID);

			category.setParent(parent);
			category.setAuditType(targetAudit);
			category.setAuditColumns(permissions);
			auditCategoryDAO.save(category);
			// update all children, only if we're moving to a new audit type
			if (targetID > 0) {
				for (AuditCategory cat : category.getChildren()) {
					cat.setAuditType(targetAudit);
					cat.setAuditColumns(permissions);
					auditCategoryDAO.save(cat);
				}
			}

			addActionMessage("Moved Category Successfully. <a href=\"ManageCategory.action?id=" + category.getId()
					+ "\">Go to this Category?</a>");
			return true;

		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	@Override
	protected String getRedirectURL() {
		if (category.getParent() == null)
			return "ManageAuditType.action?id=" + category.getAuditType().getId();
		else
			return "ManageCategory.action?id=" + category.getParent().getId();
	}

	@Override
	protected String getDeletedRedirectURL() {
		return getRedirectURL();
	}

	public AuditCategory getCategoryParent() {
		return categoryParent;
	}

	@Override
	public List<? extends AuditRule> getRelatedRules() {
		if (relatedRules == null) {
			relatedRules = ruleDAO.findByCategory(category);
		}

		return relatedRules;
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
}
