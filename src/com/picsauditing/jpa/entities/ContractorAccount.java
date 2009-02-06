package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;
import com.picsauditing.util.comparators.ContractorAuditComparator;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_info")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAccount extends Account implements java.io.Serializable {

	private String taxId;
	private String mainTrade;
	private String trades;
	private String subTrades;
	private String logoFile;
	private String brochureFile;
	private String description;
	private Date accountDate;
	private String paid;
	private Date lastPayment;
	private int lastPaymentAmount;
	private Date lastInvoiceDate;
	private String notes;
	private String adminNotes;
	private String mustPay = "Yes";
	private Date paymentExpires;
	private Date lastAnnualUpdateEmailDate;
	private int requestedById;
	private int billingAmount;
	private String secondContact;
	private String secondPhone;
	private String secondEmail;
	private String billingContact;
	private String billingPhone;
	private String billingEmail;
	private Date membershipDate;
	private int newBillingAmount;
	private int payingFacilities;
	private User auditor;
	private LowMedHigh riskLevel;
	private Date viewedFacilities;
	private String paymentMethodStatus;
	private String paymentMethod = "Credit Card";

	private int annualUpdateEmails;
	private String oqEmployees;
	
	private boolean renew;
	private Date lastUpgradeDate;
	private int balance;
	private InvoiceFee membershipLevel;
	private InvoiceFee newMembershipLevel;
	private List<Invoice> invoices = new ArrayList<Invoice>();

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected Map<OperatorAccount, ContractorOperatorFlag> flags = new HashMap<OperatorAccount, ContractorOperatorFlag>();

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

	@Column(length = 65535)
	public String getTrades() {
		return this.trades;
	}

	public void setTrades(String trades) {
		this.trades = trades;
	}

	@Column(length = 65535)
	public String getSubTrades() {
		return this.subTrades;
	}

	public void setSubTrades(String subTrades) {
		this.subTrades = subTrades;
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

	@Column(name = "notes", length = 16277215)
	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Append a new note to this contractor in the form of: M/d/yy h:mm a z User
	 * Name: NOTE HERE previous notes
	 * 
	 * @param username
	 * @param note
	 */
	public void addNote(Permissions permissions, String note) {
		String userName = "SYSTEM";
		if (permissions != null)
			userName = permissions.getName();
		this.notes = DateBean.getTodaysDateTime() + " " + userName + ": " + note + "\n" + this.notes;
	}

	@Column(name = "adminNotes", length = 16277215)
	public String getAdminNotes() {
		return this.adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAnnualUpdateEmailDate", length = 10)
	public Date getLastAnnualUpdateEmailDate() {
		return this.lastAnnualUpdateEmailDate;
	}

	public void setLastAnnualUpdateEmailDate(Date lastAnnualUpdateEmailDate) {
		this.lastAnnualUpdateEmailDate = lastAnnualUpdateEmailDate;
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

	@Column(name = "annualUpdateEmails")
	public int getAnnualUpdateEmails() {
		return annualUpdateEmails;
	}

	public void setAnnualUpdateEmails(int annualUpdateEmails) {
		this.annualUpdateEmails = annualUpdateEmails;
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
	 * Determines if this contractor must pay or not. It allows for PICS to
	 * grant "free" lifetime accounts to certain contractors
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

	/**
	 * The Payment Method Statuses are Valid, Invalid, Missing, Pending,
	 * Approved, Rejected
	 * 
	 * @return
	 */
	public String getPaymentMethodStatus() {
		return paymentMethodStatus;
	}

	/**
	 * The Payment Method Statuses are Valid, Invalid, Missing, Pending,
	 * Approved, Rejected
	 * 
	 * @param paymentMethodStatus
	 */
	public void setPaymentMethodStatus(String paymentMethodStatus) {
		this.paymentMethodStatus = paymentMethodStatus;
	}

	@Transient
	public boolean isPaymentMethodStatusValid() {
		if (!Strings.isEmpty(paymentMethod) && "Check".equals(paymentMethod))
			return true;
		if (!Strings.isEmpty(paymentMethodStatus)) {
			if ("Valid".equals(paymentMethodStatus) || "Approved".equals(paymentMethodStatus))
				return true;
		}
		return false;
	}

	/**
	 * The Payment methods are Credit Card and Check
	 * 
	 * @return
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Transient
	public boolean isPaymentMethodCreditCard() {
		if (!Strings.isEmpty(paymentMethod) && "Credit Card".equals(paymentMethod))
			return true;
		return false;
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

	/**
	 * The last Invoiced amount
	 * 
	 * @return
	 */
	@Column(name = "billingAmount", nullable = false)
	public int getBillingAmount() {
		return this.billingAmount;
	}

	public void setBillingAmount(int billingAmount) {
		this.billingAmount = billingAmount;
	}

	/**
	 * The date we last sent an invoice to the contractor
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "lastInvoiceDate", length = 10)
	public Date getLastInvoiceDate() {
		return this.lastInvoiceDate;
	}

	public void setLastInvoiceDate(Date lastInvoiceDate) {
		this.lastInvoiceDate = lastInvoiceDate;
	}

	// Payment Billing info
	/**
	 * This is not used anymore: Yes/No
	 * 
	 * @return
	 */
	@Column(name = "paid", nullable = true, length = 3)
	public String getPaid() {
		return this.paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	/**
	 * The date the contractor last paid their account in full
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "lastPayment", length = 10)
	public Date getLastPayment() {
		return this.lastPayment;
	}

	public void setLastPayment(Date lastPayment) {
		this.lastPayment = lastPayment;
	}

	/**
	 * The USD amount they paid on lastPayment date
	 * 
	 * @return
	 */
	@Column(name = "lastPaymentAmount")
	public int getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}

	public void setLastPaymentAmount(int lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	/**
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
	 * "period's" membership fee
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "paymentExpires", length = 10)
	public Date getPaymentExpires() {
		return this.paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	/**
	 * The annual membership fee for this contractor
	 * 
	 * @return
	 */
	@Column(name = "newBillingAmount", nullable = false)
	public int getNewBillingAmount() {
		return this.newBillingAmount;
	}

	public void setNewBillingAmount(int newBillingAmount) {
		this.newBillingAmount = newBillingAmount;
	}

	@Transient
	public int getActivationFee() {
		if (newBillingAmount == 0)
			return 0;
		if (lastPayment == null)
			return 99;
		else
			return 199;
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
	public int getUpgradeAmountOwed() {
		if (!isActiveB())
			// Inactive contractor must renew their account, they can't just
			// upgrade
			return 0;
		if (!isMustPayB())
			return 0;
		if (paymentExpires != null && lastPayment != null && lastInvoiceDate != null) {
			// TODO we really shouldn't have to check for nulls, we should fix
			// the root cause instead
			if (lastInvoiceDate.before(lastPayment) || lastInvoiceDate.equals(lastPayment))
				// already paid the invoice
				return 0;
			if (DateBean.getDateDifference(lastInvoiceDate, paymentExpires) < 75)
				// This is an invoice for annual payment
				return 0;
		}
		// Removed this 10/27/08 because billingAmount actually reflect the
		// difference, not the total amount required
		if (billingAmount < lastPaymentAmount)
			// they already overpaid (probably garbage data)
			return 0;

		// return newBillingAmount - lastPaymentAmount;
		// Examples:
		// $699 - $399
		// $1197 - $225 in cases of 3 year billing cycles
		return billingAmount - lastPaymentAmount;
	}

	@Transient
	public int getAnnualAmountOwed() {
		if (!isMustPayB())
			return 0;
		if (isActiveB() && paymentExpires != null && lastPayment != null && lastInvoiceDate != null) {
			if (lastInvoiceDate.before(lastPayment) || lastInvoiceDate.equals(lastPayment))
				// already paid the invoice
				return 0;
			if (DateBean.getDateDifference(lastInvoiceDate, paymentExpires) >= 75)
				// This is an invoice for upgrade payment
				return 0;
		}
		return billingAmount;
	}

	@Transient
	public boolean isPaymentOverdue() {
		if (getUpgradeAmountOwed() > 0 || getAnnualAmountOwed() > 0)
			if (lastInvoiceDate != null && lastInvoiceDate.before(new Date()))
				return true;
		return false;
	}

	@Transient
	/**
	 * Get a double-keyed map, by OshaType and auditFor, for the last 3 years of
	 * applicable osha data (verified or not)
	 */
	public Map<OshaType, Map<String, OshaAudit>> getOshas() {
		if (oshas != null)
			return oshas;
		int number = 0;
		oshas = new TreeMap<OshaType, Map<String, OshaAudit>>();

		for (ContractorAudit audit : getSortedAudits()) {
			if (number < 3 && audit.getAuditType().getId() == AuditType.ANNUALADDENDUM 
					&& !audit.getAuditStatus().isExpired()) {
				for (AuditCatData auditCatData : audit.getCategories()) {
					if (auditCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT 
							&& auditCatData.getPercentCompleted() == 100) {
						// Store the corporate OSHA rates into a map for later
						// use
						for (OshaAudit osha : audit.getOshas())
							if (osha.getType().equals(OshaType.OSHA) 
								&& osha.isCorporate() && osha.isApplicable()) {
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
	/**
	 * Get a map of the last 3 years of applicable emr data (verified or not)
	 */
	public Map<String, AuditData> getEmrs() {
		if (emrs != null)
			return emrs;

		emrs = new HashMap<String, AuditData>();
		int number = 0;
		for (ContractorAudit audit : getSortedAudits()) {
			if (number < 3 && audit.getAuditType().getId() == AuditType.ANNUALADDENDUM) {
				for (AuditCatData auditCatData : audit.getCategories()) {
					if (auditCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT 
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
		Collections.sort(getAudits(), new ContractorAuditComparator("auditFor -1"));
		return getAudits();
	}

	public boolean isRenew() {
		return renew;
	}

	public void setRenew(boolean renew) {
		this.renew = renew;
	}

	@Temporal(TemporalType.DATE)
	public Date getLastUpgradeDate() {
		return lastUpgradeDate;
	}

	public void setLastUpgradeDate(Date lastUpgradeDate) {
		this.lastUpgradeDate = lastUpgradeDate;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	@ManyToOne
	@JoinColumn(name = "membershipLevelID")
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
	/**
	 * The following are states of Billing Status:
	 *     Membership Canceled
	 *         Contractor is not active and membership is not set to renew.
	 *     
	 *     Activation
	 *         Contractor is not active and Contractor does not have a membership
	 *         
	 *     Reactivation
	 *         Contractor is not active and Contractor's membership expired
	 *         
	 *     Upgrade
	 *         The number of facilities a contractor is at has increased.
	 *     
	 *     Do Not Renew
	 *         Contractor's Membership is not set to renew.
	 *         
	 *     Renewal Overdue
	 *         Contractor is active and the Membership Expiration Date is past.
	 *         
	 *     Renewal
	 *         Contractor is active and the Membership Expiration Date is in the next 30 Days
	 * 
	 * @return A String of the current Billing Status
	 */
	public String getBillingStatus() {
		// TODO: still need to determine if this is a reactivation or initial activation
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
		if (newMembershipLevel.getAmount() > membershipLevel.getAmount())
			return "Upgrade";

		if (!renew)
			return "Do not renew";
		
		int daysUntilRenewal = DateBean.getDateDifference(paymentExpires);
		if (daysUntilRenewal < 0)
			return "Renewal Overdue";
		if (daysUntilRenewal < 30)
			return "Renewal";
		
		return "Current";
	}
}
