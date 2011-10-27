package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "desktopmatrix")
public class DesktopMatrix extends BaseTable implements java.io.Serializable {
	private AuditCategory category;
	private AuditQuestion question;

	@ManyToOne
	@JoinColumn(name = "categoryID", nullable = false, updatable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	@Override
	public int hashCode() {
		final int PRIME = 74;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}
}
