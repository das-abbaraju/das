package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "contractor_info")
public class ContractorInfo implements java.io.Serializable {

	private int id;
	private Account account;
	private String taxId;
	private String mainTrade;
	private String trades;
	private String subTrades;
	private String logoFile;
	private Date auditDate;
	private Date lastAuditDate;
	private String auditHour;
	private String auditAmPm;
	private String prequalFile;
	private Date pqfSubmittedDate;
	private Date desktopSubmittedDate;
	private String brochureFile;
	private String description;
	private String status;
	private byte certs;
	private Date accountDate;
	private Date welcomeEmailDate;
	private Date emailConfirmedDate;
	private String paid;
	private Date lastPayment;
	private short lastPaymentAmount;
	private Date lastInvoiceDate;
	private char accountNewComplete;
	private String notes;
	private String adminNotes;
	private String canEditPrequal;
	private String canEditDesktop;
	private Date lastAuditEmailDate;
	private String mustPay;
	private Date paymentExpires;
	private Date lastAnnualUpdateEmailDate;
	private int auditorId;
	private int desktopAuditorId;
	private int daAuditorId;
	private int pqfAuditorId;
	private Date assignedDate;
	private char isPrequalOk;
	private String auditStatus;
	private Date auditCompletedDate;
	private Date auditClosedDate;
	private Date desktopAssignedDate;
	private Date desktopCompletedDate;
	private Date desktopClosedDate;
	private Date daAssignedDate;
	private Date daSubmittedDate;
	private Date daClosedDate;
	private Date officeSubmittedDate;
	private Date officeClosedDate;
	private int requestedById;
	private byte billingCycle;
	private short billingAmount;
	private String isExempt;
	private String hasExpiredCerts;
	private String isOnlyCerts;
	private String auditLocation;
	private byte desktopPercent;
	private byte desktopVerifiedPercent;
	private byte officePercent;
	private byte officeVerifiedPercent;
	private byte daPercent;
	private byte daVerifiedPercent;
	private byte pqfPercent;
	private String hasNcmsdesktop;
	private String isNewOfficeAudit;
	private String secondContact;
	private String secondPhone;
	private String secondEmail;
	private String billingContact;
	private String billingPhone;
	private String billingEmail;
	private Date membershipDate;
	private short newBillingAmount;
	private short payingFacilities;
	private Date welcomeCallDate;
	private int welcomeAuditorId;


	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false, insertable = false, updatable = false)
	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(name = "taxID", nullable = false, length = 100)
	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	@Column(name = "main_trade", nullable = false, length = 100)
	public String getMainTrade() {
		return this.mainTrade;
	}

	public void setMainTrade(String mainTrade) {
		this.mainTrade = mainTrade;
	}

	@Column(name = "trades", nullable = false, length = 65535)
	public String getTrades() {
		return this.trades;
	}

	public void setTrades(String trades) {
		this.trades = trades;
	}

	@Column(name = "subTrades", nullable = false, length = 65535)
	public String getSubTrades() {
		return this.subTrades;
	}

	public void setSubTrades(String subTrades) {
		this.subTrades = subTrades;
	}

	@Column(name = "logo_file", nullable = false, length = 50)
	public String getLogoFile() {
		return this.logoFile;
	}

	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "auditDate", nullable = false, length = 10)
	public Date getAuditDate() {
		return this.auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAuditDate", nullable = false, length = 10)
	public Date getLastAuditDate() {
		return this.lastAuditDate;
	}

	public void setLastAuditDate(Date lastAuditDate) {
		this.lastAuditDate = lastAuditDate;
	}

	@Column(name = "auditHour", nullable = false, length = 10)
	public String getAuditHour() {
		return this.auditHour;
	}

	public void setAuditHour(String auditHour) {
		this.auditHour = auditHour;
	}

	@Column(name = "auditAmPm", nullable = false, length = 2)
	public String getAuditAmPm() {
		return this.auditAmPm;
	}

	public void setAuditAmPm(String auditAmPm) {
		this.auditAmPm = auditAmPm;
	}

	@Column(name = "prequal_file", nullable = false, length = 3)
	public String getPrequalFile() {
		return this.prequalFile;
	}

	public void setPrequalFile(String prequalFile) {
		this.prequalFile = prequalFile;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "pqfSubmittedDate", nullable = false, length = 10)
	public Date getPqfSubmittedDate() {
		return this.pqfSubmittedDate;
	}

	public void setPqfSubmittedDate(Date pqfSubmittedDate) {
		this.pqfSubmittedDate = pqfSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopSubmittedDate", nullable = false, length = 10)
	public Date getDesktopSubmittedDate() {
		return this.desktopSubmittedDate;
	}

	public void setDesktopSubmittedDate(Date desktopSubmittedDate) {
		this.desktopSubmittedDate = desktopSubmittedDate;
	}

	@Column(name = "brochure_file", nullable = false, length = 3)
	public String getBrochureFile() {
		return this.brochureFile;
	}

	public void setBrochureFile(String brochureFile) {
		this.brochureFile = brochureFile;
	}

	@Column(name = "description", nullable = false, length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "status", nullable = false, length = 9)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "certs", nullable = false)
	public byte getCerts() {
		return this.certs;
	}

	public void setCerts(byte certs) {
		this.certs = certs;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "accountDate", nullable = false, length = 10)
	public Date getAccountDate() {
		return this.accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "welcomeEmailDate", nullable = false, length = 10)
	public Date getWelcomeEmailDate() {
		return this.welcomeEmailDate;
	}

	public void setWelcomeEmailDate(Date welcomeEmailDate) {
		this.welcomeEmailDate = welcomeEmailDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "emailConfirmedDate", nullable = false, length = 10)
	public Date getEmailConfirmedDate() {
		return this.emailConfirmedDate;
	}

	public void setEmailConfirmedDate(Date emailConfirmedDate) {
		this.emailConfirmedDate = emailConfirmedDate;
	}

	@Column(name = "paid", nullable = false, length = 3)
	public String getPaid() {
		return this.paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastPayment", nullable = false, length = 10)
	public Date getLastPayment() {
		return this.lastPayment;
	}

	public void setLastPayment(Date lastPayment) {
		this.lastPayment = lastPayment;
	}

	@Column(name = "lastPaymentAmount", nullable = false)
	public short getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}

	public void setLastPaymentAmount(short lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastInvoiceDate", nullable = false, length = 10)
	public Date getLastInvoiceDate() {
		return this.lastInvoiceDate;
	}

	public void setLastInvoiceDate(Date lastInvoiceDate) {
		this.lastInvoiceDate = lastInvoiceDate;
	}

	@Column(name = "accountNewComplete", nullable = false, length = 1)
	public char getAccountNewComplete() {
		return this.accountNewComplete;
	}

	public void setAccountNewComplete(char accountNewComplete) {
		this.accountNewComplete = accountNewComplete;
	}

	@Column(name = "notes", nullable = false, length = 16277215)
	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "adminNotes", nullable = false, length = 16277215)
	public String getAdminNotes() {
		return this.adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}

	@Column(name = "canEditPrequal", nullable = false, length = 4)
	public String getCanEditPrequal() {
		return this.canEditPrequal;
	}

	public void setCanEditPrequal(String canEditPrequal) {
		this.canEditPrequal = canEditPrequal;
	}

	@Column(name = "canEditDesktop", nullable = false, length = 4)
	public String getCanEditDesktop() {
		return this.canEditDesktop;
	}

	public void setCanEditDesktop(String canEditDesktop) {
		this.canEditDesktop = canEditDesktop;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAuditEmailDate", nullable = false, length = 10)
	public Date getLastAuditEmailDate() {
		return this.lastAuditEmailDate;
	}

	public void setLastAuditEmailDate(Date lastAuditEmailDate) {
		this.lastAuditEmailDate = lastAuditEmailDate;
	}

	@Column(name = "mustPay", nullable = false, length = 4)
	public String getMustPay() {
		return this.mustPay;
	}

	public void setMustPay(String mustPay) {
		this.mustPay = mustPay;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "paymentExpires", nullable = false, length = 10)
	public Date getPaymentExpires() {
		return this.paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAnnualUpdateEmailDate", nullable = false, length = 10)
	public Date getLastAnnualUpdateEmailDate() {
		return this.lastAnnualUpdateEmailDate;
	}

	public void setLastAnnualUpdateEmailDate(Date lastAnnualUpdateEmailDate) {
		this.lastAnnualUpdateEmailDate = lastAnnualUpdateEmailDate;
	}

	@Column(name = "auditor_id", nullable = false)
	public int getAuditorId() {
		return this.auditorId;
	}

	public void setAuditorId(int auditorId) {
		this.auditorId = auditorId;
	}

	@Column(name = "desktopAuditor_id", nullable = false)
	public int getDesktopAuditorId() {
		return this.desktopAuditorId;
	}

	public void setDesktopAuditorId(int desktopAuditorId) {
		this.desktopAuditorId = desktopAuditorId;
	}

	@Column(name = "daAuditor_id", nullable = false)
	public int getDaAuditorId() {
		return this.daAuditorId;
	}

	public void setDaAuditorId(int daAuditorId) {
		this.daAuditorId = daAuditorId;
	}

	@Column(name = "pqfAuditor_id", nullable = false)
	public int getPqfAuditorId() {
		return this.pqfAuditorId;
	}

	public void setPqfAuditorId(int pqfAuditorId) {
		this.pqfAuditorId = pqfAuditorId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "assignedDate", nullable = false, length = 10)
	public Date getAssignedDate() {
		return this.assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	@Column(name = "isPrequalOK", nullable = false, length = 1)
	public char getIsPrequalOk() {
		return this.isPrequalOk;
	}

	public void setIsPrequalOk(char isPrequalOk) {
		this.isPrequalOk = isPrequalOk;
	}

	@Column(name = "auditStatus", nullable = false, length = 14)
	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "auditCompletedDate", nullable = false, length = 10)
	public Date getAuditCompletedDate() {
		return this.auditCompletedDate;
	}

	public void setAuditCompletedDate(Date auditCompletedDate) {
		this.auditCompletedDate = auditCompletedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "auditClosedDate", nullable = false, length = 10)
	public Date getAuditClosedDate() {
		return this.auditClosedDate;
	}

	public void setAuditClosedDate(Date auditClosedDate) {
		this.auditClosedDate = auditClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopAssignedDate", nullable = false, length = 10)
	public Date getDesktopAssignedDate() {
		return this.desktopAssignedDate;
	}

	public void setDesktopAssignedDate(Date desktopAssignedDate) {
		this.desktopAssignedDate = desktopAssignedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopCompletedDate", nullable = false, length = 10)
	public Date getDesktopCompletedDate() {
		return this.desktopCompletedDate;
	}

	public void setDesktopCompletedDate(Date desktopCompletedDate) {
		this.desktopCompletedDate = desktopCompletedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopClosedDate", nullable = false, length = 10)
	public Date getDesktopClosedDate() {
		return this.desktopClosedDate;
	}

	public void setDesktopClosedDate(Date desktopClosedDate) {
		this.desktopClosedDate = desktopClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daAssignedDate", nullable = false, length = 10)
	public Date getDaAssignedDate() {
		return this.daAssignedDate;
	}

	public void setDaAssignedDate(Date daAssignedDate) {
		this.daAssignedDate = daAssignedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daSubmittedDate", nullable = false, length = 10)
	public Date getDaSubmittedDate() {
		return this.daSubmittedDate;
	}

	public void setDaSubmittedDate(Date daSubmittedDate) {
		this.daSubmittedDate = daSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daClosedDate", nullable = false, length = 10)
	public Date getDaClosedDate() {
		return this.daClosedDate;
	}

	public void setDaClosedDate(Date daClosedDate) {
		this.daClosedDate = daClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "officeSubmittedDate", nullable = false, length = 10)
	public Date getOfficeSubmittedDate() {
		return this.officeSubmittedDate;
	}

	public void setOfficeSubmittedDate(Date officeSubmittedDate) {
		this.officeSubmittedDate = officeSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "officeClosedDate", nullable = false, length = 10)
	public Date getOfficeClosedDate() {
		return this.officeClosedDate;
	}

	public void setOfficeClosedDate(Date officeClosedDate) {
		this.officeClosedDate = officeClosedDate;
	}

	@Column(name = "requestedByID", nullable = false)
	public int getRequestedById() {
		return this.requestedById;
	}

	public void setRequestedById(int requestedById) {
		this.requestedById = requestedById;
	}

	@Column(name = "billingCycle", nullable = false)
	public byte getBillingCycle() {
		return this.billingCycle;
	}

	public void setBillingCycle(byte billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Column(name = "billingAmount", nullable = false)
	public short getBillingAmount() {
		return this.billingAmount;
	}

	public void setBillingAmount(short billingAmount) {
		this.billingAmount = billingAmount;
	}

	@Column(name = "isExempt", nullable = false, length = 3)
	public String getIsExempt() {
		return this.isExempt;
	}

	public void setIsExempt(String isExempt) {
		this.isExempt = isExempt;
	}

	@Column(name = "hasExpiredCerts", nullable = false, length = 3)
	public String getHasExpiredCerts() {
		return this.hasExpiredCerts;
	}

	public void setHasExpiredCerts(String hasExpiredCerts) {
		this.hasExpiredCerts = hasExpiredCerts;
	}

	@Column(name = "isOnlyCerts", nullable = false, length = 3)
	public String getIsOnlyCerts() {
		return this.isOnlyCerts;
	}

	public void setIsOnlyCerts(String isOnlyCerts) {
		this.isOnlyCerts = isOnlyCerts;
	}

	@Column(name = "auditLocation", nullable = false, length = 8)
	public String getAuditLocation() {
		return this.auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

	@Column(name = "desktopPercent", nullable = false)
	public byte getDesktopPercent() {
		return this.desktopPercent;
	}

	public void setDesktopPercent(byte desktopPercent) {
		this.desktopPercent = desktopPercent;
	}

	@Column(name = "desktopVerifiedPercent", nullable = false)
	public byte getDesktopVerifiedPercent() {
		return this.desktopVerifiedPercent;
	}

	public void setDesktopVerifiedPercent(byte desktopVerifiedPercent) {
		this.desktopVerifiedPercent = desktopVerifiedPercent;
	}

	@Column(name = "officePercent", nullable = false)
	public byte getOfficePercent() {
		return this.officePercent;
	}

	public void setOfficePercent(byte officePercent) {
		this.officePercent = officePercent;
	}

	@Column(name = "officeVerifiedPercent", nullable = false)
	public byte getOfficeVerifiedPercent() {
		return this.officeVerifiedPercent;
	}

	public void setOfficeVerifiedPercent(byte officeVerifiedPercent) {
		this.officeVerifiedPercent = officeVerifiedPercent;
	}

	@Column(name = "daPercent", nullable = false)
	public byte getDaPercent() {
		return this.daPercent;
	}

	public void setDaPercent(byte daPercent) {
		this.daPercent = daPercent;
	}

	@Column(name = "daVerifiedPercent", nullable = false)
	public byte getDaVerifiedPercent() {
		return this.daVerifiedPercent;
	}

	public void setDaVerifiedPercent(byte daVerifiedPercent) {
		this.daVerifiedPercent = daVerifiedPercent;
	}

	@Column(name = "pqfPercent", nullable = false)
	public byte getPqfPercent() {
		return this.pqfPercent;
	}

	public void setPqfPercent(byte pqfPercent) {
		this.pqfPercent = pqfPercent;
	}

	@Column(name = "hasNCMSDesktop", nullable = false, length = 3)
	public String getHasNcmsdesktop() {
		return this.hasNcmsdesktop;
	}

	public void setHasNcmsdesktop(String hasNcmsdesktop) {
		this.hasNcmsdesktop = hasNcmsdesktop;
	}

	@Column(name = "isNewOfficeAudit", nullable = false, length = 3)
	public String getIsNewOfficeAudit() {
		return this.isNewOfficeAudit;
	}

	public void setIsNewOfficeAudit(String isNewOfficeAudit) {
		this.isNewOfficeAudit = isNewOfficeAudit;
	}

	@Column(name = "secondContact", nullable = false, length = 50)
	public String getSecondContact() {
		return this.secondContact;
	}

	public void setSecondContact(String secondContact) {
		this.secondContact = secondContact;
	}

	@Column(name = "secondPhone", nullable = false, length = 50)
	public String getSecondPhone() {
		return this.secondPhone;
	}

	public void setSecondPhone(String secondPhone) {
		this.secondPhone = secondPhone;
	}

	@Column(name = "secondEmail", nullable = false, length = 50)
	public String getSecondEmail() {
		return this.secondEmail;
	}

	public void setSecondEmail(String secondEmail) {
		this.secondEmail = secondEmail;
	}

	@Column(name = "billingContact", nullable = false, length = 50)
	public String getBillingContact() {
		return this.billingContact;
	}

	public void setBillingContact(String billingContact) {
		this.billingContact = billingContact;
	}

	@Column(name = "billingPhone", nullable = false, length = 50)
	public String getBillingPhone() {
		return this.billingPhone;
	}

	public void setBillingPhone(String billingPhone) {
		this.billingPhone = billingPhone;
	}

	@Column(name = "billingEmail", nullable = false, length = 50)
	public String getBillingEmail() {
		return this.billingEmail;
	}

	public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "membershipDate", nullable = false, length = 10)
	public Date getMembershipDate() {
		return this.membershipDate;
	}

	public void setMembershipDate(Date membershipDate) {
		this.membershipDate = membershipDate;
	}

	@Column(name = "newBillingAmount", nullable = false)
	public short getNewBillingAmount() {
		return this.newBillingAmount;
	}

	public void setNewBillingAmount(short newBillingAmount) {
		this.newBillingAmount = newBillingAmount;
	}

	@Column(name = "payingFacilities", nullable = false)
	public short getPayingFacilities() {
		return this.payingFacilities;
	}

	public void setPayingFacilities(short payingFacilities) {
		this.payingFacilities = payingFacilities;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "welcomeCallDate", nullable = false, length = 10)
	public Date getWelcomeCallDate() {
		return this.welcomeCallDate;
	}

	public void setWelcomeCallDate(Date welcomeCallDate) {
		this.welcomeCallDate = welcomeCallDate;
	}

	@Column(name = "welcomeAuditor_id", nullable = false)
	public int getWelcomeAuditorId() {
		return this.welcomeAuditorId;
	}

	public void setWelcomeAuditorId(int welcomeAuditorId) {
		this.welcomeAuditorId = welcomeAuditorId;
	}
}
