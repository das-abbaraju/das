package com.picsauditing.access.permissions.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_info")
// Cache is only on the operator account now, if this works.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAccount extends Account /*implements JSONable*/ {
//
//	private String taxId;
//	private String logoFile;
//	private String brochureFile;
//	private String mustPay = "Yes";
//	private OperatorAccount requestedBy;
//	private String billingAddress;
//	private String billingCity;
//	private CountrySubdivision billingCountrySubdivision;
//	private Country billingCountry;
//	private String billingZip;
//	private Date membershipDate;
//	private int payingFacilities;
//	private User auditor;
//	private User recommendedCsr;
//    private boolean tradeSafetySensitive;
//    private LowMedHigh tradeSafetyRisk = LowMedHigh.None;
//    private boolean safetySensitive;
//	private LowMedHigh safetyRisk = LowMedHigh.None;
//	private Date safetyRiskVerified;
//	private LowMedHigh productRisk = LowMedHigh.None;
//	private Date productRiskVerified;
//	private LowMedHigh transportationRisk = LowMedHigh.None;
//	private Date transportationRiskVerified;
//	private Date viewedFacilities;
//	private Float emrAverage;
//	private Float trirAverage;
//	private Float lwcrAverage;
//	private PaymentMethod paymentMethod = PaymentMethod.CreditCard;
//	private boolean ccOnFile = false;
//	private Date ccExpiration;
//	private boolean soleProprietor;
//	private Boolean competitorMembership;
//	private boolean showInDirectory = true;
//	private AccountLevel accountLevel = AccountLevel.Full;
//    // TODO Tech Debt: PICS-13969
//	private String vatId;
//
//	private Date paymentExpires;
//	private boolean renew = true;
//	private Date lastUpgradeDate;
//	private BigDecimal balance;
//	private Map<FeeClass, ContractorFee> fees = new TreeMap<FeeClass, ContractorFee>();
//	private Date agreementDate;
//	private User agreedBy;
//	private List<Invoice> invoices = new ArrayList<Invoice>();
//	private List<Payment> payments = new ArrayList<Payment>();
//	private List<Refund> refunds = new ArrayList<Refund>();
//    private List<InvoiceCreditMemo> creditMemos = new ArrayList<>();
//	private Set<FlagCriteriaContractor> flagCriteria = new HashSet<FlagCriteriaContractor>();
//	private Set<FlagDataOverride> flagDataOverrides = new HashSet<FlagDataOverride>();
//
//	private int needsRecalculation;
//	private Date lastRecalculation;
//	private String tradesSelf;
//	private String tradesSub;
//	private int score;
//	private Date tradesUpdated;
//	private LcCorPhase lcCorPhase;
//	private Date lcCorNotification;
//	private boolean dontReassign;
//	private boolean autoAddClientSite = true;
//
//	private List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
//	protected List<OshaAudit> oshaAudits = new ArrayList<OshaAudit>();
	private List<ContractorOperator> operators = new ArrayList<>();
//	private List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();
//	private List<Certificate> certificates = new ArrayList<Certificate>();
//	private List<JobContractor> jobSites = new ArrayList<JobContractor>();
//	private Set<ContractorTrade> trades = new TreeSet<ContractorTrade>();
//	private List<AssessmentResultStage> assessmentResultStages = new ArrayList<AssessmentResultStage>();
//	private List<ContractorOperatorNumber> contractorOperatorNumbers = new ArrayList<ContractorOperatorNumber>();
//	private List<InsuranceCriteriaContractorOperator> insuranceCriteriaContractorOperators = new ArrayList<>();
//	private Date lastContactedByAutomatedEmailDate;
//	private User lastContactedByInsideSales;
//	// Transient helper methods
//	private OshaOrganizer oshaOrganizer = null;
//	// Registration Requests
//	private Date lastContactedByInsideSalesDate;
//	private Date followUpDate;
//	private int contactCountByEmail;
//	private int contactCountByPhone;
//	private Date registrationRequestExpiresOn;
//	private LowMedHigh insideSalesPriority = LowMedHigh.None;
//	private String registrationHash;
//    private String salesRepSalesForceID;
//    private boolean hasEmployeeGuard;
//
//	// Agreement Changed on Release date 6/3/2010
//	private static final Date USER_AGREEMENT_CHANGED = DateBean.parseDate("06/03/2010");
//	public static final int MAX_RECALC = 127;
//
//	// This is only for testing, do not autowire these
//	private InputValidator inputValidator;
//	private VATValidator vatValidator;
//	private CountryDAO countryDAO;
//    private YearList yearList;
//
//	private static Logger logger = LoggerFactory.getLogger(ContractorAccount.class);
//
//	public ContractorAccount() {
//		this.type = "Contractor";
//	}
//
//	public ContractorAccount(int id) {
//		this.id = id;
//	}
//
//	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
//	@Where(clause = "expiresDate > NOW() OR expiresDate IS NULL")
//	public List<ContractorAudit> getAudits() {
//		return this.audits;
//	}
//
//	public void setAudits(List<ContractorAudit> audits) {
//		this.audits = audits;
//	}
//
	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

