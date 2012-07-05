package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;

@Entity
@Table(name = "ref_state")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class State extends BaseTranslatable implements Comparable<State>, Serializable, Autocompleteable, IsoCode {
	private static final long serialVersionUID = -7010252482295453919L;

	protected int id;
	protected String isoCode;
	protected TranslatableString name;
	protected String english;
	protected String french;

	protected Country country;
	protected User csr;

	public State() {
	}

	public State(String isoCode, Country country) {
		this.isoCode = isoCode;
		this.country = country;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 10)
	@ReportField(filterType = FilterType.Integer)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getFrench() {
		return french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	@ManyToOne
	@JoinColumn(name = "countryCode")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "csrID")
	public User getCsr() {
		return csr;
	}

	public void setCsr(User csr) {
		this.csr = csr;
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
		if (locale.getLanguage().equals("fr"))
			return french;
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
			obj.put("french", french);
			obj.put("country", country == null ? null : country.getIsoCode());
			obj.put("CSR", csr == null ? null : csr.toJSON());
		}
		return obj;
	}

	@Transient
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + isoCode + "." + country.getIsoCode();
	}

	@Transient
	public String getI18nKey(String property) {
		return getI18nKey();
	}

	public int compareTo(State o) {
		return this.getI18nKey().compareTo(o.getI18nKey());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof State && this.isoCode.equals(((State) obj).getIsoCode());
	}
}