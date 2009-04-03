package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.util.comparators.ContractorAuditComparator;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_info")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAccount extends Account implements java.io.Serializable {

	private String taxId;
	private String mainTrade;
	private String logoFile;
	private String brochureFile;
	private String description;
	private Date accountDate;
	private String mustPay = "Yes";
	private int requestedById;
	// TODO private OperatorAccount requestedBy;
	private String secondContact;
	private String secondPhone;
	private String secondEmail;
	private String billingContact;
	private String billingPhone;
	private String billingEmail;
	private Date membershipDate;
	private int payingFacilities;
	private User auditor;
	private LowMedHigh riskLevel;
	private Date viewedFacilities;
	private boolean ccOnFile = false;
	private PaymentMethod paymentMethod = PaymentMethod.CreditCard;

	private String oqEmployees;

	private Date paymentExpires;
	private boolean renew = true;
	private Date lastUpgradeDate;
	private BigDecimal balance;
	private InvoiceFee membershipLevel;
	private InvoiceFee newMembershipLevel;
	private List<Invoice> invoices = new ArrayList<Invoice>();

	private boolean needsRecalculation;
	private Date lastRecalculation;

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected Map<OperatorAccount, ContractorOperatorFlag> flags = new HashMap<OperatorAccount, ContractorOperatorFlag>();
	protected List<ContractorTag> operatorTags = new ArrayList<ContractorTag>();

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

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getDescriptionHTML() {
		return Utilities.escapeNewLines(this.description);
	}

	@Column(name = "requestedByID")
	public int getRequestedById() {
		return this.requestedById;
	}

	public void setRequestedById(int requestedById) {
		this.requestedById = requestedById;
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

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "riskLevel", nullable = false)
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Column(name = "oqEmployees")
	public String getOqEmployees() {
		return oqEmployees;
	}

	public void setOqEmployees(String oqEmployees) {
		this.oqEmployees = oqEmployees;
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

	@Column(name = "payingFacilities", nullable = false)
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
			if (ccOnFile)
				return true;
			return false;
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
	 * The date this is first activated. This is never reset afterwards.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "accountDate", length = 10)
	public Date getAccountDate() {
		return this.accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
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
			if (!invoice.isPaid() && invoice.getDueDate() != null && invoice.getDueDate().before(new Date()))
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
		int number = 0;
		oshas = new TreeMap<OshaType, Map<String, OshaAudit>>();

		for (ContractorAudit audit : getSortedAudits()) {
			if (number < 3) {
				for (AuditCatData auditCatData : audit.getCategories()) {
					if (auditCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT
							&& auditCatData.getPercentCompleted() == 100) {
						// Store the corporate OSHA rates into a map for later
						// use
						for (OshaAudit osha : audit.getOshas())
							if (osha.getType().equals(OshaType.OSHA) && osha.isCorporate() && osha.isApplicable()) {
								number++;
								Map<String, OshaAudit> theMap = oshas.get(osha.getType());

								if (theMap == null) {
									theMap = new TreeMap<String, OshaAudit>();
									oshas.put(osha.getType(), theMap);
								}

								theMap.put(audit.getAuditFor(), osha);

							}
					}
				}
			}
		}
		for (OshaType oshaType : oshas.keySet()) {

			Map<String, OshaAudit> theseOshas = oshas.get(oshaType);

			int count = theseOshas.size();
			if (count > 0) {
				// Add in the average for the past 3 years
				OshaAudit avg = new OshaAudit();
				avg.setLostWorkCasesRate(0);
				avg.setRecordableTotalRate(0);

				float manHours = 0;
				float fatalities = 0;
				float injuries = 0;
				float lwc = 0;
				float lwcr = 0;
				float lwd = 0;
				float tri = 0;
				float trir = 0;
				float rwc = 0;

				for (String key : theseOshas.keySet()) {
					OshaAudit osha = theseOshas.get(key);
					avg.setFactor(osha.getFactor());
					avg.setApplicable(true);
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

				theseOshas.put(OshaAudit.AVG, avg);
			}
		}
		return oshas;
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
								number++;
								emrs.put(audit.getAuditFor(), answer);
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

		balance = BigDecimal.ZERO;
		for (Invoice invoice : getInvoices()) {
			if (!invoice.isPaid())
				balance = balance.add(invoice.getTotalAmount());
		}
		
		for (Invoice invoice : getSortedInvoices()) {
			if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
				for (InvoiceItem invoiceItem : invoice.getItems()) {
					if (!invoiceItem.isRefunded()) {
						if (!foundCurrentMembership && invoiceItem.getInvoiceFee().getFeeClass().equals("Membership")) {
							paymentExpires = invoiceItem.getPaymentExpires();
							membershipLevel = invoiceItem.getInvoiceFee();
							foundCurrentMembership = true;
						}
						if (!foundMembershipDate && invoiceItem.getInvoiceFee().getFeeClass().equals("Activation")) {
							membershipDate = invoice.getCreationDate();
							foundMembershipDate = true;
						}
					}
				}
				if (foundCurrentMembership && foundMembershipDate)
					return;
			}
		}
		if (!foundCurrentMembership) {
			paymentExpires = creationDate;
			membershipLevel = new InvoiceFee(InvoiceFee.FREE);
		}
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

	@OneToMany(mappedBy = "account")
	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

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

		if (newMembershipLevel == null)
			return "Not Calculated";

		if (newMembershipLevel.isFree())
			return "Current";

		if (!isActiveB()) {
			if (!renew)
				return "Membership Canceled";
			else {
				if (membershipDate == null)
					return "Activation";
				else
					return "Reactivation";
			}
		}

		if (newMembershipLevel.getAmount().compareTo(membershipLevel.getAmount()) > 0)
			return "Upgrade";

		if (!renew)
			return "Do not renew";

		int daysUntilRenewal = DateBean.getDateDifference(paymentExpires);
		if (daysUntilRenewal < 0)
			return "Renewal Overdue";
		if (daysUntilRenewal < 45)
			return "Renewal";

		return "Current";
	}
}