//	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
//	public List<InsuranceCriteriaContractorOperator> getInsuranceCriteriaContractorOperators() {
//		return insuranceCriteriaContractorOperators;
//	}
//
//	public void setInsuranceCriteriaContractorOperators(List<InsuranceCriteriaContractorOperator> insuranceCriteriaContractorOperators) {
//		this.insuranceCriteriaContractorOperators = insuranceCriteriaContractorOperators;
//	}
//
//	/**
//	 * Only includes the Active/Pending/Demo operator accounts, not corporate
//	 * accounts or Deleted/Deactivated Operators
//	 *
//	 * @return
//	 */
//	@Transient
//	public List<OperatorAccount> getOperatorAccounts() {
//		List<OperatorAccount> list = new ArrayList<OperatorAccount>();
//		for (ContractorOperator co : operators) {
//			if (co.getOperatorAccount().isOperator() && !co.getOperatorAccount().getStatus().isDeactivated()
//					&& !co.getOperatorAccount().getStatus().isDeleted()) {
//				list.add(co.getOperatorAccount());
//			}
//		}
//		Collections.sort(list);
//		return list;
//	}
//
//	@Transient
//	public List<ContractorOperator> getNonCorporateOperators() {
//		return new Grepper<ContractorOperator>() {
//
//			@Override
//			public boolean check(ContractorOperator t) {
//				return !t.getOperatorAccount().isCorporate() && !t.getOperatorAccount().getStatus().isDeactivated()
//						&& !t.getOperatorAccount().getStatus().isDeleted();
//			}
//		}.grep(this.operators);
//	}
//
//	@OneToMany(mappedBy = "contractor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
//	public List<ContractorTag> getOperatorTags() {
//		return operatorTags;
//	}
//
//	public void setOperatorTags(List<ContractorTag> operatorTags) {
//		this.operatorTags = operatorTags;
//	}
//
//	@OneToMany(mappedBy = "contractor", cascade = {CascadeType.REMOVE})
//	public List<Certificate> getCertificates() {
//		return certificates;
//	}
//
//	public void setCertificates(List<Certificate> certificates) {
//		this.certificates = certificates;
//	}
//
//	@OneToMany(mappedBy = "contractor")
//	public List<JobContractor> getJobSites() {
//		return jobSites;
//	}
//
//	public void setJobSites(List<JobContractor> jobSites) {
//		this.jobSites = jobSites;
//	}
//
//	@Column(name = "taxID", length = 100)
//    @Deprecated
//	public String getTaxId() {
//		return this.taxId;
//	}
//
//	public void setTaxId(String taxId) {
//		this.taxId = taxId;
//	}
//
//	@Column(name = "logo_file", length = 50)
//	public String getLogoFile() {
//		return this.logoFile;
//	}
//
//	public void setLogoFile(String logoFile) {
//		this.logoFile = logoFile;
//	}
//
//	@Column(name = "brochure_file", length = 3)
//	public String getBrochureFile() {
//		return this.brochureFile;
//	}
//
//	public void setBrochureFile(String brochureFile) {
//		this.brochureFile = brochureFile;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "requestedByID")
//	public OperatorAccount getRequestedBy() {
//		return requestedBy;
//	}
//
//	public void setRequestedBy(OperatorAccount requestedBy) {
//		this.requestedBy = requestedBy;
//	}
//
//	@Column(length = 50)
//    @ReportField(requiredPermissions = OpPerms.Billing)
//	public String getBillingAddress() {
//		return billingAddress;
//	}
//
//	public void setBillingAddress(String billingAddress) {
//		this.billingAddress = billingAddress;
//	}
//
//	@Column(length = 35)
//    @ReportField(requiredPermissions = OpPerms.Billing)
//	public String getBillingCity() {
//		return billingCity;
//	}
//
//	public void setBillingCity(String billingCity) {
//		this.billingCity = billingCity;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "billingCountrySubdivision")
//    @ReportField(i18nKeyPrefix = "CountrySubdivision", type = FieldType.CountrySubdivision, requiredPermissions = OpPerms.Billing)
//	public CountrySubdivision getBillingCountrySubdivision() {
//		return billingCountrySubdivision;
//	}
//
//	public void setBillingCountrySubdivision(CountrySubdivision billingCountrySubdivision) {
//		this.billingCountrySubdivision = billingCountrySubdivision;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "billingCountry")
//    @ReportField(i18nKeyPrefix = "Country", type = FieldType.Country, width = 150, requiredPermissions = OpPerms.Billing)
//	public Country getBillingCountry() {
//		return billingCountry;
//	}
//
//	public void setBillingCountry(Country billingCountry) {
//		this.billingCountry = billingCountry;
//	}
//
//	@Column(length = 10)
//    @ReportField(requiredPermissions = OpPerms.Billing)
//	public String getBillingZip() {
//		return billingZip;
//	}
//
//	public void setBillingZip(String billingZip) {
//		this.billingZip = billingZip;
//	}
//
//	public void resetRisksBasedOnTypes() {
//		if (!isOnsiteServices() && !isOffsiteServices()) {
//			setSafetyRisk(LowMedHigh.None);
//		}
//		if (!isMaterialSupplier()) {
//			setProductRisk(LowMedHigh.None);
//		}
//		if (!isTransportationServices()) {
//			setTransportationRisk(LowMedHigh.None);
//		}
//	}
//
//    @Column(name = "tradeSafetySensitive", nullable = false)
//    @ReportField(type = FieldType.Boolean, importance = FieldImportance.Average, requiredPermissions = OpPerms.RiskRank)
//    public boolean isTradeSafetySensitive() {
//        return tradeSafetySensitive;
//    }
//
//    public void setTradeSafetySensitive(boolean tradeSafetySensitive) {
//        this.tradeSafetySensitive = tradeSafetySensitive;
//    }
//
//    @Column(name = "tradeSafetyRisk", nullable = false)
//    @ReportField(type = FieldType.LowMedHigh, importance = FieldImportance.Average, requiredPermissions = OpPerms.AllContractors)
//    public LowMedHigh getTradeSafetyRisk() {
//        return tradeSafetyRisk;
//    }
//
//    public void setTradeSafetyRisk(LowMedHigh tradeSafetyRisk) {
//        this.tradeSafetyRisk = tradeSafetyRisk;
//    }
//
//    @Column(name = "safetySensitive", nullable = false)
//    @ReportField(type = FieldType.Boolean, importance = FieldImportance.Average)
//    public boolean isSafetySensitive() {
//        return safetySensitive;
//    }
//
//    public void setSafetySensitive(boolean safetySensitive) {
//        this.safetySensitive = safetySensitive;
//    }
//
//    @Column(name = "safetyRisk", nullable = false)
//	@ReportField(type = FieldType.LowMedHigh, importance = FieldImportance.Average, requiredPermissions = OpPerms.AllContractors)
//	public LowMedHigh getSafetyRisk() {
//		return safetyRisk;
//	}
//
//	public void setSafetyRisk(LowMedHigh safetyRisk) {
//		this.safetyRisk = safetyRisk;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getSafetyRiskVerified() {
//		return safetyRiskVerified;
//	}
//
//	public void setSafetyRiskVerified(Date safetyRiskVerified) {
//		this.safetyRiskVerified = safetyRiskVerified;
//	}
//
//	@Column(name = "productRisk", nullable = false)
//	@ReportField(type = FieldType.LowMedHigh, importance = FieldImportance.Average)
//	public LowMedHigh getProductRisk() {
//		return productRisk;
//	}
//
//	public void setProductRisk(LowMedHigh productRisk) {
//		this.productRisk = productRisk;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@ReportField(type = FieldType.Date)
//	public Date getProductRiskVerified() {
//		return productRiskVerified;
//	}
//
//	public void setProductRiskVerified(Date productRiskVerified) {
//		this.productRiskVerified = productRiskVerified;
//	}
//
//	@Column(name = "transportationRisk", nullable = false)
//	@ReportField(type = FieldType.LowMedHigh, importance = FieldImportance.Average)
//	public LowMedHigh getTransportationRisk() {
//		return transportationRisk;
//	}
//
//	public void setTransportationRisk(LowMedHigh transportationRisk) {
//		this.transportationRisk = transportationRisk;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@ReportField(type = FieldType.Date)
//	public Date getTransportationRiskVerified() {
//		return transportationRiskVerified;
//	}
//
//	public void setTransportationRiskVerified(Date transportationRiskVerified) {
//		this.transportationRiskVerified = transportationRiskVerified;
//	}
//
//	@Column(name = "emrAverage")
//	@Deprecated
//	public Float getEmrAverage() {
//		return emrAverage;
//	}
//
//	@Deprecated
//	public void setEmrAverage(Float emrAverage) {
//		this.emrAverage = emrAverage;
//	}
//
//	@Column(name = "trirAverage")
//	@Deprecated
//	public Float getTrirAverage() {
//		return trirAverage;
//	}
//
//	@Deprecated
//	public void setTrirAverage(Float trirAverage) {
//		this.trirAverage = trirAverage;
//	}
//
//	@Column(name = "lwcrAverage")
//	@Deprecated
//	public Float getLwcrAverage() {
//		return lwcrAverage;
//	}
//
//	@Deprecated
//	public void setLwcrAverage(Float lwcrAverage) {
//		this.lwcrAverage = lwcrAverage;
//	}
//
//	@Column(name = "dontReassign")
//	@ReportField(type = FieldType.Boolean)
//	public boolean isDontReassign() {
//		return dontReassign;
//	}
//
//	public void setDontReassign(boolean dontReassign) {
//		this.dontReassign = dontReassign;
//	}
//
//	// //// BILLING/ACCOUNT - related columns //////
//
//	/**
//	 * Determines if this contractor must pay or not. It allows for PICS to
//	 * grant "free" lifetime accounts to certain contractors. Yes or No
//	 */
//	@Column(name = "mustPay", nullable = false, length = 3)
//	@ReportField(type = FieldType.Boolean, requiredPermissions = OpPerms.Billing, sql = "CASE "
//			+ ReportOnClause.ToAlias + ".mustPay WHEN 'Yes' THEN 1 ELSE 0 END", importance = FieldImportance.Average)
//	public String getMustPay() {
//		return this.mustPay;
//	}
//
//	public void setMustPay(String mustPay) {
//		this.mustPay = mustPay;
//	}
//
//	@Transient
//	public boolean isMustPayB() {
//		return "Yes".equals(mustPay);
//	}
//
//	@Column(name = "payingFacilities", nullable = false)
//	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average, requiredPermissions = OpPerms.AllOperators)
//	public int getPayingFacilities() {
//		return this.payingFacilities;
//	}
//
//	public void setPayingFacilities(int payingFacilities) {
//		this.payingFacilities = payingFacilities;
//	}
//
//	@Transient
//	public boolean isPaymentMethodStatusValid() {
//		if (paymentMethod == null) {
//			return false;
//		}
//		if (paymentMethod.isCreditCard()) {
//			return isCcValid();
//		}
//		// If Check
//		return true;
//	}
//
//	/**
//	 * Set to true if we have a credit card on file
//	 *
//	 * @return
//	 */
//	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Average, requiredPermissions = OpPerms.Billing)
//	public boolean isCcOnFile() {
//		return ccOnFile;
//	}
//
//	public void setCcOnFile(boolean ccOnFile) {
//		this.ccOnFile = ccOnFile;
//	}
//
//	@ReportField(type = FieldType.Date, requiredPermissions = OpPerms.Billing)
//	public Date getCcExpiration() {
//		return ccExpiration;
//	}
//
//	public void setCcExpiration(Date ccExpiration) {
//		this.ccExpiration = ccExpiration;
//	}
//
//	@Transient
//	public boolean isCcValid() {
//		if (!ccOnFile) {
//			return false;
//		}
//
//		return isCcExpired();
//	}
//
//	@Transient
//	public boolean isCcExpired() {
//		if (ccExpiration == null) {
//			// Because this is new, some haven't been loaded yet. Assume it's
//			// fine for now
//			// TODO remove this section once we load all the dates
//			return true;
//		}
//
//		Calendar expires = Calendar.getInstance();
//		expires.setTime(ccExpiration);
//		expires.set(Calendar.DAY_OF_MONTH, 1);
//		expires.set(Calendar.HOUR_OF_DAY, 23);
//		expires.set(Calendar.MINUTE, 59);
//		expires.add(Calendar.MONTH, 1);
//		expires.add(Calendar.DAY_OF_MONTH, -1);
//
//		return expires.getTime().after(new Date());
//	}
//
//	/**
//	 * The Payment methods are Credit Card and Check
//	 *
//	 * @return
//	 */
//	@Enumerated(EnumType.STRING)
//	@ReportField(type = FieldType.String, requiredPermissions = OpPerms.Billing)
//	public PaymentMethod getPaymentMethod() {
//		return paymentMethod;
//	}
//
//	public void setPaymentMethod(PaymentMethod paymentMethod) {
//		this.paymentMethod = paymentMethod;
//	}
//
//    /**
//     * The date the contractor was invoiced for their most recent
//     * activation/reactivation fee
//     *
//     * @return
//     */
//	@Temporal(TemporalType.DATE)
//	@ReportField(type = FieldType.Date, importance = FieldImportance.Average)
//	public Date getMembershipDate() {
//		return this.membershipDate;
//	}
//
//	public void setMembershipDate(Date membershipDate) {
//		this.membershipDate = membershipDate;
//	}
//
//	/**
//	 * The date the contractor last reviewed their facility list
//	 */
//	@Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date,requiredPermissions = OpPerms.AllContractors)
//	public Date getViewedFacilities() {
//		return viewedFacilities;
//	}
//
//	public void setViewedFacilities(Date viewedFacilities) {
//		this.viewedFacilities = viewedFacilities;
//	}
//
//    /**
//     * The date the lastPayment expires and the contractor is due to pay another
//     * "period's" membership fee. This should NEVER be null.
//     *
//     * @return
//     */
//	@Temporal(TemporalType.DATE)
//	@Column(nullable = false)
//	@ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllOperators, importance = FieldImportance.Average)
//	public Date getPaymentExpires() {
//		return this.paymentExpires;
//	}
//
//	public void setPaymentExpires(Date paymentExpires) {
//		this.paymentExpires = paymentExpires;
//	}
//
//    /**
//     * Used to determine if we need to calculate the flagColor, audits and
//     * billing
//     *
//     * @return
//     */
//    @ReportField(type = FieldType.Integer, requiredPermissions = OpPerms.DevelopmentEnvironment)
//	public int getNeedsRecalculation() {
//		return needsRecalculation;
//	}
//
//	public void setNeedsRecalculation(int needsRecalculation) {
//		this.needsRecalculation = needsRecalculation;
//	}
//
//	@Transient
//	public void incrementRecalculation() {
//		incrementRecalculation(1);
//	}
//
//	@Transient
//	public void incrementRecalculation(int increment) {
//        this.needsRecalculation += increment;
//		if (this.needsRecalculation > MAX_RECALC) {
//			this.needsRecalculation = MAX_RECALC;
//		}
//	}
//
//	/**
//	 * Sets the date and time when the calculator ran
//	 *
//	 * @return
//	 */
//    @ReportField(type = FieldType.DateTime, requiredPermissions = OpPerms.DevelopmentEnvironment)
//	public Date getLastRecalculation() {
//		return lastRecalculation;
//	}
//
//	public void setLastRecalculation(Date lastRecalculation) {
//		this.lastRecalculation = lastRecalculation;
//	}
//
//	@OneToMany(mappedBy = "contractor")
//	@Sort(type = SortType.NATURAL)
//	public Set<ContractorTrade> getTrades() {
//		return trades;
//	}
//
//	public void setTrades(Set<ContractorTrade> trades) {
//		this.trades = trades;
//	}
//
//	@OneToMany(mappedBy = "picsAccount")
//	public List<AssessmentResultStage> getAssessmentResultStages() {
//		return assessmentResultStages;
//	}
//
//	public void setAssessmentResultStages(List<AssessmentResultStage> assessmentResultStages) {
//		this.assessmentResultStages = assessmentResultStages;
//	}
//
//	@OneToMany(mappedBy = "contractor")
//	public List<ContractorOperatorNumber> getContractorOperatorNumbers() {
//		return contractorOperatorNumbers;
//	}
//
//	public void setContractorOperatorNumbers(List<ContractorOperatorNumber> contractorOperatorNumbers) {
//		this.contractorOperatorNumbers = contractorOperatorNumbers;
//	}
//
//	@Transient
//	public List<OperatorAccount> getGeneralContractorOperatorAccounts() {
//		List<OperatorAccount> gcOperators = new ArrayList<OperatorAccount>();
//		for (ContractorOperator co : operators) {
//			if (co.getOperatorAccount().isGeneralContractor()) {
//				gcOperators.add(co.getOperatorAccount());
//			}
//		}
//
//		return gcOperators;
//	}
//
//	@Transient
//	public List<ContractorOperatorNumber> getVisibleContractorOperatorNumbers(Permissions permissions) {
//		List<ContractorOperatorNumber> visibleContractorOperatorNumbers = new ArrayList<ContractorOperatorNumber>();
//
//		visibleContractorOperatorNumbers.addAll(getContractorOperatorNumbers());
//		Iterator<ContractorOperatorNumber> iterator = visibleContractorOperatorNumbers.iterator();
//
//		while (iterator.hasNext()) {
//			if (!iterator.next().isVisibleTo(permissions)) {
//				iterator.remove();
//			}
//		}
//
//		return visibleContractorOperatorNumbers;
//	}
//
//	/**
//	 * Returns a list of the trades in a sorted order This is needed for the
//	 * struts iterator for the trade cloud
//	 */
//	@Transient
//	public List<ContractorTrade> getTradesSorted() {
//		List<ContractorTrade> list = new ArrayList<ContractorTrade>();
//		list.addAll(trades);
//		return list;
//	}
//
//	@Transient
//	public ContractorTrade getTopTrade() {
//		ContractorTrade topTrade = null;
//		List<ContractorTrade> listOfSameTradeActivity = new ArrayList<ContractorTrade>();
//
//		for (ContractorTrade trade : getTradesSorted()) {
//            if (!trade.isSelfPerformed())
//                continue;
//
//			if (topTrade == null || trade.getActivityPercent() > topTrade.getActivityPercent()) {
//				topTrade = trade;
//				listOfSameTradeActivity.clear();
//				listOfSameTradeActivity.add(trade);
//			} else if (trade.getActivityPercent() == topTrade.getActivityPercent()) {
//				listOfSameTradeActivity.add(trade);
//			}
//		}
//
//		if (listOfSameTradeActivity.size() > 1) {
//			topTrade = null;
//			for (ContractorTrade trade : listOfSameTradeActivity) {
//				if (topTrade == null || trade.getTrade().getNaicsTRIRI() > topTrade.getTrade().getNaicsTRIRI()) {
//					topTrade = trade;
//				}
//			}
//		}
//
//		return topTrade;
//	}
//
//	@Transient
//	public String getTopTradesNaicsCode() {
//		Trade trade = getTopTrade().getTrade();
//		while (trade != null) {
//			for (TradeAlternate alternate : trade.getAlternates()) {
//				if ("NAICS".equals(alternate.getCategory())) {
//					return alternate.getName();
//				}
//			}
//			trade = trade.getParent();
//		}
//
//		return "0";
//	}
//
//	public String getTradesSelf() {
//		return tradesSelf;
//	}
//
//	public void setTradesSelf(String tradesSelf) {
//		this.tradesSelf = tradesSelf;
//	}
//
//	public String getTradesSub() {
//		return tradesSub;
//	}
//
//	public void setTradesSub(String tradesSub) {
//		this.tradesSub = tradesSub;
//	}
//
//	@Temporal(TemporalType.TIMESTAMP)
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getTradesUpdated() {
//		return tradesUpdated;
//	}
//
//	public void setTradesUpdated(Date tradesUpdated) {
//		this.tradesUpdated = tradesUpdated;
//	}
//
//	@Enumerated(EnumType.STRING)
//    @ReportField(type = FieldType.String, requiredPermissions = OpPerms.AllContractors)
//	public LcCorPhase getLcCorPhase() {
//		return lcCorPhase;
//	}
//
//	public void setLcCorPhase(LcCorPhase lcCorPhase) {
//		this.lcCorPhase = lcCorPhase;
//	}
//
//	@Temporal(TemporalType.DATE)
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getLcCorNotification() {
//		return lcCorNotification;
//	}
//
//	public void setLcCorNotification(Date lcCorNotification) {
//		this.lcCorNotification = lcCorNotification;
//	}
//
//	/**
//	 * All contractors should update their trades every 6 months
//	 *
//	 * @return
//	 */
//	@Transient
//	public boolean isNeedsTradesUpdated() {
//		if (tradesUpdated == null) {
//			return true;
//		}
//
//		Calendar daysAgo = Calendar.getInstance();
//		daysAgo.add(Calendar.MONTH, -6);
//
//		return daysAgo.after(tradesUpdated);
//	}
//
//	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average, requiredPermissions = OpPerms.PicsScore)
//	public int getScore() {
//		return score;
//	}
//
//	public void setScore(int score) {
//		this.score = score;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "welcomeAuditor_id")
//	public User getAuditor() {
//		return auditor;
//	}
//
//	public void setAuditor(User auditor) {
//		this.auditor = auditor;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "recommendedCsrID")
//	public User getRecommendedCsr() {
//		return recommendedCsr;
//	}
//
//	public void setRecommendedCsr(User recommendedCsr) {
//		this.recommendedCsr = recommendedCsr;
//	}
//
//	@Transient
//	public boolean hasCurrentCsr() {
//		User csr = getCurrentCsr();
//		if (csr != null) {
//			return true;
//		}
//		return false;
//	}
//
//	// named get/set for convenient ognl reference from JSPs
//	@Transient
//	public User getCurrentCsr() {
//		AccountUser accountUser = getCurrentAccountUserOfRole(UserAccountRole.PICSCustomerServiceRep);
//		if (accountUser != null) {
//			return accountUser.getUser();
//		}
//		return null;
//	}
//
//	@Transient
//	public void setCurrentCsr(User newCsr, int createdById) {
//		setAuditor(newCsr);
//		makeUserCurrentCsrExpireExistingCsr(newCsr, createdById);
//	}
//
//	@Transient
//	public void makeUserCurrentCsrExpireExistingCsr(User newCsr, int createdById) {
//		expireCurrentAccountUserOfRole(UserAccountRole.PICSCustomerServiceRep);
//		addNewCurrentAccountUserOfRole(newCsr, UserAccountRole.PICSCustomerServiceRep, createdById);
//	}
//
//	// named get/set for convenient ognl reference from JSPs
//	@Transient
//	public User getCurrentInsideSalesRepresentative() {
//		AccountUser accountUser = getCurrentAccountUserOfRole(UserAccountRole.PICSInsideSalesRep);
//		if (accountUser != null) {
//			return accountUser.getUser();
//		}
//		return null;
//	}
//
//	@Transient
//	public void setCurrentInsideSalesRepresentative(User newRep, int createdById) {
//		makeUserCurrentInsideSalesRepExpireExistingRep(newRep, createdById);
//	}
//
//	@Transient
//	public void makeUserCurrentInsideSalesRepExpireExistingRep(User newRep, int createdById) {
//		expireCurrentAccountUserOfRole(UserAccountRole.PICSInsideSalesRep);
//		addNewCurrentAccountUserOfRole(newRep, UserAccountRole.PICSInsideSalesRep, createdById);
//	}
//
//	@Transient
//	public boolean isPaymentOverdue() {
//		for (Invoice invoice : getInvoices()) {
//			if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 && invoice.getStatus().isUnpaid()
//					&& invoice.getDueDate() != null && invoice.getDueDate().before(new Date())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Uses the OshaVisitor to gather all the data
//	 *
//	 * @return
//	 */
//	@Transient
//	public OshaOrganizer getOshaOrganizer() {
//		if (oshaOrganizer == null) {
//			oshaOrganizer = new OshaOrganizer();
//			for (OshaAudit audit : this.getOshaAudits()) {
//				if (audit.isVerified()) {
//					audit.accept(oshaOrganizer);
//				}
//			}
//		}
//
//		return oshaOrganizer;
//	}
//
//	@Transient
//	public List<OshaAudit> getOshaAudits() {
//		if (oshaAudits == null || oshaAudits.size() == 0) {
//			oshaAudits = new ArrayList<OshaAudit>();
//			for (ContractorAudit audit : getAudits()) {
//				if (audit.getAuditType().isAnnualAddendum()) {
//					oshaAudits.add(new OshaAudit(audit));
//				}
//			}
//		}
//		return this.oshaAudits;
//	}
//
//	@Transient
//	public List<ContractorAudit> getCurrentAnnualUpdates() {
//		List<ContractorAudit> currentAnnualUpdates = getSortedAnnualUpdates();
//
//		if (currentAnnualUpdates.size() >= 4) {
//			boolean trimBeginning = false;
//			ContractorAudit audit = currentAnnualUpdates.get(currentAnnualUpdates.size() - 1);
//			for (ContractorAuditOperator cao : audit.getOperators()) {
//				if (cao.isVisible() && cao.getStatus().isComplete()) {
//					trimBeginning = true;
//					break;
//				}
//			}
//
//			if (trimBeginning) {
//				currentAnnualUpdates.remove(0);
//			} else {
//				currentAnnualUpdates.remove(audit);
//			}
//		}
//
//		return currentAnnualUpdates;
//	}
//
//    @Transient
//    public YearList getYearList() {
//        if (yearList == null)
//            return new YearList();
//        return yearList;
//    }
//
//    public void setYearList(YearList yearList) {
//        this.yearList = yearList;
//    }
//
//    @Deprecated
//    @Transient
//    public Map<MultiYearScope, ContractorAudit> getAfterPendingAnnualUpdates() {
//        Map<MultiYearScope, ContractorAudit> annualUpdates = new LinkedHashMap<MultiYearScope, ContractorAudit>();
//        Map<Integer, ContractorAudit> annuals = new LinkedHashMap<Integer, ContractorAudit>();
//        YearList years = getYearList();
//
//        for (ContractorAudit annualUpdate : getSortedAnnualUpdates()) {
//            if (annualUpdate.hasCaoStatusAfter(AuditStatus.Pending)) {
//                years.add(annualUpdate.getAuditFor());
//                annuals.put(Integer.parseInt(annualUpdate.getAuditFor()), annualUpdate);
//            }
//        }
//
//        annualUpdates.put(MultiYearScope.LastYearOnly,
//                annuals.get(years.getYearForScope(MultiYearScope.LastYearOnly)));
//        annualUpdates.put(MultiYearScope.TwoYearsAgo,
//                annuals.get(years.getYearForScope(MultiYearScope.TwoYearsAgo)));
//        annualUpdates.put(MultiYearScope.ThreeYearsAgo,
//                annuals.get(years.getYearForScope(MultiYearScope.ThreeYearsAgo)));
//
//        return annualUpdates;
//
//    }
//
//    @Deprecated
//	@Transient
//	public Map<MultiYearScope, ContractorAudit> getCompleteAnnualUpdates() {
//		Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new LinkedHashMap<MultiYearScope, ContractorAudit>();
//		Map<Integer, ContractorAudit> annuals = new LinkedHashMap<Integer, ContractorAudit>();
//		YearList years = getYearList();
//
//		for (ContractorAudit annualUpdate : getSortedAnnualUpdates()) {
//			if (annualUpdate.hasCaoStatus(AuditStatus.Complete)) {
//				years.add(annualUpdate.getAuditFor());
//				annuals.put(Integer.parseInt(annualUpdate.getAuditFor()), annualUpdate);
//			}
//		}
//
//		completeAnnualUpdates.put(MultiYearScope.LastYearOnly,
//				annuals.get(years.getYearForScope(MultiYearScope.LastYearOnly)));
//		completeAnnualUpdates.put(MultiYearScope.TwoYearsAgo,
//				annuals.get(years.getYearForScope(MultiYearScope.TwoYearsAgo)));
//		completeAnnualUpdates.put(MultiYearScope.ThreeYearsAgo,
//				annuals.get(years.getYearForScope(MultiYearScope.ThreeYearsAgo)));
//
//		return completeAnnualUpdates;
//	}
//
//	@Transient
//	public List<ContractorAudit> getSortedAnnualUpdates() {
//		List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
//		for (ContractorAudit contractorAudit : getAudits()) {
//			if (contractorAudit.getAuditType().isAnnualAddendum() && contractorAudit.getExpiresDate() != null
//					&& !contractorAudit.isExpired()) {
//				annualAList.add(contractorAudit);
//			}
//		}
//		Collections.sort(annualAList, new ContractorAuditComparator("auditFor -1"));
//		return annualAList;
//	}
//
//    @Deprecated
//    @Transient
//    public List<ContractorAudit> getAuditByAuditType(AuditType auditType) {
//        List<ContractorAudit> auditList = new ArrayList<ContractorAudit>();
//
//        for (ContractorAudit ca:getAudits()) {
//            if (ca.getAuditType().equals(auditType)) {
//                auditList.add(ca);
//            }
//        }
//
//        return auditList;
//    }
//
//	@ReportField(type = FieldType.Boolean, requiredPermissions = OpPerms.AllContractors, importance = FieldImportance.Average)
//	public boolean isRenew() {
//		return renew;
//	}
//
//	public void setRenew(boolean renew) {
//		this.renew = renew;
//	}
//
//    @ReportField(type = FieldType.Boolean, requiredPermissions = OpPerms.AllContractors)
//	public boolean isAutoAddClientSite() {
//		return autoAddClientSite;
//	}
//
//	public void setAutoAddClientSite(boolean autoAddClientSite) {
//		this.autoAddClientSite = autoAddClientSite;
//	}
//
//	/**
//	 * The last day someone added a facility to this contractor. This is used to
//	 * prorate upgrade amounts
//	 *
//	 * @return
//	 */
//	@Temporal(TemporalType.DATE)
//	@ReportField(type = FieldType.Date, requiredPermissions = OpPerms.Billing, importance = FieldImportance.Average)
//	public Date getLastUpgradeDate() {
//		return lastUpgradeDate;
//	}
//
//	public void setLastUpgradeDate(Date lastUpgradeDate) {
//		this.lastUpgradeDate = lastUpgradeDate;
//	}
//
//	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average, requiredPermissions = OpPerms.AllContractors)
//	public BigDecimal getBalance() {
//		return balance;
//	}
//
//	public void setBalance(BigDecimal balance) {
//		this.balance = balance;
//	}
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name = "agreementDate")
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getAgreementDate() {
//		return agreementDate;
//	}
//
//	public void setAgreementDate(Date agreementDate) {
//		this.agreementDate = agreementDate;
//	}
//
//	@ManyToOne
//	@JoinColumn(name = "agreedBy")
//	public User getAgreedBy() {
//		return agreedBy;
//	}
//
//	public void setAgreedBy(User agreedBy) {
//		this.agreedBy = agreedBy;
//	}
//
//	@Transient
//	public boolean isHasUpgrade() {
//		for (FeeClass feeClass : this.getFees().keySet()) {
//			if (this.getFees().get(feeClass).isUpgrade()) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	@Transient
//	public boolean isHasFreeMembership() {
//		return this.getPayingFacilities() == 0 || !this.isMustPayB();
//	}
//
//	/**
//	 * con.getFees().get(FeeClass.DocuGUARD).getNewLevel();
//	 * con.getFees().getDocuGUARD().getNewLevel();
//	 *
//	 * @return
//	 */
//	@OneToMany(mappedBy = "contractor", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//	@MapKey(name = "feeClass")
//	@Sort(type = SortType.NATURAL)
//	public Map<FeeClass, ContractorFee> getFees() {
//		return fees;
//	}
//
//	public void setFees(Map<FeeClass, ContractorFee> fees) {
//		this.fees = fees;
//	}
//
//	@OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Transaction.class)
//	@Where(clause = "tableType='I'")
//	public List<Invoice> getInvoices() {
//		return invoices;
//	}
//
//	public void setInvoices(List<Invoice> invoices) {
//		this.invoices = invoices;
//	}
//
//    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Transaction.class)
//    @Where(clause = "tableType='C'")
//    public List<InvoiceCreditMemo> getCreditMemos() {
//        return creditMemos;
//    }
//
//    public void setCreditMemos(List<InvoiceCreditMemo> creditMemos) {
//        this.creditMemos = creditMemos;
//    }
//
//	@OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Transaction.class)
//	@Where(clause = "tableType='P'")
//	public List<Payment> getPayments() {
//		return payments;
//	}
//
//	public void setPayments(List<Payment> payments) {
//		this.payments = payments;
//	}
//
//	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Transaction.class)
//	@Where(clause = "tableType='R'")
//	public List<Refund> getRefunds() {
//		return refunds;
//	}
//
//	public void setRefunds(List<Refund> refunds) {
//		this.refunds = refunds;
//	}
//
//	@OneToMany(mappedBy = "contractor", cascade = {CascadeType.ALL})
//	public Set<FlagCriteriaContractor> getFlagCriteria() {
//		return flagCriteria;
//	}
//
//	public void setFlagCriteria(Set<FlagCriteriaContractor> flagCriteria) {
//		this.flagCriteria = flagCriteria;
//	}
//
//	@OneToMany(mappedBy = "contractor", cascade = {CascadeType.ALL})
//	public Set<FlagDataOverride> getFlagDataOverrides() {
//		return flagDataOverrides;
//	}
//
//	public void setFlagDataOverrides(Set<FlagDataOverride> flagDataOverrides) {
//		this.flagDataOverrides = flagDataOverrides;
//	}
//
//	public boolean newMember() {
//		return (membershipDate == null || upgradingFromBidOnlyOrListOnlyAccountToFull());
//	}
//
//	private boolean upgradingFromBidOnlyOrListOnlyAccountToFull() {
//		return (getAccountLevel().isFull() && payingBidOnlyOrFeeOnly());
//	}
//
//	private boolean payingBidOnlyOrFeeOnly() {
//		return (!fees.get(FeeClass.BidOnly).getCurrentLevel().isFree() || !fees.get(FeeClass.ListOnly)
//				.getCurrentLevel().isFree());
//
//	}
//
//    public boolean pendingRequestedOrActive() {
//        return status.isPending() || status.isActive() || status.isRequested();
//    }
//
//    public boolean pendingOrActive() {
//		return status.isPending() || status.isActive();
//	}
//
//    public boolean isHasEmployeeGuard() {
//        return hasEmployeeGuard;
//    }
//
//    public void setHasEmployeeGuard(boolean hasEmployeeGuard) {
//        this.hasEmployeeGuard = hasEmployeeGuard;
//    }
//
//	@Transient
//	public Boolean hasPastDueInvoice() {
//		for (Invoice in : invoices) {
//			if (in.getStatus().equals(TransactionStatus.Unpaid)) {
//				if (in.getDueDate() == null || in.getDueDate().before(new Date())) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	@Transient
//	public boolean hasAuditWithOnlyInvisibleCaos() {
//		for (ContractorAudit audit : this.audits) {
//			if (audit.hasOnlyInvisibleCaos()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Transient
//	public Set<String> getCountries() {
//		Set<String> countries = new HashSet<String>();
//		for (ContractorOperator co : getNonCorporateOperators()) {
//			try {
//				countries.add(co.getOperatorAccount().getCountry().getIsoCode());
//			} catch (Exception justIgnoreThisOperator) {
//			}
//		}
//		return countries;
//	}
//
//	@Transient
//	public CreditCard getCreditCard() {
//		try {
//			BrainTree brainTree = SpringUtils.getBean(SpringUtils.BrainTree);
//			return brainTree.getCreditCard(this);
//		} catch (Exception e) {
//			logger.error("An error occurred while trying to get the Credit Card info for contractor with id = {}", id,
//					e);
//		}
//
//		return null;
//	}
//
//	@Transient
//	public String getCcNumber() {
//		String cardNumber = getCreditCard().getCardNumber();
//		return cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
//	}
//
//	@Transient
//	public boolean isAgreementInEffect() {
//		if (agreementDate == null) {
//			return false;
//		}
//
//		return agreementDate.after(USER_AGREEMENT_CHANGED);
//	}
//
//	@Transient
//	public boolean isAgreed() {
//		return (agreementDate != null);
//	}
//
//	@Transient
//	public boolean isFinanciallyReadyForAudits() {
//		double halfMembership = 0.0;
//		for (FeeClass feeClass : this.getFees().keySet()) {
//			if (!this.getFees().get(feeClass).getCurrentLevel().isFree() && feeClass.isMembership()) {
//				halfMembership += this.getFees().get(feeClass).getCurrentAmount().doubleValue();
//			}
//		}
//		halfMembership *= 0.5;
//
//		double balance = getBalance().doubleValue();
//		return balance < halfMembership;
//	}
//
//	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Average)
//	public boolean getSoleProprietor() {
//		return soleProprietor;
//	}
//
//	public void setSoleProprietor(boolean soleProprietor) {
//		this.soleProprietor = soleProprietor;
//	}
//
//	public void setCompetitorMembership(Boolean competitorMembership) {
//		this.competitorMembership = competitorMembership;
//	}
//
//	public Boolean getCompetitorMembership() {
//		return competitorMembership;
//	}
//
//    @ReportField(type = FieldType.Boolean, requiredPermissions = OpPerms.AllContractors)
//	public boolean isShowInDirectory() {
//		return showInDirectory;
//	}
//
//	public void setShowInDirectory(boolean showInDirectory) {
//		this.showInDirectory = showInDirectory;
//	}
//
//	@Enumerated(EnumType.STRING)
//	@ReportField(type = FieldType.AccountLevel, importance = FieldImportance.Average)
//	public AccountLevel getAccountLevel() {
//		return accountLevel;
//	}
//
//	public void setAccountLevel(AccountLevel accountLevel) {
//		this.accountLevel = accountLevel;
//	}
//
//	@Transient
//	public BigDecimal getNewMembershipAmount() {
//		BigDecimal newTotal = BigDecimal.ZERO;
//		for (ContractorFee fee : fees.values()) {
//			if (fee.getFeeClass().isMembership()) {
//				newTotal = newTotal.add(fee.getNewAmount());
//			}
//		}
//
//		return newTotal;
//	}
//
//	@Transient
//	public BigDecimal getCurrentMembershipAmount() {
//		BigDecimal currentTotal = BigDecimal.ZERO;
//		for (ContractorFee fee : fees.values()) {
//			if (fee.getFeeClass().isMembership()) {
//				currentTotal = currentTotal.add(fee.getCurrentAmount());
//			}
//		}
//
//		return currentTotal;
//	}
//
//	@Transient
//	public boolean isListOnlyEligible() {
//		// Low Risk Material Supplier Only
//		// or is Material Supplier who only works for CEDA
//		if (isMaterialSupplierOnly()
//				&& (getProductRisk().equals(LowMedHigh.Low) || onlyWorksFor(new int[]{OperatorAccount.CEDA_CANADA,
//				OperatorAccount.CEDA_USA}))) {
//			return true;
//		} else if (isOffsiteServices() && !isOnsiteServices() && getSafetyRisk().equals(LowMedHigh.Low)) {
//			return true;
//		} else if (getSoleProprietor() && !isOnsiteServices() && (isOffsiteServices() || isMaterialSupplier())) {
//			return true;
//		}
//
//		return false;
//	}
//
//	@Transient
//	public boolean onlyWorksFor(OperatorAccount operator) {
//		return onlyWorksFor(operator.getId());
//	}
//
//	@Transient
//	public boolean onlyWorksFor(List<OperatorAccount> operators) {
//		Set<Integer> IDs = new HashSet<Integer>(operators.size());
//		for (OperatorAccount operator : operators) {
//			IDs.add(operator.getId());
//		}
//		return onlyWorksFor(IDs);
//	}
//
//	@Transient
//	public boolean onlyWorksFor(int operatorId) {
//		return onlyWorksFor(new int[]{operatorId});
//	}
//
//	@Transient
//	public boolean onlyWorksFor(int[] operatorIDs) {
//		Set<Integer> IDs = new HashSet<Integer>(operatorIDs.length);
//		for (int id : operatorIDs) {
//			IDs.add(id);
//		}
//		return onlyWorksFor(IDs);
//	}
//
//	@Transient
//	public boolean onlyWorksFor(Set<Integer> operatorIDs) {
//		// This is an inclusive 'or'. (As in, "Coffee or desert?" vs.
//		// "Soup or Salad?")
//		// So long as the operators for this contractor are a subset of the
//		// parameter list,
//		// this method will return true.
//		if (getOperatorAccounts().isEmpty()) {
//			return false;
//		}
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			if (!operatorIDs.contains(operator.getId())) {
//				if (operator.getParent() != null && operatorIDs.contains(operator.getParent().getId())) {
//					continue;
//				}
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public boolean willExpireSoon(int daysBeforeExpiration) {
//		int daysToExpire = DateBean.getDateDifference(paymentExpires);
//
//		return (daysToExpire <= daysBeforeExpiration);
//	}
//
//    @Transient
//    public boolean isHasPaymentExpired() {
//        if (getPaymentExpires() == null) {
//            return true;
//        }
//
//        return new Date().after(getPaymentExpires());
//    }
//
//    @Transient
//	public float getWeightedIndustryAverage() {
//		float sum = 0;
//		int activitySum = 0;
//
//		if (trades.size() > 0) {
//			if (hasSelfPerformedTrades()) {
//				for (ContractorTrade t : trades) {
//					if (t.isSelfPerformed()) {
//						sum += t.getActivityPercent() * t.getTrade().getNaicsTRIRI();
//						activitySum += t.getActivityPercent();
//					}
//				}
//			} else {
//				for (ContractorTrade t : trades) {
//					sum += t.getActivityPercent() * t.getTrade().getNaicsTRIRI();
//					activitySum += t.getActivityPercent();
//				}
//			}
//			return sum / activitySum;
//		} else {
//			return 0;
//		}
//	}
//
//	@Transient
//	public boolean hasWiaCriteria() {
//		for (FlagCriteriaContractor fcc : flagCriteria) {
//			if (OshaRateType.TrirWIA.equals(fcc.getCriteria().getOshaRateType())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Transient
//	public boolean hasWiaCriteria(OshaType ot) {
//		for (FlagCriteriaContractor fcc : flagCriteria) {
//			if (ot.equals(fcc.getCriteria().getOshaType())
//					&& OshaRateType.TrirWIA.equals(fcc.getCriteria().getOshaRateType())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Transient
//	public boolean hasSelfPerformedTrades() {
//		for (ContractorTrade t : trades) {
//			if (t.isSelfPerformed()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//    /**
//     * @return a list of invoices sorted by creationDate DESC
//     */
//    @Transient
//    public List<Invoice> getSortedInvoices() {
//        List<Invoice> sortedInvoiceList = new ArrayList<Invoice>(getInvoices());
//        Collections.sort(sortedInvoiceList, new Comparator<Invoice>() {
//            public int compare(Invoice invoiceOne, Invoice invoiceTwo) {
//                return invoiceTwo.getCreationDate().compareTo(invoiceOne.getCreationDate());
//            }
//        });
//        return sortedInvoiceList;
//    }
//
//    /**
//	 * @return Null if no unpaid invoices exist
//	 */
//	@Transient
//	public Invoice findLastUnpaidInvoice() {
//		for (Invoice invoice : getSortedInvoices()) {
//			if (invoice.getStatus().isUnpaid()) {
//				return invoice;
//			}
//		}
//
//		return null;
//	}
//
//	@Transient
//	public boolean isEligibleForImportPQF() {
//		// This list is empty because currently Import PQF is not enabled for
//		// any Operators
//		List<Integer> importPQFEligibleOperators = new ArrayList<Integer>();
//
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			if (importPQFEligibleOperators.contains(operator.getTopAccount().getId())) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	@Transient
//	public boolean isContractorTypeRequired(ContractorType conType) {
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			Set<ContractorType> operatorTypes = operator.getAccountTypes();
//			if (operatorTypes.size() == 1 && operatorTypes.contains(conType)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Transient
//	public List<OperatorAccount> getOperatorsThatRequireContractorType(ContractorType conType) {
//		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
//
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			Set<ContractorType> operatorTypes = operator.getAccountTypes();
//			if (operatorTypes.size() == 1 && operatorTypes.contains(conType)) {
//				operators.add(operator);
//			}
//		}
//		return operators;
//	}
//
//	@Transient
//	public List<String> getOperatorsNamesThatRequireContractorType(ContractorType conType) {
//		List<String> operators = new ArrayList<String>();
//
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			Set<ContractorType> operatorTypes = operator.getAccountTypes();
//			if (operatorTypes.size() == 1 && operatorTypes.contains(conType)) {
//				operators.add(operator.getName());
//			}
//		}
//		return operators;
//	}
//
//	@Transient
//	public boolean meetsOperatorRequirements(OperatorAccount operator) {
//		boolean meetsOperatorsRequirements = false;
//
//		Set<ContractorType> contractorTypes = getAccountTypes();
//
//		for (ContractorType opType : operator.getAccountTypes()) {
//			if (contractorTypes.contains(opType)) {
//				meetsOperatorsRequirements = true;
//			}
//		}
//
//		return meetsOperatorsRequirements;
//	}
//
//	@Transient
//	public ContractorOperator getContractorOperatorForOperator(OperatorAccount operator) {
//		for (ContractorOperator op : getOperators()) {
//			if (op.getOperatorAccount().getId() == operator.getId()) {
//				return op;
//			}
//		}
//
//		return null;
//	}
//
//	@Transient
//	public boolean isWorksForOperator(int operator) {
//		for (OperatorAccount operatorAccount : getOperatorAccounts()) {
//			if (operatorAccount.getId() == operator) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	// TODO: Remove this if it is not being used.
//	@SuppressWarnings("unused")
//	private void setOshaAudits(List<OshaAudit> oshaAudits) {
//		this.oshaAudits = oshaAudits;
//	}
//
//	@Transient
//	@Deprecated
//	public boolean isOnlyAssociatedWith(int operatorID) {
//		if (getOperators().size() == 0) {
//			return false;
//		}
//
//		for (OperatorAccount operator : getOperatorAccounts()) {
//			if (operator.getTopAccount().getId() != operatorID) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getLastContactedByAutomatedEmailDate() {
//		return lastContactedByAutomatedEmailDate;
//	}
//
//	public void setLastContactedByAutomatedEmailDate(Date lastContactedByAutomatedEmailDate) {
//		this.lastContactedByAutomatedEmailDate = lastContactedByAutomatedEmailDate;
//	}
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "lastContactedByInsideSales", nullable = true)
//	public User getLastContactedByInsideSales() {
//		return lastContactedByInsideSales;
//	}
//
//	public void setLastContactedByInsideSales(User SalesUser) {
//		this.lastContactedByInsideSales = SalesUser;
//	}
//
//	public void setLastContactedByInsideSales(int salesUserID) {
//		User tempUser = new User();
//		tempUser.setId(salesUserID);
//		setLastContactedByInsideSales(tempUser);
//	}
//
//	@Column(name = "europeanUnionVATnumber", nullable = true)
//    @ReportField(requiredPermissions = OpPerms.Billing)
//	public String getVatId() {
//		return vatId;
//	}
//
//	public void setVatId(String vatId) {
//		this.vatId = vatId;
//	}
//
//	public boolean hasVatId() {
//		return !Strings.isEmpty(vatId);
//	}
//
//	@Transient
//	public boolean isHasEmployeeGUARDTag() {
//		for (ContractorTag contractorTag : getOperatorTags()) {
//			OperatorTag operatorTag = contractorTag.getTag();
//
//			if (operatorTag != null && operatorTag.getCategory().isEmployeeGUARD()) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	@Transient
//	public boolean hasTagCategory(OperatorTagCategory category) {
//		if (category != null) {
//			for (ContractorTag contractorTag : getOperatorTags()) {
//				OperatorTag tag = contractorTag.getTag();
//
//				if (category == tag.getCategory()) {
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}
//
//	@Transient
//	public boolean containsOnlySafeCharacters(String str) {
//		return getInputValidator().containsOnlySafeCharacters(str);
//	}
//
//	@Transient
//	public boolean isCompanyNameNotTaken(String companyName) {
//		return !getInputValidator().isCompanyNameTaken(companyName);
//	}
//
//	@Transient
//	public boolean isValidVAT(String vat) {
//        TaxIdValidator taxIdValidator = new TaxIdValidatorFactory().buildTaxIdValidator(country);
//        if (taxIdValidator != null) {
//            try {
//                taxIdValidator.validated(country, vat);
//            } catch (Exception e) {
//                return false;
//            }
//        }
//
//		if (StringUtils.isNotEmpty(vat)) {
//			return getInputValidator().containsOnlySafeCharacters(vat);
//		}
//
//		return true;
//	}
//
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getLastContactedByInsideSalesDate() {
//		return lastContactedByInsideSalesDate;
//	}
//
//	public void setLastContactedByInsideSalesDate(Date lastContactedByInsideSalesDate) {
//		this.lastContactedByInsideSalesDate = lastContactedByInsideSalesDate;
//	}
//
//	@Column(name = "followupDate")
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getFollowUpDate() {
//		return followUpDate;
//	}
//
//	public void setFollowUpDate(Date followUpDate) {
//		this.followUpDate = followUpDate;
//	}
//
//    @ReportField(type = FieldType.Integer, requiredPermissions = OpPerms.AllContractors)
//	public int getContactCountByEmail() {
//		return contactCountByEmail;
//	}
//
//	public void setContactCountByEmail(int contactCountByEmail) {
//		this.contactCountByEmail = contactCountByEmail;
//	}
//
//	@Transient
//	public void contactByEmail() {
//		contactCountByEmail++;
//	}
//
//	@Transient
//	public void contactByPhone() {
//		contactCountByPhone++;
//	}
//
//    @ReportField(type = FieldType.Integer, requiredPermissions = OpPerms.AllContractors)
//	public int getContactCountByPhone() {
//		return contactCountByPhone;
//	}
//
//	public void setContactCountByPhone(int contactCountByPhone) {
//		this.contactCountByPhone = contactCountByPhone;
//	}
//
//	@Transient
//	public int getTotalContactCount() {
//		return getContactCountByEmail() + getContactCountByPhone();
//	}
//
//	@Column(name = "expiresOnDate")
//    @ReportField(type = FieldType.Date, requiredPermissions = OpPerms.AllContractors)
//	public Date getRegistrationRequestExpiresOn() {
//		return registrationRequestExpiresOn;
//	}
//
//	public void setRegistrationRequestExpiresOn(Date registrationRequestExpiresOn) {
//		this.registrationRequestExpiresOn = registrationRequestExpiresOn;
//	}
//
//	@Enumerated(EnumType.ORDINAL)
//    @ReportField(type = FieldType.LowMedHigh, requiredPermissions = OpPerms.AllContractors)
//	public LowMedHigh getInsideSalesPriority() {
//		return insideSalesPriority;
//	}
//
//	public void setInsideSalesPriority(LowMedHigh insideSalesPriority) {
//		this.insideSalesPriority = insideSalesPriority;
//	}
//
//    public String getSalesRepSalesForceID() {
//        return salesRepSalesForceID;
//    }
//
//    public void setSalesRepSalesForceID(String salesRepSalesForceID) {
//        this.salesRepSalesForceID = salesRepSalesForceID;
//    }
//
//    public String getRegistrationHash() {
//		return registrationHash;
//	}
//
//	public void setRegistrationHash(String registrationHash) {
//		this.registrationHash = registrationHash;
//	}
//
//	@Transient
//	public void generateRegistrationHash() {
//		String seed = "request" + this.getId() + "-" + new Date();
//		setRegistrationHash(Strings.hashUrlSafe(seed));
//	}
//
//	@Transient
//	public ContractorOperator getContractorOperatorWithClosestDeadline() {
//		ContractorOperator closestDeadline = null;
//
//		for (ContractorOperator contractorOperator : getOperators()) {
//			if (contractorOperator.getDeadline() != null) {
//				if (closestDeadline == null || closestDeadline.getDeadline().after(contractorOperator.getDeadline())) {
//					closestDeadline = contractorOperator;
//				}
//			}
//		}
//
//		return closestDeadline;
//	}
//
//	@Transient
//	public InputValidator getInputValidator() {
//		if (inputValidator == null) {
//			return SpringUtils.getBean("InputValidator");
//		}
//
//		return inputValidator;
//	}
//
//	@Transient
//	public VATValidator getVatValidator() {
//		if (vatValidator == null) {
//			return SpringUtils.getBean("VATValidator");
//		}
//
//		return vatValidator;
//	}
//
//	@Transient
//	public CountryDAO getCountryDao() {
//		if (countryDAO == null) {
//			return SpringUtils.getBean("CountryDAO");
//		}
//
//		return countryDAO;
//	}
//
//	@Override
//	@Transient
//	public String getViewLink() {
//        return ("ContractorView.action?id=" + this.id);
//	}
//
//	public InsuranceCriteriaContractorOperator getInsuranceCriteriaContractorOperators(int flagCriteriaId, int opId) {
//		for (InsuranceCriteriaContractorOperator insurance : getInsuranceCriteriaContractorOperators()) {
//			if (insurance.getFlagCriteria().getId() == flagCriteriaId && insurance.getOperatorAccount().getId() == opId) {
//				return insurance;
//			}
//		}
//		return null;
//	}
//
//	public OperatorAccount getOperator(int opId) {
//		for (OperatorAccount operator : getOperatorAndCorporateAccounts()) {
//			if (operator.getId() == opId) {
//				return operator;
//			}
//		}
//		return null;
//	}
//
//	public static ContractorAccountBuilder builder() {
//		return new ContractorAccountBuilder();
//	}
//
//	@Transient
//	public boolean hasOperatorWithCompetencyRequiringDocumentation() {
//		for (ContractorOperator contractorOperator : getOperators()) {
//			if (contractorOperator.getOperatorAccount().hasCompetencyRequiringDocumentation()) {
//				return true;
//			}
//		}
//
//		return false;
//	}
}
