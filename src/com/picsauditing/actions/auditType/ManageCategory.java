package com.picsauditing.actions.auditType;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTableRequiringLanguages;
import com.picsauditing.models.audits.TranslationKeysGenerator;

@SuppressWarnings("serial")
public class ManageCategory extends ManageAuditType implements Preparable {
	protected AuditCategory categoryParent;
	private int targetCategoryID = 0;

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

			if (category.getName() == null || category.getName().toString().length() == 0) {
				this.addActionError("Category name is required");
				return false;
			}

			if (category.getLanguages().isEmpty()) {
				if (category.getParent() != null && category.getParent().getLanguages().size() >= 1)
					category.setLanguages(category.getParent().getLanguages());
				else if (auditType.getLanguages().size() >= 1)
					category.setLanguages(auditType.getLanguages());
			}

			if (category.hasMissingChildRequiredLanguages()) {
				addActionError("Changes to required languages must always have at least one language left. "
						+ "Check your hierarchy to make sure that each category and question has at least one language.");
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
				if (categoryParent != null && ancestors.size() > 0) {
					// Ancestors should be loaded
					categoryParent = ancestors.get(ancestors.size() - 1);
					for (AuditCategory sibling : categoryParent.getSubCategories()) {
						if (sibling.getNumber() > maxID)
							maxID = sibling.getNumber();
					}
				} else if (category.getAuditType() != null) {
					AuditType audit = auditTypeDAO.find(category.getAuditType().getId());
					for (AuditCategory sibling : audit.getCategories()) {
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
	
	@Override
	public String findTranslations() throws IOException {
		Set<String> usedKeys = translationKeysGenerator.generateCategoryKeys(category);
		populateSessionVariablesForManageTranslationsRedirect(usedKeys);
		
		return setUrlForRedirect("ManageTranslations.action");
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

	@Override
	protected void fillSelectedLocales() {
		if (category != null && !category.getLanguages().isEmpty()) {
			for (String locale : category.getLanguages()) {
				selectedLocales.add(new Locale(locale));
			}
		}
	}

	@Override
	public List<Locale> getAvailableLocalesFrom(BaseTableRequiringLanguages entity) {
		if (entity == null)
			entity = category.getAuditType();
		return super.getAvailableLocalesFrom(entity);
	}
}