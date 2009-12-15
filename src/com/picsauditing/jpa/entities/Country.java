package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@Entity
public class Country {
	protected String isoCode;
	protected String english;
	protected String spanish;
	protected String french;

	public Country() {
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
		if (locale.equals(Locale.es))
			return spanish;
		if (locale.equals(Locale.fr))
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

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Country && this.isoCode.equals(((Country) obj).getIsoCode());
	}

	@Override
	public String toString() {
		return english;
	}
}
