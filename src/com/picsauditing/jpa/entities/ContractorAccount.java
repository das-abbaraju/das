package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.comparators.ContractorAuditComparator;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_info")
// Cache is only on the operator account now, if this works.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class ContractorAccount extends Account implements JSONable {
	private String taxId;
	private String logoFile;
	private String brochureFile;
	private String mustPay = "Yes";
	private OperatorAccount requestedBy;
	private String billingAddress;
	private String billingCity;
	private State billingState;
	private Country billingCountry;
	private String billingZip;
	private String ccEmail;
	private Date membershipDate;
	private int payingFacilities;
	private User auditor;
	private LowMedHigh safetyRisk = LowMedHigh.None;
	private Date safetyRiskVerified;
	private LowMedHigh productRisk = LowMedHigh.None;
	private Date productRiskVerified;
	private LowMedHigh transportationRisk = LowMedHigh.None;
	private Date transportationRiskVerified;
	private Date viewedFacilities;
	private Float emrAverage;
	private Float trirAverage;
	private Float lwcrAverage;
	private PaymentMethod paymentMethod = PaymentMethod.CreditCard;
	private boolean ccOnFile = false;
	private Date ccExpiration;
	private Webcam webcam;
	private boolean soleProprietor;
	private Boolean competitorMembership;
	private boolean showInDirectory = true;
	private AccountLevel accountLevel = AccountLevel.Full;

	private Date paymentExpires;
	private boolean renew = true;
	private Date lastUpgradeDate;
	private BigDecimal balance;
	private Map<FeeClass, ContractorFee> fees = new TreeMap<FeeClass, ContractorFee>();
	private Date agreementDate;
	private User agreedBy;
	private List<Invoice> invoices = new ArrayList<Invoice>();
	private List<Payment> payments = new ArrayList<Payment>();
	private List<Refund> refunds = new ArrayList<Refund>();
	private Set<FlagCriteriaContractor> flagCriteria = new HashSet<FlagCriteriaContractor>();
	private Set<FlagDataOverride> flagDataOverrides = new HashSet<FlagDataOverride>();

	private int needsRecalculation;
	private Date lastRecalculation;
	private String tradesSelf;
	private String tradesSub;
	private int score;
	private Date tradesUpdated;

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();
	protected List<Certificate> certificates = new ArrayList<Certificate>();
	protected List<JobContractor> jobSites = new ArrayList<JobContractor>();
	protected Set<ContractorTrade> trades = new TreeSet<ContractorTrade>();
	protected List<AssessmentResultStage> assessmentResultStages = new ArrayList<AssessmentResultStage>();

	// Transient helper methods
	protected OshaOrganizer oshaOrganizer = null;
	protected Map<String, AuditData> emrs = null;

	// Agreement Changed on Release date 6/3/2010
	public static final Date USER_AGREEMENT_CHANGED = DateBean.parseDate("06/03/2010");
	public static final int MAX_RECALC = 127;

	public ContractorAccount() {
		this.type = "Contractor";
	}

	public ContractorAccount(int id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@Where(clause = "expiresDate > NOW() OR expiresDate IS NULL")
	public List<ContractorAudit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

	/**
	 * Only includes the operator accounts, not corporate accounts
	 * 
	 * @return
	 */
	@Transient
	public List<OperatorAccount> getOperatorAccounts() {
		List<OperatorAccount> list = new ArrayList<OperatorAccount>();
		for (ContractorOperator co : operators) {
			if (co.getOperatorAccount().isOperator())
				list.add(co.getOperatorAccount());
		}
		Collections.sort(list);
		return list;
	}

	@Transient
	public List<ContractorOperator> getNonCorporateOperators() {
		return new Grepper<ContractorOperator>() {

			@Override
			public boolean check(ContractorOperator t) {
				return !t.getOperatorAccount().isCorporate();
			}
		}.grep(this.operators);
	}

	@OneToMany(mappedBy = "contractor", cascade = { CascadeType.REMOVE, CascadeType.MERGE })
	public List<ContractorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<ContractorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	@OneToMany(mappedBy = "contractor", cascade = { CascadeType.REMOVE })
	public List<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<Certificate> certificates) {
		this.certificates = certificates;
	}

	@OneToMany(mappedBy = "contractor")
	public List<JobContractor> getJobSites() {
		return jobSites;
	}

	public void setJobSites(List<JobContractor> jobSites) {
		this.jobSites = jobSites;
	}

	@Column(name = "taxID", length = 100)
	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	@Column(name = "logo_file", length = 50)
	public String getLogoFile() {
		return this.logoFile;
	}

	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	@Column(name = "brochure_file", length = 3)
	public String getBrochureFile() {
		return this.brochureFile;
	}

	public void setBrochureFile(String brochureFile) {
		this.brochureFile = brochureFile;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByID")
	public OperatorAccount getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(OperatorAccount requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Column(name = "billingAddress", length = 50)
	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	@Column(name = "billingCity", length = 35)
	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	@ManyToOne
	@JoinColumn(name = "billingState")
	public State getBillingState() {
		return billingState;
	}

	public void setBillingState(State billingState) {
		this.billingState = billingState;
	}

	@ManyToOne
	@JoinColumn(name = "billingCountry")
	public Country getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(Country billingCountry) {
		this.billingCountry = billingCountry;
	}

	@Column(name = "billingZip", length = 10)
	public String getBillingZip() {
		return billingZip;
	}

	public void setBillingZip(String billingZip) {
		this.billingZip = billingZip;
	}

	@Column(name = "ccEmail", length = 50)
	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	@Column(nullable = false)
	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Temporal(TemporalType.DATE)
	public Date getSafetyRiskVerified() {
		return safetyRiskVerified;
	}

	public void setSafetyRiskVerified(Date safetyRiskVerified) {
		this.safetyRiskVerified = safetyRiskVerified;
	}

	@Column(nullable = false)
	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	@Temporal(TemporalType.DATE)
	public Date getProductRiskVerified() {
		return productRiskVerified;
	}

	public void setProductRiskVerified(Date productRiskVerified) {
		this.productRiskVerified = productRiskVerified;
	}

	@Column(nullable = false)
	public LowMedHigh getTransportationRisk() {
		return transportationRisk;
	}

	public void setTransportationRisk(LowMedHigh transportationRisk) {
		this.transportationRisk = transportationRisk;
	}

	@Temporal(TemporalType.DATE)
	public Date getTransportationRiskVerified() {
		return transportationRiskVerified;
	}

	public void setTransportationRiskVerified(Date transportationRiskVerified) {
		this.transportationRiskVerified = transportationRiskVerified;
	}

	@Column(name = "emrAverage")
	@Deprecated
	public Float getEmrAverage() {
		return emrAverage;
	}

	@Deprecated
	public void setEmrAverage(Float emrAverage) {
		this.emrAverage = emrAverage;
	}

	@Column(name = "trirAverage")
	@Deprecated
	public Float getTrirAverage() {
		return trirAverage;
	}

	@Deprecated
	public void setTrirAverage(Float trirAverage) {
		this.trirAverage = trirAverage;
	}

	@Column(name = "lwcrAverage")
	@Deprecated
	public Float getLwcrAverage() {
		return lwcrAverage;
	}

	@Deprecated
	public void setLwcrAverage(Float lwcrAverage) {
		this.lwcrAverage = lwcrAverage;
	}

	// //// BILLING/ACCOUNT - related columns //////
	/**
	 * Determines if this contractor must pay or not. It allows for PICS to grant "free" lifetime accounts to certain
	 * contractors. Yes or No
	 */
	@Column(name = "mustPay", nullable = false, length = 3)
	public String getMustPay() {
		return this.mustPay;
	}

	public void setMustPay(String mustPay) {
		this.mustPay = mustPay;
	}

	@Transient
	public boolean isMustPayB() {
		return "Yes".equals(mustPay);
	}

	@Column(nullable = false)
	public int getPayingFacilities() {
		return this.payingFacilities;
	}

	public void setPayingFacilities(int payingFacilities) {
		this.payingFacilities = payingFacilities;
	}

	@Transient
	public boolean isPaymentMethodStatusValid() {
		if (paymentMethod == null)
			return false;
		if (paymentMethod.isCreditCard()) {
			return isCcValid();
		}
		// If Check
		return true;
	}

	/**
	 * Set to true if we have a credit card on file
	 * 
	 * @return
	 */
	public boolean isCcOnFile() {
		return ccOnFile;
	}

	public void setCcOnFile(boolean ccOnFile) {
		this.ccOnFile = ccOnFile;
	}

	public Date getCcExpiration() {
		return ccExpiration;
	}

	public void setCcExpiration(Date ccExpiration) {
		this.ccExpiration = ccExpiration;
	}

	@Transient
	public boolean isCcValid() {
		if (!ccOnFile)
			return false;

		return isCcExpired();
	}

	@Transient
	public boolean isCcExpired() {
		if (ccExpiration == null)
			// Because this is new, some haven't been loaded yet
			// Assume it's fine for now
			// TODO remove this section once we load all the dates
			return true;

		Calendar expires = Calendar.getInstance();
		expires.setTime(ccExpiration);
		expires.set(Calendar.DAY_OF_MONTH, 1);
		expires.add(Calendar.MONTH, 1);
		expires.add(Calendar.DAY_OF_MONTH, -1);

		return expires.getTime().after(new Date());
	}

	@OneToOne(mappedBy = "contractor")
	public Webcam getWebcam() {
		return webcam;
	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

	/**
	 * The Payment methods are Credit Card and Check
	 * 
	 * @return
	 */
	@Enumerated(EnumType.STRING)
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * The date the contractor was invoiced for their most recent activation/reactivation fee
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getMembershipDate() {
		return this.membershipDate;
	}

	public void setMembershipDate(Date membershipDate) {
		this.membershipDate = membershipDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	/*
	 * The date the contractor last reviewed their facility list
	 */
	public Date getViewedFacilities() {
		return viewedFacilities;
	}

	public void setViewedFacilities(Date viewedFacilities) {
		this.viewedFacilities = viewedFacilities;
	}

	/**
	 * The date the lastPayment expires and the contractor is due to pay another "period's" membership fee. This should
	 * NEVER be null.
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	public Date getPaymentExpires() {
		return this.paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	/**
	 * Used to determine if we need to calculate the flagColor, audits and billing
	 * 
	 * @return
	 */
	public int getNeedsRecalculation() {
		return needsRecalculation;
	}

	public void setNeedsRecalculation(int needsRecalculation) {
		this.needsRecalculation = needsRecalculation;
	}

	@Transient
	public boolean isNeedsRecalculation() {
		return needsRecalculation > 0;
	}

	@Transient
	public void incrementRecalculation() {
		incrementRecalculation(1);
	}

	@Transient
	public void incrementRecalculation(int increment) {
		if (increment > 0 && this.needsRecalculation + increment < MAX_RECALC)
			this.needsRecalculation += increment;
	}

	/**
	 * Sets the date and time when the calculator ran
	 * 
	 * @return
	 */
	public Date getLastRecalculation() {
		return lastRecalculation;
	}

	public void setLastRecalculation(Date lastRecalculation) {
		this.lastRecalculation = lastRecalculation;
	}

	@OneToMany(mappedBy = "contractor")
	@Sort(type = SortType.NATURAL)
	public Set<ContractorTrade> getTrades() {
		return trades;
	}

	public void setTrades(Set<ContractorTrade> trades) {
		this.trades = trades;
	}

	@OneToMany(mappedBy = "picsAccount")
	public List<AssessmentResultStage> getAssessmentResultStages() {
		return assessmentResultStages;
	}

	public void setAssessmentResultStages(List<AssessmentResultStage> assessmentResultStages) {
		this.assessmentResultStages = assessmentResultStages;
	}

	/**
	 * Returns a list of the trades in a sorted order This is needed for the struts iterator for the trade cloud
	 */
	@Transient
	public List<ContractorTrade> getTradesSorted() {
		List<ContractorTrade> list = new ArrayList<ContractorTrade>();
		list.addAll(trades);
		return list;
	}

	public String getTradesSelf() {
		return tradesSelf;
	}

	public void setTradesSelf(String tradesSelf) {
		this.tradesSelf = tradesSelf;
	}

	public String getTradesSub() {
		return tradesSub;
	}

	public void setTradesSub(String tradesSub) {
		this.tradesSub = tradesSub;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTradesUpdated() {
		return tradesUpdated;
	}

	public void setTradesUpdated(Date tradesUpdated) {
		this.tradesUpdated = tradesUpdated;
	}

	/**
	 * All contractors should update their trades every 6 months
	 * 
	 * @return
	 */
	@Transient
	public boolean isNeedsTradesUpdated() {
		if (tradesUpdated == null)
			return true;

		Calendar daysAgo = Calendar.getInstance();
		daysAgo.add(Calendar.MONTH, -6);

		return daysAgo.after(tradesUpdated);
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	// Other relationships //
	@ManyToOne
	@JoinColumn(name = "welcomeAuditor_id")
	/**
	 * The CSR
	 */
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	// /// Transient/Helper Methods ///////

	@Transient
	public boolean isPaymentOverdue() {
		for (Invoice invoice : getInvoices())
			if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 && invoice.getStatus().isUnpaid()
					&& invoice.getDueDate() != null && invoice.getDueDate().before(new Date()))
				return true;
		return false;
	}

	@Transient
	public OshaOrganizer getOshaOrganizer() {
		if (oshaOrganizer == null) {
			oshaOrganizer = new OshaOrganizer(getSortedAnnualUpdates());
		}
		return oshaOrganizer;
	}

	@Transient
	/*
	 * Get a map of the last 3 years of applicable emr data (verified or not)
	 */
	public Map<String, AuditData> getEmrs() {
		if (emrs != null)
			return emrs;

		emrs = new TreeMap<String, AuditData>();
		int number = 0;
		List<ContractorAudit> sortedAudits = getSortedAnnualUpdates();
		for (ContractorAudit audit : sortedAudits) {
			if (number < 4 && audit.hasCaoStatus(AuditStatus.Complete)) {
				// Store the EMR rates into a map for later use
				for (AuditData answer : audit.getData()) {
					if (answer.getQuestion().getId() == AuditQuestion.EMR
							|| (answer.getQuestion().getId() == 2033 && "No".equals(answer.getAnswer()))) {
						if (!Strings.isEmpty(answer.getAnswer())) {
							number++;
							if (answer.getQuestion().getId() != 2033)
								emrs.put(audit.getAuditFor(), answer);
						}
					}
				}
			}
		}

		if (emrs.size() == 4) {
			emrs.remove(((TreeMap<String, AuditData>) emrs).firstKey());
		} else if (emrs.size() > 4)
			throw new RuntimeException("Found [" + emrs.size() + "] EMRs");

		AuditData avg = AuditData.addAverageData(emrs.values());
		if (avg != null && !Strings.isEmpty(avg.getAnswer()))
			emrs.put(OshaAudit.AVG, avg);

		return emrs;
	}

	@Transient
	public Map<MultiYearScope, ContractorAudit> getCompleteAnnualUpdates() {
		Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new LinkedHashMap<MultiYearScope, ContractorAudit>();
		completeAnnualUpdates.put(MultiYearScope.LastYearOnly, null);
		completeAnnualUpdates.put(MultiYearScope.TwoYearsAgo, null);
		completeAnnualUpdates.put(MultiYearScope.ThreeYearsAgo, null);

		Iterator<MultiYearScope> scopeIter = completeAnnualUpdates.keySet().iterator();

		for (ContractorAudit annualUpdate : getSortedAnnualUpdates()) {
			if (scopeIter.hasNext() && annualUpdate.hasCaoStatus(AuditStatus.Complete))
				completeAnnualUpdates.put(scopeIter.next(), annualUpdate);
		}

		return completeAnnualUpdates;
	}

	@Transient
	public List<ContractorAudit> getSortedAnnualUpdates() {
		List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
		for (ContractorAudit contractorAudit : getAudits()) {
			if (contractorAudit.getAuditType().isAnnualAddendum() && contractorAudit.getExpiresDate() != null
					&& !contractorAudit.isExpired()) {
				annualAList.add(contractorAudit);
			}
		}
		Collections.sort(annualAList, new ContractorAuditComparator("auditFor -1"));
		return annualAList;
	}

	public boolean isRenew() {
		return renew;
	}

	public void setRenew(boolean renew) {
		this.renew = renew;
	}

	/**
	 * The last day someone added a facility to this contractor. This is used to prorate upgrade amounts
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getLastUpgradeDate() {
		return lastUpgradeDate;
	}

	public void setLastUpgradeDate(Date lastUpgradeDate) {
		this.lastUpgradeDate = lastUpgradeDate;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "agreementDate")
	public Date getAgreementDate() {
		return agreementDate;
	}

	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}

	@ManyToOne
	@JoinColumn(name = "agreedBy")
	public User getAgreedBy() {
		return agreedBy;
	}

	public void setAgreedBy(User agreedBy) {
		this.agreedBy = agreedBy;
	}

	/**
	 * Set the balance equal to the sum of all unpaid invoices
	 */
	@Transient
	public void syncBalance() {
		/**
		 * Roll up the outstanding balance on all transactions
		 */
		balance = BigDecimal.ZERO;
		for (Invoice invoice : getInvoices()) {
			if (!invoice.getStatus().isVoid())
				balance = balance.add(invoice.getTotalAmount());
		}

		for (Refund refund : getRefunds()) {
			if (!refund.getStatus().isVoid())
				balance = balance.add(refund.getTotalAmount());
		}

		for (Payment payment : getPayments()) {
			if (!payment.getStatus().isVoid())
				balance = balance.subtract(payment.getTotalAmount());
		}

		balance = balance.setScale(2);

		// STart here, call private method and set the contractor.fee
		InvoiceFeeDAO feeDAO = (InvoiceFeeDAO) SpringUtils.getBean("InvoiceFeeDAO");

		boolean foundListOnlyMembership = false;
		boolean foundBidOnlyMembership = false;
		boolean foundDocuGUARDMembership = false;
		boolean foundAuditGUARDMembership = false;
		boolean foundInsureGUARDMembership = false;
		boolean foundEmployeeGUARDMembership = false;
		boolean foundImportPQFFee = false;
		/**
		 * TRUE if we found the most recent membership activation/reactivation or renewal. We're not looking for
		 * upgrades here.
		 */
		boolean foundMembership = false;
		boolean foundMembershipDate = false;
		boolean foundPaymentExpires = false;

		/**
		 * Go through the list of invoices in reverse order (most recent first). Find the first invoice with a
		 * membership line and grab all the invoiceFees.
		 */
		for (Invoice invoice : getSortedInvoices()) {
			if (!invoice.getStatus().isVoid()) {
				for (InvoiceItem invoiceItem : invoice.getItems()) {
					if (!foundMembership && invoiceItem.getInvoiceFee().isMembership()) {
						if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.ListOnly)
								&& !foundListOnlyMembership) {
							foundListOnlyMembership = true;
							InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly,
									getPayingFacilities());
							setCurrentFee(fee, fee.getAmount());
						} else if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.BidOnly)
								&& !foundBidOnlyMembership) {
							foundBidOnlyMembership = true;
							InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly,
									getPayingFacilities());
							setCurrentFee(fee, fee.getAmount());
						} else if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.DocuGUARD)
								&& !foundDocuGUARDMembership) {
							foundDocuGUARDMembership = true;

							if (invoiceItem.getInvoiceFee().isLegacyMembership()) {
								// We have no way of knowing how many paying
								// facilities the contractor had when we
								// transitioned fee levels. Since all
								// DocuGUARD fee amounts are currently the
								// same, set fee level based on current
								// number of paying facilities if contractor
								// paid legacy DocuGUARD fee.
								InvoiceFee newDocuGUARDFee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD,
										this.getPayingFacilities());
								setCurrentFee(newDocuGUARDFee, newDocuGUARDFee.getAmount());

								foundInsureGUARDMembership = true;
								InvoiceFee newInsureGUARDFee = feeDAO.findByNumberOfOperatorsAndClass(
										FeeClass.InsureGUARD, this.getPayingFacilities());
								setCurrentFee(newInsureGUARDFee, newInsureGUARDFee.getAmount());
							} else {
								setCurrentFee(invoiceItem.getInvoiceFee(), invoiceItem.getInvoiceFee().getAmount());
							}

							// DocuGUARD overrides Bid/List Only membership
							foundBidOnlyMembership = true;
							clearCurrentFee(FeeClass.BidOnly, feeDAO);
							foundListOnlyMembership = true;
							clearCurrentFee(FeeClass.ListOnly, feeDAO);
						} else if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.AuditGUARD)
								&& !foundAuditGUARDMembership) {
							foundAuditGUARDMembership = true;

							if (invoiceItem.getInvoiceFee().isLegacyMembership()) {
								InvoiceFee fee = feeDAO.findMembershipByLegacyAuditGUARDID(FeeClass.AuditGUARD,
										invoiceItem.getInvoiceFee());
								BigDecimal currentAmount = FeeClass.AuditGUARD.getAdjustedFeeAmountIfNecessary(this,
										fee);
								setCurrentFee(fee, currentAmount);
							} else {
								BigDecimal currentAmount = FeeClass.AuditGUARD.getAdjustedFeeAmountIfNecessary(this,
										invoiceItem.getInvoiceFee());
								setCurrentFee(invoiceItem.getInvoiceFee(), currentAmount);
							}

							// Old AuditGUARD included DocuGUARD fee & InsureGUARD fee
							// For legacy compliance
							if (invoiceItem.getInvoiceFee().isLegacyMembership()) {
								foundDocuGUARDMembership = true;
								InvoiceFee newDocuGUARDfee = feeDAO.findMembershipByLegacyAuditGUARDID(
										FeeClass.DocuGUARD, invoiceItem.getInvoiceFee());
								setCurrentFee(newDocuGUARDfee, newDocuGUARDfee.getAmount());

								foundInsureGUARDMembership = true;
								InvoiceFee newInsureGUARDfee = feeDAO.findMembershipByLegacyAuditGUARDID(
										FeeClass.InsureGUARD, invoiceItem.getInvoiceFee());
								setCurrentFee(newInsureGUARDfee, newInsureGUARDfee.getAmount());
							}
						} else if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.InsureGUARD)
								&& !foundInsureGUARDMembership) {
							foundInsureGUARDMembership = true;
							BigDecimal currentAmount = FeeClass.InsureGUARD.getAdjustedFeeAmountIfNecessary(this,
									invoiceItem.getInvoiceFee());
							setCurrentFee(invoiceItem.getInvoiceFee(), currentAmount);
						} else if (invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.EmployeeGUARD)
								&& !foundEmployeeGUARDMembership) {
							foundEmployeeGUARDMembership = true;
							BigDecimal currentAmount = FeeClass.EmployeeGUARD.getAdjustedFeeAmountIfNecessary(this,
									invoiceItem.getInvoiceFee());
							setCurrentFee(invoiceItem.getInvoiceFee(), currentAmount);
						}

						if (!foundPaymentExpires && invoiceItem.getPaymentExpires() != null) {
							paymentExpires = invoiceItem.getPaymentExpires();
							foundPaymentExpires = true;
						}
					}
					if (!foundMembershipDate
							&& (invoiceItem.getInvoiceFee().isActivation() || invoiceItem.getInvoiceFee()
									.isReactivation())) {
						if (invoiceItem.getPaymentExpires() != null)
							membershipDate = invoiceItem.getPaymentExpires();
						else
							membershipDate = invoice.getCreationDate();
						foundMembershipDate = true;
					}
					// Checking for ImportPQF fee and potentially others
					if (!foundImportPQFFee && invoiceItem.getInvoiceFee().getFeeClass().equals(FeeClass.ImportFee)
							&& getFees().containsKey(FeeClass.ImportFee)) {
						InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);
						foundImportPQFFee = true;
						setCurrentFee(fee, fee.getAmount());
					}
				}

				if (foundPaymentExpires) {
					foundMembership = true;
				}
			}
		}

		if (!foundListOnlyMembership)
			clearCurrentFee(FeeClass.ListOnly, feeDAO);
		if (!foundBidOnlyMembership)
			clearCurrentFee(FeeClass.BidOnly, feeDAO);
		if (!foundDocuGUARDMembership)
			clearCurrentFee(FeeClass.DocuGUARD, feeDAO);
		if (!foundAuditGUARDMembership)
			clearCurrentFee(FeeClass.AuditGUARD, feeDAO);
		if (!foundInsureGUARDMembership)
			clearCurrentFee(FeeClass.InsureGUARD, feeDAO);
		if (!foundEmployeeGUARDMembership)
			clearCurrentFee(FeeClass.EmployeeGUARD, feeDAO);
		if (getFees().containsKey(FeeClass.ImportFee) && !foundImportPQFFee)
			clearCurrentFee(FeeClass.ImportFee, feeDAO);
		if (!foundPaymentExpires)
			paymentExpires = creationDate;
		if (!foundMembershipDate)
			membershipDate = null;
	}

	private void clearCurrentFee(FeeClass feeClass, InvoiceFeeDAO feeDAO) {
		this.getFees().get(feeClass).setCurrentLevel(feeDAO.findByNumberOfOperatorsAndClass(feeClass, 0));
		this.getFees().get(feeClass).setCurrentAmount(BigDecimal.ZERO);
	}

	private void setCurrentFee(InvoiceFee fee, BigDecimal amount) {
		this.getFees().get(fee.getFeeClass()).setCurrentLevel(fee);
		this.getFees().get(fee.getFeeClass()).setCurrentAmount(amount);
	}

	@Transient
	public void clearNewFee(FeeClass feeClass, InvoiceFeeDAO feeDAO) {
		this.getFees().get(feeClass).setNewLevel(feeDAO.findByNumberOfOperatorsAndClass(feeClass, 0));
		this.getFees().get(feeClass).setNewAmount(BigDecimal.ZERO);
	}

	@Transient
	public void setNewFee(InvoiceFee fee, BigDecimal amount) {
		this.getFees().get(fee.getFeeClass()).setNewLevel(fee);
		this.getFees().get(fee.getFeeClass()).setNewAmount(amount);
	}

	@Transient
	public boolean isHasMembershipChanged() {
		for (FeeClass feeClass : this.getFees().keySet()) {
			if (this.getFees().get(feeClass).isHasChanged())
				return true;
		}

		return false;
	}

	@Transient
	public boolean isHasUpgrade() {
		for (FeeClass feeClass : this.getFees().keySet()) {
			if (this.getFees().get(feeClass).isUpgrade())
				return true;
		}

		return false;
	}

	@Transient
	public boolean isHasFreeMembership() {
		return this.getPayingFacilities() == 0 || !this.isMustPayB();
	}

	/**
	 * con.getFees().get(FeeClass.DocuGUARD).getNewLevel(); con.getFees().getDocuGUARD().getNewLevel();
	 * 
	 * @return
	 */
	@OneToMany(mappedBy = "contractor", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@MapKey(name = "feeClass")
	@Sort(type = SortType.NATURAL)
	public Map<FeeClass, ContractorFee> getFees() {
		return fees;
	}

	public void setFees(Map<FeeClass, ContractorFee> fees) {
		this.fees = fees;
	}

	@OneToMany(mappedBy = "account", targetEntity = Transaction.class)
	@Where(clause = "tableType='I'")
	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	@OneToMany(mappedBy = "account", targetEntity = Transaction.class)
	@Where(clause = "tableType='P'")
	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	@OneToMany(mappedBy = "account", targetEntity = Transaction.class)
	@Where(clause = "tableType='R'")
	public List<Refund> getRefunds() {
		return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
		this.refunds = refunds;
	}

	@OneToMany(mappedBy = "contractor", cascade = { CascadeType.ALL })
	public Set<FlagCriteriaContractor> getFlagCriteria() {
		return flagCriteria;
	}

	public void setFlagCriteria(Set<FlagCriteriaContractor> flagCriteria) {
		this.flagCriteria = flagCriteria;
	}

	@OneToMany(mappedBy = "contractor", cascade = { CascadeType.ALL })
	public Set<FlagDataOverride> getFlagDataOverrides() {
		return flagDataOverrides;
	}

	public void setFlagDataOverrides(Set<FlagDataOverride> flagDataOverrides) {
		this.flagDataOverrides = flagDataOverrides;
	}

	/**
	 * 
	 * @return a list of invoices sorted by creationDate DESC
	 */
	@Transient
	public List<Invoice> getSortedInvoices() {
		List<Invoice> sortedInvoiceList = new ArrayList<Invoice>(getInvoices());
		Collections.sort(sortedInvoiceList, new Comparator<Invoice>() {

			@Override
			public int compare(Invoice invoiceOne, Invoice invoiceTwo) {

				return invoiceTwo.getCreationDate().compareTo(invoiceOne.getCreationDate());
			}
		});
		return sortedInvoiceList;
	}

	/**
	 * The following are states of Billing Status: Membership Canceled Contractor is not active and membership is not
	 * set to renew:<br />
	 * <br>
	 * <b>Current</b> means the contractor doesn't owe anything right now<br>
	 * <b>Activation</b> means the contractor is not active and has never been active<br>
	 * <b>Reactivation</b> means the contractor was active, but is no longer active anymore<br>
	 * <b>Upgrade</b> The number of facilities a contractor is at has increased.<br>
	 * <b>Do not renew</b> means the contractor has asked not to renew their account<br>
	 * <b>Membership Canceled</b> means the contractor closed their account and doesn't want to renew<br>
	 * <b>Renewal Overdue</b> Contractor is active and the Membership Expiration Date is past.<br>
	 * <b>Renewal</b> Contractor is active and the Membership Expiration Date is in the next 30 Days<br>
	 * <b>Not Calculated</b> New Membership level is null<br>
	 * <b>Past Due</b> Inovice is open and not paid by due date
	 * 
	 * @return A String of the current Billing Status
	 */
	@Transient
	public String getBillingStatus() {
		// If contractor is Free, Deleted, or Demo, give a pass on billing
		if (!isMustPayB() || this.getPayingFacilities() == 0 || status.isDemo() || status.isDeleted())
			return "Current";

		int daysUntilRenewal = (paymentExpires == null) ? 0 : DateBean.getDateDifference(paymentExpires);

		if (status.isPending() && membershipDate == null) {
			if (getAccountLevel().isFull())
				return "Activation";
			else
				return "Renewal";
		}

		if (status.isDeactivated() || daysUntilRenewal < -90) {
			// this contractor is not active or their membership expired more
			// than 90 days ago
			if (!renew)
				return "Membership Canceled";
			else
				return "Reactivation";
		}

		// if any non-bid or list membership level differs, amount is an upgrade
		boolean upgrade = false;
		boolean currentListOrBidOnly = false;
		for (FeeClass feeClass : getFees().keySet()) {
			if (!upgrade && this.getFees().get(feeClass).isUpgrade() && !feeClass.equals(FeeClass.BidOnly)
					&& !feeClass.equals(FeeClass.ListOnly))
				upgrade = true;
			if ((this.getFees().get(feeClass).getCurrentLevel().isBidonly() || this.getFees().get(feeClass)
					.getCurrentLevel().isListonly())
					&& !this.getFees().get(feeClass).getCurrentLevel().isFree())
				currentListOrBidOnly = true;
		}

		if (upgrade) {
			if (currentListOrBidOnly)
				return "Renewal";
			else
				return "Upgrade";
		}

		if (daysUntilRenewal < 0)
			return "Renewal Overdue";
		if (daysUntilRenewal < 45)
			return "Renewal";

		if (hasPastDueInvoice())
			return "Past Due";

		return "Current";
	}

	@Transient
	public Boolean hasPastDueInvoice() {
		for (Invoice in : invoices) {
			if (in.getStatus().equals(TransactionStatus.Unpaid)) {
				if (in.getDueDate().before(new Date())) {
					return true;
				}
			}
		}
		return false;
	}

	@Transient
	public boolean hasAuditWithOnlyInvisibleCaos() {
		for (ContractorAudit audit : this.audits) {
			if (audit.hasOnlyInvisibleCaos())
				return true;
		}
		return false;
	}

	@Transient
	public Set<String> getCountries() {
		Set<String> countries = new HashSet<String>();
		for (ContractorOperator co : getNonCorporateOperators()) {
			try {
				countries.add(co.getOperatorAccount().getCountry().getIsoCode());
			} catch (Exception justIgnoreThisOperator) {
			}
		}
		return countries;
	}

	@Transient
	public List<User> getUsersByRole(OpPerms opPerms) {
		List<User> users = new ArrayList<User>();
		for (User user : getUsers()) {
			// TJA - not sure how null users are getting into the list but on
			// registration it happens
			if (user != null && user.isActiveB()) {
				for (UserAccess userAccess : user.getOwnedPermissions()) {
					if (userAccess.getOpPerm().equals(opPerms)) {
						users.add(user);
					}
				}
			}
		}
		return users;
	}

	@Transient
	public CreditCard getCreditCard() {
		CreditCard cc = null;
		BrainTreeService ccService = new BrainTreeService();
		AppPropertyDAO appPropDao = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");

		ccService.setCanadaProcessorID(appPropDao.find("brainTree.processor_id.canada").getValue());
		ccService.setUsProcessorID(appPropDao.find("brainTree.processor_id.us").getValue());
		ccService.setUserName(appPropDao.find("brainTree.username").getValue());
		ccService.setPassword(appPropDao.find("brainTree.password").getValue());

		// Accounting for transmission errors which result in
		// exceptions being thrown.
		boolean transmissionError = true;
		int retries = 0, quit = 5;
		while (transmissionError && retries < quit) {
			try {
				cc = ccService.getCreditCard(getId());
				transmissionError = false;
			} catch (Exception communicationProblem) {
				// a message or packet could have been dropped in transmission
				// wait and resume retrying
				retries++;
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}
			}
		}

		return cc;
	}

	@Transient
	public String getCcNumber() {
		String cardNumber = getCreditCard().getCardNumber();
		return cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
	}

	@Transient
	public boolean isAgreementInEffect() {
		if (agreementDate == null)
			return false;

		return agreementDate.after(USER_AGREEMENT_CHANGED);
	}

	@Transient
	public boolean isAgreed() {
		return (agreementDate != null);
	}

	@Transient
	public OperatorAccount getReducedActivationFeeOperator(InvoiceFee activation) {
		// if Operator activation fee is reduced, return Operator account
		if (getRequestedBy() != null) {
			if (getRequestedBy().getActivationFee() != null
					&& !getRequestedBy().getActivationFee().equals(activation.getAmount().intValue()))
				return getRequestedBy();

			// if Corporate activation fee is reduced, return Corporate account
			for (Facility f : getRequestedBy().getCorporateFacilities())
				if (f.getCorporate().getActivationFee() != null
						&& !f.getCorporate().getActivationFee().equals(activation.getAmount().intValue()))
					return f.getCorporate();
		}
		return null;
	}

	@Transient
	public boolean hasReducedActivation(InvoiceFee activation) {
		return getReducedActivationFeeOperator(activation) != null
				&& activation.getAmount().intValue() != getReducedActivationFeeOperator(activation).getActivationFee();
	}

	@Transient
	public boolean isFinanciallyReadyForAudits() {
		double halfMembership = 0.0;
		for (FeeClass feeClass : this.getFees().keySet()) {
			if (!this.getFees().get(feeClass).getCurrentLevel().isFree() && feeClass.isMembership())
				halfMembership += this.getFees().get(feeClass).getCurrentAmount().doubleValue();
		}
		halfMembership *= 0.5;

		double balance = getBalance().doubleValue();
		return balance < halfMembership;
	}

	public boolean getSoleProprietor() {
		return soleProprietor;
	}

	public void setSoleProprietor(boolean soleProprietor) {
		this.soleProprietor = soleProprietor;
	}

	public void setCompetitorMembership(Boolean competitorMembership) {
		this.competitorMembership = competitorMembership;
	}

	public Boolean getCompetitorMembership() {
		return competitorMembership;
	}

	public boolean isShowInDirectory() {
		return showInDirectory;
	}

	public void setShowInDirectory(boolean showInDirectory) {
		this.showInDirectory = showInDirectory;
	}

	@Enumerated(EnumType.STRING)
	public AccountLevel getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(AccountLevel accountLevel) {
		this.accountLevel = accountLevel;
	}

	@Transient
	public BigDecimal getNewMembershipAmount() {
		BigDecimal newTotal = BigDecimal.ZERO;
		for (ContractorFee fee : fees.values()) {
			if (fee.getFeeClass().isMembership())
				newTotal = newTotal.add(fee.getNewAmount());
		}

		return newTotal;
	}

	@Transient
	public BigDecimal getCurrentMembershipAmount() {
		BigDecimal currentTotal = BigDecimal.ZERO;
		for (ContractorFee fee : fees.values()) {
			if (fee.getFeeClass().isMembership())
				currentTotal = currentTotal.add(fee.getCurrentAmount());
		}

		return currentTotal;
	}

	@Transient
	public boolean isListOnlyEligible() {
		// Low Risk Material Supplier Only
		if (isMaterialSupplierOnly() && getProductRisk().equals(LowMedHigh.Low))
			return true;
		// Low Safety Risk Offsite Services
		else if (isOffsiteServices() && !isOnsiteServices() && getSafetyRisk().equals(LowMedHigh.Low))
			return true;
		// Sole Proprietors which are Offsite or Material Supplier
		else if (getSoleProprietor() && !isOnsiteServices() && (isOffsiteServices() || isMaterialSupplier()))
			return true;

		return false;
	}

	public boolean willExpireSoon(int daysBeforeExpiration) {
		int daysToExpire = DateBean.getDateDifference(paymentExpires);

		return (daysToExpire <= daysBeforeExpiration);
	}

	@Transient
	public float getWeightedIndustryAverage() {
		float sum = 0;
		int activitySum = 0;

		if (trades.size() > 0) {
			if (hasSelfPerformedTrades()) {
				for (ContractorTrade t : trades) {
					if (t.isSelfPerformed()) {
						sum += t.getActivityPercent() * t.getTrade().getNaicsTRIRI();
						activitySum += t.getActivityPercent();
					}
				}
			} else {
				for (ContractorTrade t : trades) {
					sum += t.getActivityPercent() * t.getTrade().getNaicsTRIRI();
					activitySum += t.getActivityPercent();
				}
			}
			return sum / activitySum;
		} else
			return 0;
	}

	@Transient
	public boolean hasWiaCriteria() {
		for (FlagCriteriaContractor fcc : flagCriteria) {
			if (OshaRateType.TrirWIA.equals(fcc.getCriteria().getOshaRateType()))
				return true;
		}
		return false;
	}

	@Transient
	public boolean hasWiaCriteria(OshaType ot) {
		for (FlagCriteriaContractor fcc : flagCriteria) {
			if (ot.equals(fcc.getCriteria().getOshaType())
					&& OshaRateType.TrirWIA.equals(fcc.getCriteria().getOshaRateType()))
				return true;
		}
		return false;
	}

	@Transient
	public boolean hasSelfPerformedTrades() {
		for (ContractorTrade t : trades) {
			if (t.isSelfPerformed()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param contractor
	 * @return Null if no unpaid invoices exist
	 */
	@Transient
	public Invoice findLastUnpaidInvoice() {
		for (Invoice invoice : getSortedInvoices()) {
			if (invoice.getStatus().isUnpaid()) {
				return invoice;
			}
		}

		return null;
	}

	@Transient
	public boolean isEligibleForImportPQF() {
		// This list is empty because currently Import PQF is not enabled for any Operators
		List<Integer> importPQFEligibleOperators = new ArrayList<Integer>();

		for (OperatorAccount operator : getOperatorAccounts())
			if (importPQFEligibleOperators.contains(operator.getTopAccount().getId()))
				return true;

		return false;
	}
	
	@Transient
	public boolean isContractorTypeRequired(ContractorType conType) {
		for (OperatorAccount operator : getOperatorAccounts()) {
			Set<ContractorType> operatorTypes = operator.getAccountTypes();
			if (operatorTypes.size() == 1 && operatorTypes.contains(conType)) {
				return true;
			}
		}
		return false;
	}
}
