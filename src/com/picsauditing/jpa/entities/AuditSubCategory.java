package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashSet;
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

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfsubcategories")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditSubCategory extends BaseTable implements java.io.Serializable, Comparable<AuditSubCategory> {

	private String subCategory;
	private AuditCategory category;
	private int number;
	private String helpText;
	private String countries;

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

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
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
			if (question.isValid())
				return true;
		return false;
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

	@Transient
	public String[] getCountriesArray() {
		if (Strings.isEmpty(countries))
			return new String[] {};
		return countries.replaceFirst("^[!]?\\|", "").replaceAll("\\|$", "").split("\\|");
	}

	@Transient
	public void setCountriesArray(String[] countryArray, boolean exclude) {
		Set<String> countrySet = new HashSet<String>();
		Collections.addAll(countrySet, countryArray);
		countrySet.remove("");
		String tmp = null;
		if (countrySet.size() > 0) {
			tmp = "";
			if (exclude)
				tmp += "!";
			tmp += "|";

			for (String country : countrySet) {
				tmp += country + "|";
			}
		}

		countries = tmp;
	}

}
