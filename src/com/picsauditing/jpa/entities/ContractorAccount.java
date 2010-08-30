package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.comparators.ContractorAuditComparator;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_info")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAccount extends Account implements JSONable {

	private String taxId;
	private String mainTrade;
	private String logoFile;
	private String brochureFile;
	private String mustPay = "Yes";
	private OperatorAccount requestedBy;
	private String billingAddress;
	private String billingCity;
	private State billingState;
	private String billingZip;
	private String ccEmail;
	private Date membershipDate;
	private int payingFacilities;
	private User auditor;
	private LowMedHigh riskLevel;
	private Date viewedFacilities;
	private Float emrAverage;
	private Float trirAverage;
	private Float lwcrAverage;
	private PaymentMethod paymentMethod = PaymentMethod.CreditCard;
	private boolean ccOnFile = false;
	private Date ccExpiration;
	private Webcam webcam;

	private Date paymentExpires;
	private boolean renew = true;
	private Date lastUpgradeDate;
	private BigDecimal balance;
	private InvoiceFee membershipLevel;
	private InvoiceFee newMembershipLevel;
	private Date agreementDate;
	private User agreedBy;
	private List<Invoice> invoices = new ArrayList<Invoice>();
	private List<Payment> payments = new ArrayList<Payment>();
	private List<Refund> refunds = new ArrayList<Refund>();
	private Set<FlagCriteriaContractor> flagCriteria = new HashSet<FlagCriteriaContractor>();

	private int needsRecalculation;
	private Date lastRecalculation;
	private String tradesSelf;
	private String tradesSub;

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();
	protected List<Certificate> certificates = new ArrayList<Certificate>();

	// Transient helper methods
	protected OshaOrganizer oshaOrganizer = null;
	protected Map<String, AuditData> emrs = null;

	// Agreement Changed on Release date 6/3/2010
	public static final Date USER_AGREEMENT_CHANGED = DateBean.parseDate("06/03/2010");

	public ContractorAccount() {
		this.type = "Contractor";
	}

	public ContractorAccount(int id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
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
	
	@Transient
	public List<OperatorAccount> getOperatorAccounts() {
		List<OperatorAccount> list = new ArrayList<OperatorAccount>();
		for (ContractorOperator co : operators) {
			if (co.getOperatorAccount().isOperator())
				list.add(co.getOperatorAccount());
		}
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

	@Column(name = "taxID", length = 100)
	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	@Column(name = "main_trade", length = 100)
	public String getMainTrade() {
		return this.mainTrade;
	}

	public void setMainTrade(String mainTrade) {
		this.mainTrade = mainTrade;
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

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "riskLevel")
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Column(name = "emrAverage")
	public Float getEmrAverage() {
		return emrAverage;
	}

	public void setEmrAverage(Float emrAverage) {
		this.emrAverage = emrAverage;
	}

	@Column(name = "trirAverage")
	public Float getTrirAverage() {
		return trirAverage;
	}

	public void setTrirAverage(Float trirAverage) {
		this.trirAverage = trirAverage;
	}

	@Column(name = "lwcrAverage")
	public Float getLwcrAverage() {
		return lwcrAverage;
	}

	public void setLwcrAverage(Float lwcrAverage) {
		this.lwcrAverage = lwcrAverage;
	}

	// //// BILLING/ACCOUNT - related columns //////
	/**
	 * Determines if this contractor must pay or not. It allows for PICS to
	 * grant "free" lifetime accounts to certain contractors. Yes or No
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
	 * The date the contractor was invoiced for their most recent
	 * activation/reactivation fee
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "membershipDate", length = 10)
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
	 * The date the lastPayment expires and the contractor is due to pay another
	 * "period's" membership fee. This should NEVER be null.
	 * 
	 * UPDATE contractor_info, accounts SET paymentExpires = creationDate WHERE
	 * (paymentExpires = '0000-00-00' or paymentExpires is null) AND
	 * contractor_info.id = accounts.id;
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "paymentExpires", nullable = false)
	public Date getPaymentExpires() {
		return this.paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	/**
	 * Used to determine if we need to calculate the flagColor, audits and
	 * billing
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
		if (increment > 0 && this.needsRecalculation < 128)
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
			oshaOrganizer = new OshaOrganizer(getSortedAudits());
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
		for (ContractorAudit audit : getSortedAudits()) {
			if (number < 4) {
				// Store the EMR rates into a map for later use
				for (AuditData answer : audit.getData()) {
					if (answer.getQuestion().getId() == AuditQuestion.EMR
							|| (answer.getQuestion().getId() == 2033 && "No".equals(answer.getAnswer()))) {
						if (!Strings.isEmpty(answer.getAnswer())) {
							number++;
							if (answer.getQuestion().getId() == 2033)
								emrs.put(audit.getAuditFor(), null);
							else
								emrs.put(audit.getAuditFor(), answer);
						}
					}
				}
			}
		}

		AuditData result = trim(new ArrayList<AuditData>(emrs.values()));
		if (result != null)
			emrs.remove(result.getAudit().getAuditFor());

		AuditData avg = AuditData.addAverageData(emrs.values());
		if (avg != null && !Strings.isEmpty(avg.getAnswer()))
			emrs.put(OshaAudit.AVG, avg);

		return emrs;
	}

	private AuditData trim(List<AuditData> list) {
		if (list.size() < 4)
			return null;
		if (list.size() > 4)
			throw new RuntimeException("Found [" + list.size() + "] EMRs");

		// We have 4 years worth of data, get rid of either the first or the
		// last
		// We trim the fourth year ONLY if it's not verified but all three
		// previous years are.
		if (!list.get(3).isVerified() && list.get(2).isVerified() && list.get(1).isVerified()
				&& list.get(0).isVerified()) {
			PicsLogger.log("removed fourthYear" + list.get(3).getAudit().getAuditFor());
			return list.get(3);
		} else {
			return list.get(0);
		}
	}

	@Transient
	public List<ContractorAudit> getSortedAudits() {
		List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
		for (ContractorAudit contractorAudit : getAudits()) {
			if (contractorAudit.getAuditType().isAnnualAddendum()
					&& (contractorAudit.getAuditStatus().isActiveSubmitted()
							|| contractorAudit.getAuditStatus().isResubmitted() || contractorAudit.getAuditStatus()
							.isIncomplete())) {
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
	 * The last day someone added a facility to this contractor. This is used to
	 * prorate upgrade amounts
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
		boolean foundCurrentMembership = false;
		boolean foundMembershipDate = false;
		boolean foundPaymentExpires = false;

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

		for (Invoice invoice : getSortedInvoices()) {
			if (!invoice.getStatus().isVoid()) {
				for (InvoiceItem invoiceItem : invoice.getItems()) {
					if (invoiceItem.getInvoiceFee().getFeeClass().equals("Membership")) {
						if (!foundCurrentMembership) {
							membershipLevel = invoiceItem.getInvoiceFee();
							foundCurrentMembership = true;
						}
						if (!foundPaymentExpires && invoiceItem.getPaymentExpires() != null) {
							paymentExpires = invoiceItem.getPaymentExpires();
							foundPaymentExpires = true;
						}
					}
					if (!foundMembershipDate && invoiceItem.getInvoiceFee().getFeeClass().equals("Activation")) {
						if (invoiceItem.getPaymentExpires() != null)
							membershipDate = invoiceItem.getPaymentExpires();
						else
							membershipDate = invoice.getCreationDate();
						foundMembershipDate = true;
					}
				}
			}
			if (foundCurrentMembership && foundMembershipDate && foundPaymentExpires)
				return;
		}
		if (!foundCurrentMembership)
			membershipLevel = new InvoiceFee(InvoiceFee.FREE);
		if (!foundPaymentExpires)
			paymentExpires = creationDate;
		if (!foundMembershipDate)
			membershipDate = null;
	}

	@ManyToOne
	@JoinColumn(name = "membershipLevelID", nullable = false)
	public InvoiceFee getMembershipLevel() {
		return membershipLevel;
	}

	public void setMembershipLevel(InvoiceFee membershipLevel) {
		this.membershipLevel = membershipLevel;
	}

	@ManyToOne
	@JoinColumn(name = "newMembershipLevelID")
	public InvoiceFee getNewMembershipLevel() {
		return newMembershipLevel;
	}

	public void setNewMembershipLevel(InvoiceFee newMembershipLevel) {
		this.newMembershipLevel = newMembershipLevel;
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
	 * The following are states of Billing Status: Membership Canceled
	 * Contractor is not active and membership is not set to renew:<br />
	 * <br>
	 * <b>Current</b> means the contractor doesn't owe anything right now<br>
	 * <b>Activation</b> means the contractor is not active and has never been
	 * active<br>
	 * <b>Reactivation</b> means the contractor was active, but is no longer
	 * active anymore<br>
	 * <b>Upgrade</b> The number of facilities a contractor is at has increased.<br>
	 * <b>Do not renew</b> means the contractor has asked not to renew their
	 * account<br>
	 * <b>Membership Canceled</b> means the contractor closed their account and
	 * doesn't want to renew<br>
	 * <b>Renewal Overdue</b> Contractor is active and the Membership Expiration
	 * Date is past.<br>
	 * <b>Renewal</b> Contractor is active and the Membership Expiration Date is
	 * in the next 30 Days<br>
	 * <b>Not Calculated</b> New Membership level is null<br>
	 * <b>Past Due</b> Inovice is open and not paid by due date
	 * 
	 * @return A String of the current Billing Status
	 */
	@Transient
	public String getBillingStatus() {
		if (!isMustPayB())
			return "Current";

		if (status.isDemo())
			return "Current";

		if (status.isDeleted())
			return "Current";

		if (acceptsBids) {
			// Do we want to do this?
			if (status.isActive()) {
				return "Current";
			}
			return "Bid Only Account";
		}

		if (newMembershipLevel == null)
			return "Not Calculated";

		if (newMembershipLevel.isFree())
			return "Current";

		if (status.isPending() && membershipDate == null)
			return "Activation";

		int daysUntilRenewal = (paymentExpires == null) ? 0 : DateBean.getDateDifference(paymentExpires);

		if (status.isDeactivated() || daysUntilRenewal < -90) {
			// this contractor is not active or their membership expired more
			// than 90 days ago
			if (!renew)
				return "Membership Canceled";
			else {
				if (new Date().before(paymentExpires))
					return "Current";

				return "Reactivation";
			}
		}

		if (membershipLevel.getId() == InvoiceFee.BIDONLY) {
			return "Renewal";
		}
		if (newMembershipLevel.getAmount().compareTo(membershipLevel.getAmount()) > 0)
			return "Upgrade";

		if (!renew)
			return "Do not renew";

		if (daysUntilRenewal < 0)
			return "Renewal Overdue";
		if (daysUntilRenewal < 45)
			return "Renewal";
		
		if(hasPastDueInvoice())
			return "Past Due";

		return "Current";
	}
	
	@Transient
	public Boolean hasPastDueInvoice(){
		for(Invoice in : invoices){
			if(in.getStatus().equals(TransactionStatus.Unpaid)){
				if(in.getDueDate().before(new Date())){
					return true;
				}
			}
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
}
