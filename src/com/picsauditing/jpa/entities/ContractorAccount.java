package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import javax.persistence.MapKey;
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

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.util.Strings;
import com.picsauditing.util.comparators.ContractorAuditComparator;

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
	private String secondContact;
	private String secondPhone;
	private String secondEmail;
	private String billingContact;
	private String billingPhone;
	private String billingEmail;
	private String billingAddress;
	private String billingCity;
	private String billingState;
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
	private List<Invoice> invoices = new ArrayList<Invoice>();
	private List<Payment> payments = new ArrayList<Payment>();
	private List<Refund> refunds = new ArrayList<Refund>();

	private boolean needsRecalculation;
	private Date lastRecalculation;

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected Map<OperatorAccount, ContractorOperatorFlag> flags = new HashMap<OperatorAccount, ContractorOperatorFlag>();
	protected List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();
	protected List<Certificate> certificates = new ArrayList<Certificate>();

	// Transient helper methods
	protected Map<OshaType, Map<String, OshaAudit>> oshas = null;
	protected Map<String, AuditData> emrs = null;

	public ContractorAccount() {
		this.type = "Contractor";
	}

	public ContractorAccount(int id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	public List<ContractorAudit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.REMOVE })
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
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

	@Column(name = "secondContact", length = 50)
	public String getSecondContact() {
		return this.secondContact;
	}

	public void setSecondContact(String secondContact) {
		this.secondContact = secondContact;
	}

	@Column(name = "secondPhone", length = 50)
	public String getSecondPhone() {
		return this.secondPhone;
	}

	public void setSecondPhone(String secondPhone) {
		this.secondPhone = secondPhone;
	}

	@Column(name = "secondEmail", length = 50)
	public String getSecondEmail() {
		return this.secondEmail;
	}

	public void setSecondEmail(String secondEmail) {
		this.secondEmail = secondEmail;
	}

	@Column(name = "billingContact", length = 50)
	public String getBillingContact() {
		return this.billingContact;
	}

	public void setBillingContact(String billingContact) {
		this.billingContact = billingContact;
	}

	@Column(name = "billingPhone", length = 50)
	public String getBillingPhone() {
		return this.billingPhone;
	}

	public void setBillingPhone(String billingPhone) {
		this.billingPhone = billingPhone;
	}

	@Column(name = "billingEmail", length = 50)
	public String getBillingEmail() {
		return this.billingEmail;
	}

	public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
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

	@Column(name = "billingState", length = 10)
	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
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
	 * The date the contractor paid their activation/reactivation fee
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
	public boolean isNeedsRecalculation() {
		return needsRecalculation;
	}

	public void setNeedsRecalculation(boolean needsRecalculation) {
		this.needsRecalculation = needsRecalculation;
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

	// Other relationships //

	@ManyToOne
	@JoinColumn(name = "welcomeAuditor_id")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	/**
	 * Map of Contractor Flags with OperatorID as the key
	 */
	@MapKey(name = "operatorAccount")
	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.REMOVE })
	public Map<OperatorAccount, ContractorOperatorFlag> getFlags() {
		return flags;
	}

	public void setFlags(Map<OperatorAccount, ContractorOperatorFlag> flags) {
		this.flags = flags;
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
	/*
	 * Get a double-keyed map, by OshaType and auditFor, for the last 3 years of
	 * applicable osha data (verified or not)
	 */
	public Map<OshaType, Map<String, OshaAudit>> getOshas() {
		if (oshas != null)
			return oshas;
		oshas = new TreeMap<OshaType, Map<String, OshaAudit>>();

		List<ContractorAudit> annualAudits = getSortedAudits();
		oshas.put(OshaType.OSHA, buildOshaMap(annualAudits, OshaType.OSHA, AuditCategory.OSHA_AUDIT));
		oshas.put(OshaType.MSHA, buildOshaMap(annualAudits, OshaType.MSHA, AuditCategory.MSHA));
		oshas.put(OshaType.COHS, buildOshaMap(annualAudits, OshaType.COHS, AuditCategory.CANADIAN_STATISTICS));

		return oshas;
	}

	@Transient
	private Map<String, OshaAudit> buildOshaMap(List<ContractorAudit> annualAudits, OshaType oshaType,
			int auditCategoryID) {
		Map<String, OshaAudit> oshaMap = new TreeMap<String, OshaAudit>();

		int number = 0;
		for (ContractorAudit audit : annualAudits) {
			if (number < 3) {
				for (AuditCatData auditCatData : audit.getCategories()) {
					if (auditCatData.getCategory().getId() == auditCategoryID
							&& auditCatData.getPercentCompleted() == 100) {
						// Store the corporate OSHA rates into a map for later
						// use
						for (OshaAudit osha : audit.getOshas())
							if (osha.getType().equals(oshaType) && osha.isCorporate()) {
								number++;
								oshaMap.put(audit.getAuditFor(), osha);
							}
					}
				}
			}
		}
		int count = oshaMap.size();
		if (count > 0) {
			// Add in the average for the past 3 years
			OshaAudit avg = new OshaAudit();
			avg.setLostWorkCasesRate(0);
			avg.setRecordableTotalRate(0);
			avg.setRestrictedDaysAwayRate(0);

			float manHours = 0;
			float fatalities = 0;
			float injuries = 0;
			float lwc = 0;
			float lwcr = 0;
			float lwd = 0;
			float tri = 0;
			float trir = 0;
			float rwc = 0;
			float dart = 0;
			float neer = 0;
			float cad7 = 0;

			for (String key : oshaMap.keySet()) {
				OshaAudit osha = oshaMap.get(key);
				avg.setFactor(osha.getFactor());
				avg.setConAudit(osha.getConAudit());

				manHours += osha.getManHours();
				fatalities += osha.getFatalities();
				injuries += osha.getInjuryIllnessCases();
				lwc += osha.getLostWorkCases();
				lwcr += osha.getLostWorkCasesRate();
				lwd += osha.getLostWorkDays();
				tri += osha.getRecordableTotal();
				trir += osha.getRecordableTotalRate();
				rwc += osha.getRestrictedWorkCases();
				dart += osha.getRestrictedDaysAwayRate();
				if (osha.getNeer() != null)
					neer += osha.getNeer();
				if (osha.getCad7() != null)
					cad7 += osha.getCad7();
			}
			avg.setManHours(Math.round(manHours / count));
			avg.setFatalities(Math.round(fatalities / count));
			avg.setInjuryIllnessCases(Math.round(injuries / count));
			avg.setLostWorkCases(Math.round(lwc / count));
			avg.setLostWorkCasesRate(lwcr / count);
			avg.setLostWorkDays(Math.round(lwd / count));
			avg.setRecordableTotal(Math.round(tri / count));
			avg.setRecordableTotalRate(trir / count);
			avg.setRestrictedWorkCases(Math.round(rwc / count));
			avg.setRestrictedDaysAwayRate(dart / count);
			avg.setNeer(neer / count);
			avg.setCad7(cad7 / count);

			oshaMap.put(OshaAudit.AVG, avg);
		}

		return oshaMap;
	}

	@Transient
	/*
	 * Get a map of the last 3 years of applicable emr data (verified or not)
	 */
	public Map<String, AuditData> getEmrs() {
		if (emrs != null)
			return emrs;

		emrs = new HashMap<String, AuditData>();
		int number = 0;
		for (ContractorAudit audit : getSortedAudits()) {
			if (number < 3) {
				for (AuditCatData auditCatData : audit.getCategories()) {
					if (auditCatData.getCategory().getId() == AuditCategory.EMR
							&& auditCatData.getPercentCompleted() == 100) {
						// Store the EMR rates into a map for later use
						for (AuditData answer : audit.getData())
							if (answer.getQuestion().getId() == AuditQuestion.EMR) {
								if (answer != null && !Strings.isEmpty(answer.getAnswer())) {
									number++;
									emrs.put(audit.getAuditFor(), answer);
								}
							}
					}
				}
			}
		}

		AuditData avg = AuditData.addAverageData(emrs.values());
		emrs.put(OshaAudit.AVG, avg);

		return emrs;
	}

	@Transient
	public List<ContractorAudit> getSortedAudits() {
		List<ContractorAudit> annualAList = new ArrayList<ContractorAudit>();
		for (ContractorAudit contractorAudit : getAudits()) {
			if (contractorAudit.getAuditType().isAnnualAddendum() && !contractorAudit.getAuditStatus().isExpired()) {
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
	 * 
	 * @return A String of the current Billing Status
	 */
	@Transient
	public String getBillingStatus() {
		if (!isMustPayB())
			return "Current";

		if (acceptsBids) {
			if (isActiveB()) {
				return "Current";
			}
			return "BidOnlyAccount";
		}

		if (newMembershipLevel == null)
			return "Not Calculated";

		if (newMembershipLevel.isFree())
			return "Current";

		int daysUntilRenewal = (paymentExpires == null) ? 0 : DateBean.getDateDifference(paymentExpires);

		if (!isActiveB() || daysUntilRenewal < -90) {
			// this contractor is not active or their membership expired more
			// than 90 days ago
			if (!renew)
				return "Membership Canceled";
			else {
				if (new Date().before(paymentExpires))
					return "Current";

				if (membershipDate == null)
					return "Activation";
				else
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

		return "Current";
	}

	@Transient
	public boolean isOqEmployees(AuditDataDAO auditDataDAO) {
		List<Integer> questions = new ArrayList<Integer>();
		questions.add(AuditQuestion.OQ_EMPLOYEES);
		List<AuditData> auditDataList = auditDataDAO.findAnswerByConQuestions(getId(), questions);
		if (auditDataList != null && auditDataList.size() > 0) {
			AuditData auditData = auditDataList.get(0);
			if (auditData != null && "Yes".equals(auditData.getAnswer()))
				return true;
		}
		return false;
	}

	@Transient
	public boolean isCOR(AuditDataDAO auditDataDAO) {
		List<Integer> questions = new ArrayList<Integer>();
		questions.add(2954);
		List<AuditData> auditDataList = auditDataDAO.findAnswerByConQuestions(getId(), questions);
		if (auditDataList != null && auditDataList.size() > 0) {
			AuditData auditData = auditDataList.get(0);
			if (auditData != null && "Yes".equals(auditData.getAnswer()))
				return true;
		}
		return false;
	}

	@Transient
	public Set<String> getCountries() {
		Set<String> countries = new HashSet<String>();
		for (ContractorOperator co : getOperators()) {
			countries.add(co.getOperatorAccount().getCountry().getIsoCode());
		}
		return countries;
	}
}
