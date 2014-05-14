package com.picsauditing.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ref_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Country implements /*Comparable<Country>, */Serializable/*, Autocompleteable, IsoCode, Translatable*/ {
//	public static final String DEFAULT_COUNTRY_SUBDIVISION_LABEL = "ContractorAccount.countrySubdivision";
//	public static final String COUNTRY_SUBDIVISION_LABEL_FORMAT = "Country.%s.SubdivisionLabel";
//
//	public static final String FRANCE_ISO_CODE = "FR";
//	public static final String GERMANY_ISO_CODE = "DE";
//	public static final String UK_ISO_CODE = "GB";
//	public static final String UAE_ISO_CODE = "AE";
//	public static final String CANADA_ISO_CODE = "CA";
//	public static final String US_ISO_CODE = "US";
//	public static final String CHINA_ISO_CODE = "CN";
//    public static final String AUSTRALIA_ISO_CODE = "AU";
//    public static final String SOUTH_AFRICA_ISO_CODE = "ZA";
//    public static final String BRAZIL_ISO_CODE = "BR";
//
//    public static final List<String> COUNTRIES_WITH_SUBDIVISIONS = Collections.unmodifiableList(new ArrayList<>(Arrays
//			.asList(US_ISO_CODE, CANADA_ISO_CODE, AUSTRALIA_ISO_CODE)));
//
//	public static final Comparator<Country> NAME_COMPARATOR = new Comparator<Country>() {
//		Collator collator = Collator.getInstance();
//
//		public int compare(Country o1, Country o2) {
//			return collator.compare(o1.getName(), o2.getName());
//		}
//	};
//
//	private static final long serialVersionUID = 6312208192653925848L;
//
//	/*
//	 * TODO We hard-coded the following two lists as a stop-gap measure to get
//	 * PICS-6555 going. We considered adding an isEuropeanUnion column to
//	 * ref_country, but that begged the question -- Are there any other country
//	 * affiliations that we might want to know about in the future? -- So, we
//	 * decided that it would be short-sighted to add a European-Union-specific
//	 * column, when down the line we'll really want a 1:M table that shows
//	 * multiple affiliations. So, we're invoking the Rule of Three here, and
//	 * this is only strike one.
//	 */
//
//	// The 17 Countries in the EuroZone (that have adopted the Euro)
//	private static final Set<String> EUROZONE = Collections
//			.unmodifiableSet(new HashSet<>(Arrays.asList("AT", "BE", "CY", "DE", "EE", "ES", "FI",
//					FRANCE_ISO_CODE, "GR", "IE", "IT", "LU", "MT", "NL", "PT", "SI", "SK")));
//
//	// The 10 additional countries that are in the European Union, but not in
//	// the EuroZone (i.e. that have not yet adopted the Euro, or never will)
//	private static final Set<String> EUROPEAN_UNION_ALSO = Collections.unmodifiableSet(new HashSet<>(Arrays
//			.asList("BG", "CZ", "DK", UK_ISO_CODE, "GI", "HU", "LT", "LV", "PL", "RO", "SE")));
//
    protected String isoCode;
//    protected User createdBy;
//    protected User updatedBy;
//    protected Date creationDate;
//    protected Date updateDate;
//
//	protected String name;
//	protected String english;
//
//    protected Currency currency = Currency.USD;
//	protected Double corruptionPerceptionIndex;
//	protected boolean proforma;
//
//    protected String csrPhone;
//    protected String csrFax;
//    protected String csrEmail;
//    protected String csrAddress;
//    protected String csrAddress2;
//    protected String csrCity;
//    protected String csrCountry;
//    protected String csrCountrySubdivision;
//    protected String csrZip;
//
//    protected String isrPhone;
//    protected String isrFax;
//    protected String isrEmail;
//
//    @Deprecated
//    protected String isrAddress;
//    @Deprecated
//    protected String isrCity;
//    @Deprecated
//    protected CountrySubdivision isrCountrySubdivision;
//    @Deprecated
//    protected String isrZip;
//
//	protected BusinessUnit businessUnit;
//
//	private List<CountrySubdivision> countrySubdivisions = new ArrayList<CountrySubdivision>();
//	private List<InvoiceFeeCountry> amountOverrides = new ArrayList<InvoiceFeeCountry>();
//
//	public Country() {
//	}
//
//	public Country(String isoCode) {
//		this.isoCode = isoCode;
//	}
//
//	public Country(String isoCode, String english) {
//		this.isoCode = isoCode;
//		this.english = english;
//	}
//
	@Id
	@Column(nullable = false, length = 2)
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "createdBy", nullable = true)
//    public User getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(User createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "updatedBy", nullable = true)
//    public User getUpdatedBy() {
//        return updatedBy;
//    }
//
//    public void setUpdatedBy(User updatedBy) {
//        this.updatedBy = updatedBy;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    public Date getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Date creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
//    public Date getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(Date updateDate) {
//        this.updateDate = updateDate;
//    }
//
//    @Transient
//	public String getName() {
//		if (name != null) {
//			return name;
//		}
//
//		return new TranslatableString(getI18nKey()).toTranslatedString();
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//    @ReportField(importance = FieldImportance.Required)
//	public String getEnglish() {
//		return english;
//	}
//
//	public void setEnglish(String english) {
//		this.english = english;
//	}
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    @ReportField(requiredPermissions = OpPerms.Billing, importance = FieldImportance.Average)
//    public Currency getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(Currency curreny) {
//        this.currency = curreny;
//    }
//
//    @Column(name = "perceivedCorruption")
//    public Double getCorruptionPerceptionIndex() {
//        return this.corruptionPerceptionIndex;
//    }
//
//    public void setCorruptionPerceptionIndex(Double corruptionPerceptionIndex) {
//        this.corruptionPerceptionIndex = corruptionPerceptionIndex;
//    }
//
//    @ReportField(type = FieldType.Boolean, requiredPermissions = OpPerms.Billing, importance = FieldImportance.Average)
//    public boolean isProforma() {
//        return proforma;
//    }
//
//    public void setProforma(boolean proforma) {
//        this.proforma = proforma;
//    }
//
//    @Column(name = "csrPhone", length = 30)
//    public String getCsrPhone() {
//        return csrPhone;
//    }
//
//    public void setCsrPhone(String csrPhone) {
//        this.csrPhone = csrPhone;
//    }
//
//    @Column(name = "csrFax", length = 30)
//    public String getCsrFax() {
//        return csrFax;
//    }
//
//    public void setCsrFax(String csrFax) {
//        this.csrFax = csrFax;
//    }
//
//    public String getCsrEmail() {
//        return csrEmail;
//    }
//
//    public void setCsrEmail(String csrEmail) {
//        this.csrEmail = csrEmail;
//    }
//
//    public String getCsrAddress() {
//        return csrAddress;
//    }
//
//    public void setCsrAddress(String csrAddress) {
//        this.csrAddress = csrAddress;
//    }
//
//    public String getCsrAddress2() {
//        return csrAddress2;
//    }
//
//    public void setCsrAddress2(String csrAddress2) {
//        this.csrAddress2 = csrAddress2;
//    }
//
//    public String getCsrCity() {
//        return csrCity;
//    }
//
//    public void setCsrCity(String csrCity) {
//        this.csrCity = csrCity;
//    }
//
//    public String getCsrCountry() {
//        return csrCountry;
//    }
//
//    public void setCsrCountry(String csrCountry) {
//        this.csrCountry = csrCountry;
//    }
//
//    public String getCsrCountrySubdivision() {
//        return csrCountrySubdivision;
//    }
//
//    public void setCsrCountrySubdivision(String csrCountrySubdivision) {
//        this.csrCountrySubdivision = csrCountrySubdivision;
//    }
//
//    public String getCsrZip() {
//        return csrZip;
//    }
//
//    public void setCsrZip(String csrZip) {
//        this.csrZip = csrZip;
//    }
//
//    @Column(name = "isrPhone", length = 30)
//    public String getIsrPhone() {
//        return isrPhone;
//    }
//
//    public void setIsrPhone(String isrPhone) {
//        this.isrPhone = isrPhone;
//    }
//
//    @Column(name = "isrFax", length = 30)
//    public String getIsrFax() {
//        return csrFax;
//    }
//
//    public void setIsrFax(String isrFax) {
//        this.isrFax = isrFax;
//    }
//
//    public String getIsrEmail() {
//        return isrEmail;
//    }
//
//    public void setIsrEmail(String isrEmail) {
//        this.isrEmail = isrEmail;
//    }
//
//    @Deprecated
//    public String getIsrAddress() {
//        return isrAddress;
//    }
//
//    @Deprecated
//    public void setIsrAddress(String isrAddress) {
//        this.isrAddress = isrAddress;
//    }
//
//    @Deprecated
//    public String getIsrCity() {
//        return isrCity;
//    }
//
//    @Deprecated
//    public void setIsrCity(String isrCity) {
//        this.isrCity = isrCity;
//    }
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "isrCountrySubdivision")
//    @Deprecated
//    public CountrySubdivision getIsrCountrySubdivision() {
//        return isrCountrySubdivision;
//    }
//
//    @Deprecated
//    public void setIsrCountrySubdivision(CountrySubdivision isrCountrySubdivision) {
//        this.isrCountrySubdivision = isrCountrySubdivision;
//    }
//
//    @Deprecated
//    public String getIsrZip() {
//        return isrZip;
//    }
//
//    @Deprecated
//    public void setIsrZip(String isrZip) {
//        this.isrZip = isrZip;
//    }
//
//    public static String convertToCode(String tempCountry) {
//        if (Strings.isEmpty(tempCountry)) {
//            return null;
//        }
//
//        tempCountry = tempCountry.trim();
//        if (tempCountry.length() == 2) {
//            return tempCountry;
//        }
//        if (tempCountry.equals("Canada")) {
//            return CANADA_ISO_CODE;
//        } else if (tempCountry.equals("United States")) {
//            return US_ISO_CODE;
//        }
//
//        return "???";
//    }
//
//	@ManyToOne
//	@JoinColumn(name="businessUnitID")
//	public BusinessUnit getBusinessUnit() {
//		return businessUnit;
//	}
//
//	public void setBusinessUnit(BusinessUnit businessUnit) {
//		this.businessUnit = businessUnit;
//	}
//
//    @OneToMany(mappedBy = "country")
//	public List<InvoiceFeeCountry> getAmountOverrides() {
//		return this.amountOverrides;
//	}
//
//	public void setAmountOverrides(List<InvoiceFeeCountry> amountOverrides) {
//		this.amountOverrides = amountOverrides;
//	}
//
//	@OneToMany(mappedBy = "country")
//	public List<CountrySubdivision> getCountrySubdivisions() {
//		return countrySubdivisions;
//	}
//
//	public void setCountrySubdivisions(List<CountrySubdivision> countrySubdivisions) {
//		this.countrySubdivisions = countrySubdivisions;
//	}
//
//	@Transient
//	public boolean isHasCountrySubdivisions() {
//		return COUNTRIES_WITH_SUBDIVISIONS.contains(getIsoCode()) && countrySubdivisions.size() > 0;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		return obj instanceof Country && this.isoCode.equals(((Country) obj).getIsoCode());
//	}
//
//	@Override
//	public String toString() {
//		return Strings.isEmpty(english) ? isoCode : english;
//	}
//
//	@Transient
//	public String getAutocompleteResult() {
//		return isoCode + "_C";
//	}
//
//	@Transient
//	public String getAutocompleteItem() {
//		return isoCode;
//	}
//
//	@Transient
//	public String getAutocompleteValue() {
//		return getName();
//	}
//
//	@Transient
//	public JSONObject toJSON() {
//		return toJSON(false);
//	}
//
//	@Transient
//	@SuppressWarnings("unchecked")
//	public JSONObject toJSON(boolean full) {
//		JSONObject obj = new JSONObject();
//		obj.put("isoCode", isoCode);
//
//		if (full) {
//			obj.put("english", english);
//		}
//		return obj;
//	}
//
//	@Transient
//	public String getI18nKey() {
//		return getClass().getSimpleName() + "." + isoCode;
//	}
//
//	@Transient
//	public String getI18nKey(String property) {
//		return getI18nKey();
//	}
//
//	@Override
//	public int compareTo(Country o) {
//		return this.getI18nKey().compareTo(o.getI18nKey());
//	}
//
//	@Transient
//	public boolean isUS() {
//		return US_ISO_CODE.equals(isoCode);
//	}
//
//	@Transient
//	public boolean isCanada() {
//		return CANADA_ISO_CODE.equals(isoCode);
//	}
//
//	@Transient
//	public boolean isUAE() {
//		return UAE_ISO_CODE.equals(isoCode);
//	}
//
//	@Transient
//	public boolean isUK() {
//		return UK_ISO_CODE.equals(isoCode);
//	}
//
//    @Transient
//    public boolean isZA() {
//        return SOUTH_AFRICA_ISO_CODE.equals(isoCode);
//    }
//
//    @Transient
//	public boolean isFrance() {
//		return FRANCE_ISO_CODE.equals(isoCode);
//	}
//
//    @Transient
//    public boolean isAustralia() {
//        return AUSTRALIA_ISO_CODE.equals(isoCode);
//    }
//
//    @Transient
//    public boolean isBrazil() {
//        return BRAZIL_ISO_CODE.equals(isoCode);
//    }
//
//
//	@Transient
//	public boolean isEuroZone() {
//		return EUROZONE.contains(isoCode);
//	}
//
//	@Transient
//	public boolean isEuropeanUnion() {
//		return (isEuroZone() || EUROPEAN_UNION_ALSO.contains(isoCode));
//	}
//
//    public void setAuditColumns() {
//        updateDate = new Date();
//
//        if (createdBy == null) {
//            createdBy = updatedBy;
//        }
//        if (creationDate == null) {
//            creationDate = updateDate;
//        }
//    }
//
//    public void setAuditColumns(User user) {
//        if (user != null) {
//            updatedBy = user;
//        }
//
//        setAuditColumns();
//    }
//
//    public void setAuditColumns(Permissions permissions) {
//        if (permissions == null) {
//            setAuditColumns();
//            return;
//        }
//        int userID = permissions.getUserId();
//        if (permissions.getAdminID() > 0) {
//            userID = permissions.getAdminID();
//        }
//        setAuditColumns(new User(userID));
//    }
//
//    // TODO This is in the wrong class. It should be in a FeeService or similar. See FeeService.getRegionalAmountOverrides()
//    @Transient
//    public BigDecimal getAmount(InvoiceFee invoiceFee) {
//        for (InvoiceFeeCountry countryFeeAmountOverride : getAmountOverrides()) {
//            if (countryFeeAmountOverride.getInvoiceFee().equals(invoiceFee)) {
//                return countryFeeAmountOverride.getAmount();
//            }
//        }
//
//        return invoiceFee.getAmount();
//    }
}
