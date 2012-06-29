package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.picsauditing.util.Strings;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Country extends BaseTranslatable implements Comparable<Country>, Serializable, Autocompleteable, IsoCode {
	private static final long serialVersionUID = 6312208192653925848L;

	protected String isoCode;
	protected TranslatableString name;
	protected String english;
	protected String spanish;
	protected String french;
	protected Double corruptionPerceptionIndex;
	protected Currency currency = Currency.USD;
	protected User csr;

	private List<InvoiceFeeCountry> amountOverrides = new ArrayList<InvoiceFeeCountry>();

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
		return name.toString();
	}

	public void setName(TranslatableString name) {
		this.name = name;
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
		else if (tempCountry.equals("United States"))
			return "US";

		return "???";
	}

	@Column(name = "perceivedCorruption")
	public Double getCorruptionPerceptionIndex() {
		return this.corruptionPerceptionIndex;
	}

	public void setCorruptionPerceptionIndex(Double corruptionPerceptionIndex) {
		this.corruptionPerceptionIndex = corruptionPerceptionIndex;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency curreny) {
		this.currency = curreny;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "csrID")
	public User getCsr() {
		return csr;
	}

	public void setCsr(User csr) {
		this.csr = csr;
	}

	@OneToMany(mappedBy = "country")
	public List<InvoiceFeeCountry> getAmountOverrides() {
		return this.amountOverrides;
	}

	public void setAmountOverrides(List<InvoiceFeeCountry> amountOverrides) {
		this.amountOverrides = amountOverrides;
	}

	@Transient
	public boolean isHasStates() {
		return isUS() || isCanada() || isUK();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Country && this.isoCode.equals(((Country) obj).getIsoCode());
	}

	@Override
	public String toString() {
		return Strings.isEmpty(english) ? isoCode : english;
	}

	@Transient
	public String getAutocompleteResult() {
		return isoCode + "_C";
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
			obj.put("spanish", spanish);
			obj.put("CSR", csr == null ? null : csr.toJSON());
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

	@Override
	public int compareTo(Country o) {
		return this.getI18nKey().compareTo(o.getI18nKey());
	}

	@Transient
	public boolean isUS() {
		return "US".equals(isoCode);
	}

	@Transient
	public boolean isCanada() {
		return "CA".equals(isoCode);
	}

	@Transient
	public boolean isUAE() {
		return "AE".equals(isoCode);
	}

	@Transient
	public boolean isUK() {
		return "GB".equals(isoCode);
	}

	@Transient
	public boolean isFrance() {
		return "FR".equals(isoCode);
	}

	@Transient
	public BigDecimal getAmount(InvoiceFee invoiceFee) {
		for (InvoiceFeeCountry countryFeeAmountOverride : getAmountOverrides()) {
			if (countryFeeAmountOverride.getInvoiceFee().equals(invoiceFee))
				return countryFeeAmountOverride.getAmount();
		}

		return invoiceFee.getAmount();
	}
}