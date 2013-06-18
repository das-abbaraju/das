package com.picsauditing.jpa.entities;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import javax.persistence.Column;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Country extends BaseTranslatable implements Comparable<Country>, Serializable, Autocompleteable, IsoCode {
	public static final String DEFAULT_COUNTRY_SUBDIVISION_LABEL = "ContractorAccount.countrySubdivision";
	public static final String COUNTRY_SUBDIVISION_LABEL_FORMAT = "Country.%s.SubdivisionLabel";

	public static final String FRANCE_ISO_CODE = "FR";
	public static final String GERMANY_ISO_CODE = "DE";
	public static final String UK_ISO_CODE = "GB";
	public static final String UAE_ISO_CODE = "AE";
	public static final String CANADA_ISO_CODE = "CA";
	public static final String US_ISO_CODE = "US";

	public static final List<String> COUNTRIES_WITH_SUBDIVISIONS = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(US_ISO_CODE, CANADA_ISO_CODE)));

    public static final Comparator<Country> NAME_COMPARATOR =  new Comparator<Country>() {
        Collator collator = Collator.getInstance();

        public int compare(Country o1, Country o2) {
            return collator.compare(o1.getName(), o2.getName());
        }
    };
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
	private static final Set<String> EUROZONE = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("AT",
			"BE", "CY", "DE", "EE", "ES", "FI", FRANCE_ISO_CODE, "GR", "IE", "IT", "LU", "MT", "NL", "PT", "SI", "SK")));

	// The 10 additional countries that are in the European Union, but not in
	// the EuroZone (i.e. that have not yet adopted the Euro, or never will)
	private static final Set<String> EUROPEAN_UNION_ALSO = Collections.unmodifiableSet(new HashSet<String>(Arrays
			.asList("BG", "CZ", "DK", UK_ISO_CODE, "GI", "HU", "LT", "LV", "PL", "RO", "SE")));

	protected String isoCode;
	protected TranslatableString name;
	protected String english;
	protected String phone;
	protected String salesPhone;
	protected String fax;
	protected Double corruptionPerceptionIndex;
    protected boolean proforma;

	protected Currency currency = Currency.USD;

	private List<CountrySubdivision> countrySubdivisions = new ArrayList<CountrySubdivision>();
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

	@Column(name = "phone", length = 30)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "salesPhone", length = 30)
	public String getSalesPhone() {
		return salesPhone;
	}

	public void setSalesPhone(String salesPhone) {
		this.salesPhone = salesPhone;
	}

	@Column(name = "fax", length = 30)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public static String convertToCode(String tempCountry) {
		if (Strings.isEmpty(tempCountry)) {
			return null;
		}

		tempCountry = tempCountry.trim();
		if (tempCountry.length() == 2) {
			return tempCountry;
		}
		if (tempCountry.equals("Canada")) {
			return CANADA_ISO_CODE;
		} else if (tempCountry.equals("United States")) {
			return US_ISO_CODE;
		}

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
	@ReportField(category = FieldCategory.Billing, type = FieldType.Currency, requiredPermissions = OpPerms.Billing, importance = FieldImportance.Average)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency curreny) {
		this.currency = curreny;
	}

    public boolean isProforma() {
        return proforma;
    }

    public void setProforma(boolean proforma) {
        this.proforma = proforma;
    }

    @OneToMany(mappedBy = "country")
	public List<InvoiceFeeCountry> getAmountOverrides() {
		return this.amountOverrides;
	}

	public void setAmountOverrides(List<InvoiceFeeCountry> amountOverrides) {
		this.amountOverrides = amountOverrides;
	}

	@OneToMany(mappedBy = "country")
	public List<CountrySubdivision> getCountrySubdivisions() {
		return countrySubdivisions;
	}

	public void setCountrySubdivisions(List<CountrySubdivision> countrySubdivisions) {
		this.countrySubdivisions = countrySubdivisions;
	}

	@Transient
	public boolean isHasCountrySubdivisions() {
		return COUNTRIES_WITH_SUBDIVISIONS.contains(getIsoCode()) && countrySubdivisions.size() > 0;
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
		return US_ISO_CODE.equals(isoCode);
	}

	@Transient
	public boolean isCanada() {
		return CANADA_ISO_CODE.equals(isoCode);
	}

	@Transient
	public boolean isUAE() {
		return UAE_ISO_CODE.equals(isoCode);
	}

	@Transient
	public boolean isUK() {
		return UK_ISO_CODE.equals(isoCode);
	}

	@Transient
	public boolean isFrance() {
		return FRANCE_ISO_CODE.equals(isoCode);
	}

	@Transient
	public boolean isEuroZone() {
		return EUROZONE.contains(isoCode);
	}

	@Transient
	public boolean isEuropeanUnion() {
		return (isEuroZone() || EUROPEAN_UNION_ALSO.contains(isoCode));
	}

	// TODO This is in the wrong class. It should be in a FeeService or similar.
	@Transient
	public BigDecimal getAmount(InvoiceFee invoiceFee) {
		for (InvoiceFeeCountry countryFeeAmountOverride : getAmountOverrides()) {
			if (countryFeeAmountOverride.getInvoiceFee().equals(invoiceFee)) {
				return countryFeeAmountOverride.getAmount();
			}
		}

		return invoiceFee.getAmount();
	}
}
