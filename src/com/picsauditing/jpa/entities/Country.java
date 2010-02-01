package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@Entity
@Table(name = "ref_country")
public class Country implements Serializable {
	private static final long serialVersionUID = 6312208192653925848L;

	protected String isoCode;
	protected String english;
	protected String spanish;
	protected String french;

	protected User csr;

	public Country() {
	}

	public Country(String isoCode) {
		this.isoCode = isoCode;
	}

	public Country(String isoCode, String english) {
		this.isoCode = isoCode;
		this.english = english;
	}

	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	@Transient
	public String getName() {
		return english;
	}

	@Transient
	public String getName(Locale locale) {
		if (locale.getLanguage().equals("es"))
			return spanish;
		if (locale.getLanguage().equals("fr"))
			return french;
		return english;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getSpanish() {
		return spanish;
	}

	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}

	public String getFrench() {
		return french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	public static String convertToCode(String tempCountry) {
		if (Strings.isEmpty(tempCountry))
			return null;

		tempCountry = tempCountry.trim();
		if (tempCountry.length() == 2)
			return tempCountry;
		if (tempCountry.equals("Canada"))
			return "CA";

		return "US";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "csrID")
	public User getCsr() {
		return csr;
	}

	public void setCsr(User csr) {
		this.csr = csr;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Country && this.isoCode.equals(((Country) obj).getIsoCode());
	}

	@Override
	public String toString() {
		return english;
	}
}
