package com.picsauditing.jpa.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.OrderBy;
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
	private char accountNewComplete;
	private String notes;
	private String adminNotes;
	private String mustPay;
	private Date paymentExpires;
	private Date lastAnnualUpdateEmailDate;
	private int requestedById;
	private int billingCycle;
	private int billingAmount;
	private String isExempt;
	private String isOnlyCerts;
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

	private int annualUpdateEmails;
	private String oqEmployees;

	protected List<OshaLog> oshas;
	protected List<ContractorAudit> audits;
	protected List<ContractorOperator> operators;
	protected List<Certificate> certificates;
	protected Map<OperatorAccount, ContractorOperatorFlag> flags;

	public ContractorAccount() {
		this.type = "Contractor";
	}
	
	public ContractorAccount(int id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE})
	@OrderBy("location")
	public List<OshaLog> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaLog> oshas) {
		this.oshas = oshas;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	public List<ContractorAudit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contractorAccount", cascade= {CascadeType.REMOVE})
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

	// /////////// GETTERS/SETTERS /////////////////
	@Column(name = "taxID", nullable = true, length = 100)
	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	@Column(name = "main_trade", nullable = true, length = 100)
	public String getMainTrade() {
		return this.mainTrade;
	}

	public void setMainTrade(String mainTrade) {
		this.mainTrade = mainTrade;
	}

	@Column(name = "trades", nullable = true, length = 65535)
	public String getTrades() {
		return this.trades;
	}

	public void setTrades(String trades) {
		this.trades = trades;
	}

	@Column(name = "subTrades", nullable = true, length = 65535)
	public String getSubTrades() {
		return this.subTrades;
	}

	public void setSubTrades(String subTrades) {
		this.subTrades = subTrades;
	}

	@Column(name = "logo_file", nullable = true, length = 50)
	public String getLogoFile() {
		return this.logoFile;
	}

	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	@Column(name = "brochure_file", nullable = true, length = 3)
	public String getBrochureFile() {
		return this.brochureFile;
	}

	public void setBrochureFile(String brochureFile) {
		this.brochureFile = brochureFile;
	}

	@Column(name = "description", nullable = true, length = 65535)
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

	@Temporal(TemporalType.DATE)
	@Column(name = "accountDate", nullable = true, length = 10)
	public Date getAccountDate() {
		return this.accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
	}

	@Column(name = "paid", nullable = false, length = 3)
	public String getPaid() {
		return this.paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastPayment", nullable = true, length = 10)
	public Date getLastPayment() {
		return this.lastPayment;
	}

	public void setLastPayment(Date lastPayment) {
		this.lastPayment = lastPayment;
	}

	@Column(name = "lastPaymentAmount", nullable = true)
	public int getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}

	public void setLastPaymentAmount(int lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastInvoiceDate", nullable = true, length = 10)
	public Date getLastInvoiceDate() {
		return this.lastInvoiceDate;
	}

	public void setLastInvoiceDate(Date lastInvoiceDate) {
		this.lastInvoiceDate = lastInvoiceDate;
	}

	@Column(name = "accountNewComplete", nullable = true, length = 1)
	public char getAccountNewComplete() {
		return this.accountNewComplete;
	}

	public void setAccountNewComplete(char accountNewComplete) {
		this.accountNewComplete = accountNewComplete;
	}

	@Column(name = "notes", nullable = true, length = 16277215)
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

	@Column(name = "adminNotes", nullable = true, length = 16277215)
	public String getAdminNotes() {
		return this.adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}

	@Column(name = "mustPay", nullable = true, length = 4)
	public String getMustPay() {
		return this.mustPay;
	}

	public void setMustPay(String mustPay) {
		this.mustPay = mustPay;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "paymentExpires", nullable = true, length = 10)
	public Date getPaymentExpires() {
		return this.paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAnnualUpdateEmailDate", nullable = true, length = 10)
	public Date getLastAnnualUpdateEmailDate() {
		return this.lastAnnualUpdateEmailDate;
	}

	public void setLastAnnualUpdateEmailDate(Date lastAnnualUpdateEmailDate) {
		this.lastAnnualUpdateEmailDate = lastAnnualUpdateEmailDate;
	}

	@Column(name = "requestedByID", nullable = true)
	public int getRequestedById() {
		return this.requestedById;
	}

	public void setRequestedById(int requestedById) {
		this.requestedById = requestedById;
	}

	@Column(name = "billingCycle", nullable = false)
	public int getBillingCycle() {
		return this.billingCycle;
	}

	public void setBillingCycle(int billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Column(name = "billingAmount", nullable = false)
	public int getBillingAmount() {
		return this.billingAmount;
	}

	public void setBillingAmount(int billingAmount) {
		this.billingAmount = billingAmount;
	}

	@Column(name = "isExempt", nullable = false, length = 3)
	public String getIsExempt() {
		return this.isExempt;
	}

	public void setIsExempt(String isExempt) {
		this.isExempt = isExempt;
	}

	@Column(name = "isOnlyCerts", nullable = true, length = 3)
	public String getIsOnlyCerts() {
		return this.isOnlyCerts;
	}

	public void setIsOnlyCerts(String isOnlyCerts) {
		this.isOnlyCerts = isOnlyCerts;
	}

	@Column(name = "secondContact", nullable = true, length = 50)
	public String getSecondContact() {
		return this.secondContact;
	}

	public void setSecondContact(String secondContact) {
		this.secondContact = secondContact;
	}

	@Column(name = "secondPhone", nullable = true, length = 50)
	public String getSecondPhone() {
		return this.secondPhone;
	}

	public void setSecondPhone(String secondPhone) {
		this.secondPhone = secondPhone;
	}

	@Column(name = "secondEmail", nullable = true, length = 50)
	public String getSecondEmail() {
		return this.secondEmail;
	}

	public void setSecondEmail(String secondEmail) {
		this.secondEmail = secondEmail;
	}

	@Column(name = "billingContact", nullable = true, length = 50)
	public String getBillingContact() {
		return this.billingContact;
	}

	public void setBillingContact(String billingContact) {
		this.billingContact = billingContact;
	}

	@Column(name = "billingPhone", nullable = true, length = 50)
	public String getBillingPhone() {
		return this.billingPhone;
	}

	public void setBillingPhone(String billingPhone) {
		this.billingPhone = billingPhone;
	}

	@Column(name = "billingEmail", nullable = true, length = 50)
	public String getBillingEmail() {
		return this.billingEmail;
	}

	public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "membershipDate", nullable = true, length = 10)
	public Date getMembershipDate() {
		return this.membershipDate;
	}

	public void setMembershipDate(Date membershipDate) {
		this.membershipDate = membershipDate;
	}

	@Column(name = "newBillingAmount", nullable = false)
	public int getNewBillingAmount() {
		return this.newBillingAmount;
	}

	public void setNewBillingAmount(int newBillingAmount) {
		this.newBillingAmount = newBillingAmount;
	}

	@Column(name = "payingFacilities", nullable = false)
	public int getPayingFacilities() {
		return this.payingFacilities;
	}

	public void setPayingFacilities(int payingFacilities) {
		this.payingFacilities = payingFacilities;
	}

	@ManyToOne
	@JoinColumn(name = "welcomeAuditor_id")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "riskLevel", nullable = false)
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Column(name = "annualUpdateEmails", nullable = true)
	public int getAnnualUpdateEmails() {
		return annualUpdateEmails;
	}

	public void setAnnualUpdateEmails(int annualUpdateEmails) {
		this.annualUpdateEmails = annualUpdateEmails;
	}

	@Column(name = "oqEmployees", nullable = true)
	public String getOqEmployees() {
		return oqEmployees;
	}

	public void setOqEmployees(String oqEmployees) {
		this.oqEmployees = oqEmployees;
	}

	/**
	 * Map of Contractor Flags with OperatorID as the key
	 */
	@MapKey(name = "operatorAccount")
	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE})
	public Map<OperatorAccount, ContractorOperatorFlag> getFlags() {
		return flags;
	}

	public void setFlags(Map<OperatorAccount, ContractorOperatorFlag> flags) {
		this.flags = flags;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE})
	public List<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<Certificate> certificates) {
		this.certificates = certificates;
	}

	@Transient
	public int getUpgradeAmountOwed() {
		if ("No".equals(mustPay))
			return 0;
		if ("Yes".equals(isExempt))
			return 0;
		if (lastPayment.after(lastInvoiceDate) || lastPayment.equals(lastInvoiceDate)) // already paid the invoice
			return 0;
		if (billingAmount < lastPaymentAmount) // they already overpaid (probably garbage data)
			return 0;
		if (DateBean.getDateDifference(lastInvoiceDate, paymentExpires) > 75)
			return 0; // This is an invoice for annual payment

		return billingAmount;
	}

	@Transient
	public int getAnnualAmountOwed() {
		if ("No".equals(mustPay))
			return 0;
		if ("Yes".equals(isExempt))
			return 0;
		if (lastPayment.after(lastInvoiceDate) || lastPayment.equals(lastInvoiceDate)) // already paid the invoice
			return 0;
		if (DateBean.getDateDifference(lastInvoiceDate, paymentExpires) < 75)
			return 0; // This is an invoice for upgrade payment

		return newBillingAmount - lastPaymentAmount;
	}

}
