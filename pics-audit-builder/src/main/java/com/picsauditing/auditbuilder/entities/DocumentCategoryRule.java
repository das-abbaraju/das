package com.picsauditing.auditbuilder.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditCategoryRule")
@Table(name = "audit_category_rule")
public class DocumentCategoryRule extends DocumentRule {

	private DocumentCategory documentCategory;
	protected Boolean rootCategory;

	@ManyToOne
	@JoinColumn(name = "catID")
	public DocumentCategory getDocumentCategory() {
		return documentCategory;
	}

	public void setDocumentCategory(DocumentCategory category) {
		this.documentCategory = category;
	}

	public Boolean getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(Boolean rootCategory) {
		this.rootCategory = rootCategory;
	}
}