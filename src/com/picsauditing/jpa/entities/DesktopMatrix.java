package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "desktopmatrix")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class DesktopMatrix extends BaseTable implements java.io.Serializable {
	private AuditCategory category;
	private AuditQuestion question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false, updatable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@ManyToOne(fetch = FetchType.LAZY)
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DesktopMatrix other = (DesktopMatrix) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
