package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

@Entity
@Table(name = "ref_country_subdivision")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class CountrySubdivision extends BaseTranslatable implements Comparable<CountrySubdivision>, Serializable, Autocompleteable, IsoCode {
	private static final long serialVersionUID = -7010252482295453919L;

	protected String isoCode;
	protected TranslatableString name;
	protected String english;

	protected Country country;

	public CountrySubdivision() {
	}

	public CountrySubdivision(String isoCode) {
		this.isoCode = isoCode;
	}

	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	@ManyToOne
	@JoinColumn(name = "countryCode")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Transient
	public String getName() {
		return name.toString() + ", " + country.getName();
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	@Transient
	@Deprecated
	public String getName(Locale locale) {
		return english;
	}

	@Transient
	public String getSimpleName() {
		return name.toString();
	}

	@Override
	public String toString() {
		return isoCode;
	}

	@Transient
	public String getAutocompleteResult() {
		return isoCode;
	}

	@Transient
	public String getAutocompleteItem() {
		return isoCode;
	}

	@Transient
	public String getAutocompleteValue() {
		return getName();
	}

	@Transient
	public JSONObject toJSON() {
		return toJSON(false);
	}

	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("isoCode", isoCode);

		if (full) {
			obj.put("english", english);
			obj.put("country", country == null ? null : country.getIsoCode());
		}
		return obj;
	}

	@Transient
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + isoCode;
	}

	@Transient
	public String getI18nKey(String property) {
		return getI18nKey();
	}

	public int compareTo(CountrySubdivision o) {
		return this.getI18nKey().compareTo(o.getI18nKey());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CountrySubdivision && this.isoCode.equals(((CountrySubdivision) obj).getIsoCode());
	}
}
