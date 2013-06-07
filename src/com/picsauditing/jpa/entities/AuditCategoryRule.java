package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category_rule")
public class AuditCategoryRule extends AuditRule {

	private AuditCategory auditCategory;
	protected Boolean rootCategory;

	@ManyToOne
	@JoinColumn(name = "catID")
	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory category) {
		this.auditCategory = category;
	}

	/**
	 * Does this rule apply to the given category?
	 * 
	 * @param operator
	 * @return
	 */
	@Transient
	public boolean isApplies(AuditCategory category) {
		if (this.auditCategory == null) {
			// We have a wildcard category, so let's figure out if it
			// matches on categories or subcategories or both
			if (this.rootCategory == null) {
				// Any category or subcategory matches
				return true;
			} else {
				if (this.rootCategory) {
					if (category.getParent() == null)
						// Only categories match
						return true;
				} else {
					if (category.getParent() != null)
						// Only subcategories match
						return true;
				}
			}
		} else if (this.auditCategory.equals(category)) {
			// We have a direct category match
			return true;
		}
		return false;
	}

	@Transient
	public String getAuditCategoryLabel() {
		if (auditCategory == null)
			return "*";
		return auditCategory.getName().toString();
	}

	public Boolean getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(Boolean rootCategory) {
		this.rootCategory = rootCategory;
	}

	@Transient
	public String getRootCategoryLabel() {
		if (rootCategory == null)
			return "*";
		return rootCategory ? "Yes" : "No";
	}

	@Override
	public void calculatePriority() {
		super.calculatePriority();
		if (auditCategory != null) {
			level++;
			priority += 120;
			rootCategory = (auditCategory.getParent() == null);
		} else if (rootCategory != null) {
			level++;
			priority += 101;
		}
	}

	public void merge(AuditRule source) {
		super.merge(source);
		if (auditCategory == null)
			auditCategory = ((AuditCategoryRule) source).getAuditCategory();
		if (rootCategory == null)
			rootCategory = ((AuditCategoryRule) source).getRootCategory();
	}

	public void update(AuditRule source) {
		super.update(source);
		auditCategory = ((AuditCategoryRule) source).getAuditCategory();
		rootCategory = ((AuditCategoryRule) source).getRootCategory();
	}

	@Override
	public String toString() {
		String out = super.toString();
		if (dependentAuditType != null) {
			if (!out.contains("when"))
				out += " when";
			else
				out += " and";
			out += " Dependent Audit Type is " + dependentAuditType
					+ (dependentAuditStatus == null ? "" : " is " + dependentAuditStatus);
		}
		if (rootCategory != null) {
			if (out.contains("when"))
				out += " and";
			else
				out += " when";
			out += " Root Category is [" + rootCategory.toString() + "]";
		}
		if (auditCategory != null) {
			if (out.contains("when"))
				out += " and";
			else
				out += " when";
			out += " Category is [" + auditCategory.toString() + "]";
		}
		return out;
	}

}
