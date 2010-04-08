package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfcategories")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditCategory extends BaseTable implements java.io.Serializable, Comparable<AuditCategory> {

	public static final int WORK_HISTORY = 6;
	public static final int OSHA_AUDIT = 151;
	public static final int GENERAL_INFORMATION = 155;
	public static final int EMR = 152;
	public static final int MSHA = 157;
	public static final int CANADIAN_STATISTICS = 158;
	public static final int LOSS_RUN = 159;
	public static final int TRAINING = 25;
	public static final int SAFETY_POLICIES = 7;
	public static final int SERVICES_PERFORMED = 28;
	public static final int WCB = 210;
	public static final int CITATIONS = 278;

	private AuditType auditType;
	private String category;
	private int number;
	private int numRequired;
	private int numQuestions;
	private AuditQuestion applyOnQuestion = null;
	private String applyOnAnswer;

	List<AuditSubCategory> subCategories;

	/**
	 * This is a transient field that allows us to figure out which categories,
	 * subcategories and questions should be displayed
	 */
	private Date validDate = null;
	private Set<String> countries = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditTypeID", nullable = false)
	public AuditType getAuditType() {
		return this.auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Column(name = "category", nullable = false)
	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(nullable = false)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Column(nullable = false)
	public int getNumRequired() {
		return this.numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	@Column(nullable = false)
	public int getNumQuestions() {
		return this.numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	@ManyToOne
	@JoinColumn(name = "applyOnQID")
	public AuditQuestion getApplyOnQuestion() {
		return applyOnQuestion;
	}

	public void setApplyOnQuestion(AuditQuestion applyOnQuestion) {
		this.applyOnQuestion = applyOnQuestion;
	}

	public String getApplyOnAnswer() {
		return applyOnAnswer;
	}

	public void setApplyOnAnswer(String applyOnAnswer) {
		this.applyOnAnswer = applyOnAnswer;
	}

	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<AuditSubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<AuditSubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@Transient
	public List<AuditSubCategory> getValidSubCategories() {
		List<AuditSubCategory> list = new ArrayList<AuditSubCategory>();
		for (AuditSubCategory subCategory : getSubCategories())
			if (Strings.isInCountries(subCategory.getCountries(), countries) && subCategory.hasValidQuestions())
				list.add(subCategory);
		return list;
	}

	@Transient
	public boolean hasValidSubcategories() {
		for (AuditSubCategory subCategory : subCategories) {
			if (subCategory.hasValidQuestions())
				return true;
		}
		return false;
	}

	@Transient
	public Date getValidDate() {
		if (validDate == null)
			return new Date();
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	@Transient
	public Set<String> getCountries() {
		return countries;
	}

	public void setCountries(Set<String> countries) {
		this.countries = countries;
	}

	@Override
	public int compareTo(AuditCategory other) {
		if (other == null) {
			return 1;
		}

		int cmp = getAuditType().compareTo(other.getAuditType());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}

}
