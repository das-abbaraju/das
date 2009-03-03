package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfsubcategories")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class AuditSubCategory extends BaseTable implements java.io.Serializable, Comparable<AuditSubCategory> {

	private String subCategory;
	private AuditCategory category;
	private int number;
	private String helpText;

	private List<AuditQuestion> questions;

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@OneToMany(mappedBy = "subCategory")
	@OrderBy("number")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	public boolean hasValidQuestions() {
		for (AuditQuestion question : this.getQuestions())
			if (category.getValidDate().after(question.getEffectiveDate())
					&& category.getValidDate().before(question.getExpirationDate()))
				return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 32;
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
		try {
			final AuditSubCategory other = (AuditSubCategory) obj;
			if (id != other.getId())
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AuditSubCategory other) {
		if (other == null) {
			return 1;
		}

		int cmp = getCategory().compareTo(other.getCategory());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}

}
