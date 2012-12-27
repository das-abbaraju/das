package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.Strings;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Country extends BaseTranslatable implements Comparable<Country>, Serializable, Autocompleteable, IsoCode {
	private static final long serialVersionUID = 6312208192653925848L;

	/*
	 * TODO We hard-coded the following two lists as a stop-gap measure to get
	 * PICS-6555 going. We considered adding an isEuropeanUnion column to
	 * ref_country, but that begged the question -- Are there any other country
	 * affiliations that we might want to know about in the future? -- So, we
	 * decided that it would be short-sighted to add a European-Union-specific
	 * column, when down the line we'll really want a 1:M table that shows
	 * multiple affiliations. So, we're invoking the Rule of Three here, and
	 * this is only strike one.
	 */

	// The 17 Countries in the EuroZone (that have adopted the Euro)
	private static final String[] EUROZONE = { "AT", "BE", "CY", "DE", "EE", "ES", "FI", "FR", "GR", "IE", "IT", "LU",
			"MT", "NL", "PT", "SI", "SK" };
	// The 10 additional countries that are in the European Union, but not in
	// the EuroZone (i.e. that have not yet adopted the Euro, or never will)
	private static final String[] EUROPEAN_UNION_ALSO = { "BG", "CZ", "DK", "GB", "GI", "HU", "IT", "LT", "LV", "PL",
			"RO", "SE" };

	protected String isoCode;
	protected TranslatableString name;
	protected String english;
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

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
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
	@ReportField(category = FieldCategory.Billing, type = FieldType.Currency, requiredPermissions = OpPerms.Billing)
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
	public boolean isHasCountrySubdivisions() {
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
	public boolean isEuroZone() {
		return (Arrays.binarySearch(EUROZONE, isoCode) >= 0);

	}

	@Transient
	public boolean isEuropeanUnion() {
		return (isEuroZone() || Arrays.binarySearch(EUROPEAN_UNION_ALSO, isoCode) >= 0);
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