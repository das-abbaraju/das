package com.picsauditing.jpa.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.PICS.Utilities;

@Entity
@Table(name = "contractor_info")
@PrimaryKeyJoinColumn(name="id")
public class ContractorAccount extends Account implements java.io.Serializable{
	
	private String taxId;
	private String mainTrade;
	private String trades;
	private String subTrades;
	private String logoFile;
	private String brochureFile;
	private String description;
	private String status;
	private int certs;
	private Date welcomeEmailDate;
	private String paid;
	private Date lastPayment;
	private int lastPaymentAmount;
	private Date lastInvoiceDate;
	private String canEditPrequal;
	private String canEditDesktop;
	private Date lastAuditEmailDate;
	private Date accountDate;
	private char accountNewComplete;
	private String notes;
	private String adminNotes;
	private String mustPay;
	private Date paymentExpires;
	private int billingCycle;
	private int billingAmount;
	private String isExempt;
	private String hasExpiredCerts;
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
	private Date welcomeCallDate;
	private int welcomeAuditorId;
	private LowMedHigh riskLevel;
	
	private int annualUpdateEmails;
	private String daRequired;
	private Date tempAuditDateTime;
	private String oqEmployees;
	
	
	// Audit fields to be removed
	private Date auditDate;
	private Date lastAuditDate;
	private String auditHour;
	private String auditAmPm;
	private String prequalFile;
	private Date pqfSubmittedDate;
	private Date desktopSubmittedDate;
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
	private String auditLocation;
	private int desktopPercent;
	private int desktopVerifiedPercent;
	private int officePercent;
	private int officeVerifiedPercent;
	private int daPercent;
	private int daVerifiedPercent;
	private int pqfPercent;
	private String hasNcmsdesktop;
	private String isNewOfficeAudit;

	protected List<OshaLog> oshas;
	protected List<ContractorAudit> audits;
	protected List<ContractorOperator> operators;
	
	
	@OneToMany(mappedBy = "contractorAccount")
	public List<OshaLog> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaLog> oshas) {
		this.oshas = oshas;
	}

	@OneToMany(mappedBy = "contractorAccount")
	public List<ContractorAudit> getAudits() {
		return audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contractorAccount")
	//@OrderBy("operatorName")
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}
	
	///////////// GETTERS/SETTERS /////////////////
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

	@Temporal(TemporalType.DATE)
	@Column(name = "auditDate", nullable = true, length = 10)
	public Date getAuditDate() {
		return this.auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAuditDate", nullable = true, length = 10)
	public Date getLastAuditDate() {
		return this.lastAuditDate;
	}

	public void setLastAuditDate(Date lastAuditDate) {
		this.lastAuditDate = lastAuditDate;
	}

	@Column(name = "auditHour", nullable = true, length = 10)
	public String getAuditHour() {
		return this.auditHour;
	}

	public void setAuditHour(String auditHour) {
		this.auditHour = auditHour;
	}

	@Column(name = "auditAmPm", nullable = true, length = 2)
	public String getAuditAmPm() {
		return this.auditAmPm;
	}

	public void setAuditAmPm(String auditAmPm) {
		this.auditAmPm = auditAmPm;
	}

	@Column(name = "prequal_file", nullable = true, length = 3)
	public String getPrequalFile() {
		return this.prequalFile;
	}

	public void setPrequalFile(String prequalFile) {
		this.prequalFile = prequalFile;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "pqfSubmittedDate", nullable = true, length = 10)
	public Date getPqfSubmittedDate() {
		return this.pqfSubmittedDate;
	}

	public void setPqfSubmittedDate(Date pqfSubmittedDate) {
		this.pqfSubmittedDate = pqfSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopSubmittedDate", nullable = true, length = 10)
	public Date getDesktopSubmittedDate() {
		return this.desktopSubmittedDate;
	}

	public void setDesktopSubmittedDate(Date desktopSubmittedDate) {
		this.desktopSubmittedDate = desktopSubmittedDate;
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

	@Column(name = "status", nullable = true, length = 9)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "certs", nullable = true)
	public int getCerts() {
		return this.certs;
	}

	public void setCerts(int certs) {
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
	@Column(name = "welcomeEmailDate", nullable = true, length = 10)
	public Date getWelcomeEmailDate() {
		return this.welcomeEmailDate;
	}

	public void setWelcomeEmailDate(Date welcomeEmailDate) {
		this.welcomeEmailDate = welcomeEmailDate;
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

	@Column(name = "adminNotes", nullable = true, length = 16277215)
	public String getAdminNotes() {
		return this.adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}

	@Column(name = "canEditPrequal", nullable = true, length = 4)
	public String getCanEditPrequal() {
		return this.canEditPrequal;
	}

	public void setCanEditPrequal(String canEditPrequal) {
		this.canEditPrequal = canEditPrequal;
	}

	@Column(name = "canEditDesktop", nullable = true, length = 4)
	public String getCanEditDesktop() {
		return this.canEditDesktop;
	}

	public void setCanEditDesktop(String canEditDesktop) {
		this.canEditDesktop = canEditDesktop;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastAuditEmailDate", nullable = true, length = 10)
	public Date getLastAuditEmailDate() {
		return this.lastAuditEmailDate;
	}

	public void setLastAuditEmailDate(Date lastAuditEmailDate) {
		this.lastAuditEmailDate = lastAuditEmailDate;
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

	@Column(name = "auditor_id", nullable = true)
	public int getAuditorId() {
		return this.auditorId;
	}

	public void setAuditorId(int auditorId) {
		this.auditorId = auditorId;
	}

	@Column(name = "desktopAuditor_id", nullable = true)
	public int getDesktopAuditorId() {
		return this.desktopAuditorId;
	}

	public void setDesktopAuditorId(int desktopAuditorId) {
		this.desktopAuditorId = desktopAuditorId;
	}

	@Column(name = "daAuditor_id", nullable = true)
	public int getDaAuditorId() {
		return this.daAuditorId;
	}

	public void setDaAuditorId(int daAuditorId) {
		this.daAuditorId = daAuditorId;
	}

	@Column(name = "pqfAuditor_id", nullable = true)
	public int getPqfAuditorId() {
		return this.pqfAuditorId;
	}

	public void setPqfAuditorId(int pqfAuditorId) {
		this.pqfAuditorId = pqfAuditorId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "assignedDate", nullable = true, length = 10)
	public Date getAssignedDate() {
		return this.assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	@Column(name = "isPrequalOK", nullable = true, length = 1)
	public char getIsPrequalOk() {
		return this.isPrequalOk;
	}

	public void setIsPrequalOk(char isPrequalOk) {
		this.isPrequalOk = isPrequalOk;
	}

	@Column(name = "auditStatus", nullable = true, length = 14)
	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "auditCompletedDate", nullable = true, length = 10)
	public Date getAuditCompletedDate() {
		return this.auditCompletedDate;
	}

	public void setAuditCompletedDate(Date auditCompletedDate) {
		this.auditCompletedDate = auditCompletedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "auditClosedDate", nullable = true, length = 10)
	public Date getAuditClosedDate() {
		return this.auditClosedDate;
	}

	public void setAuditClosedDate(Date auditClosedDate) {
		this.auditClosedDate = auditClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopAssignedDate", nullable = true, length = 10)
	public Date getDesktopAssignedDate() {
		return this.desktopAssignedDate;
	}

	public void setDesktopAssignedDate(Date desktopAssignedDate) {
		this.desktopAssignedDate = desktopAssignedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopCompletedDate", nullable = true, length = 10)
	public Date getDesktopCompletedDate() {
		return this.desktopCompletedDate;
	}

	public void setDesktopCompletedDate(Date desktopCompletedDate) {
		this.desktopCompletedDate = desktopCompletedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "desktopClosedDate", nullable = true, length = 10)
	public Date getDesktopClosedDate() {
		return this.desktopClosedDate;
	}

	public void setDesktopClosedDate(Date desktopClosedDate) {
		this.desktopClosedDate = desktopClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daAssignedDate", nullable = true, length = 10)
	public Date getDaAssignedDate() {
		return this.daAssignedDate;
	}

	public void setDaAssignedDate(Date daAssignedDate) {
		this.daAssignedDate = daAssignedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daSubmittedDate", nullable = true, length = 10)
	public Date getDaSubmittedDate() {
		return this.daSubmittedDate;
	}

	public void setDaSubmittedDate(Date daSubmittedDate) {
		this.daSubmittedDate = daSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "daClosedDate", nullable = true, length = 10)
	public Date getDaClosedDate() {
		return this.daClosedDate;
	}

	public void setDaClosedDate(Date daClosedDate) {
		this.daClosedDate = daClosedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "officeSubmittedDate", nullable = true, length = 10)
	public Date getOfficeSubmittedDate() {
		return this.officeSubmittedDate;
	}

	public void setOfficeSubmittedDate(Date officeSubmittedDate) {
		this.officeSubmittedDate = officeSubmittedDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "officeClosedDate", nullable = true, length = 10)
	public Date getOfficeClosedDate() {
		return this.officeClosedDate;
	}

	public void setOfficeClosedDate(Date officeClosedDate) {
		this.officeClosedDate = officeClosedDate;
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

	@Column(name = "billingAmount", nullable = true)
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

	@Column(name = "hasExpiredCerts", nullable = true, length = 3)
	public String getHasExpiredCerts() {
		return this.hasExpiredCerts;
	}

	public void setHasExpiredCerts(String hasExpiredCerts) {
		this.hasExpiredCerts = hasExpiredCerts;
	}

	@Column(name = "isOnlyCerts", nullable = true, length = 3)
	public String getIsOnlyCerts() {
		return this.isOnlyCerts;
	}

	public void setIsOnlyCerts(String isOnlyCerts) {
		this.isOnlyCerts = isOnlyCerts;
	}

	@Column(name = "auditLocation", nullable = true, length = 8)
	public String getAuditLocation() {
		return this.auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

	@Column(name = "desktopPercent", nullable = true)
	public int getDesktopPercent() {
		return this.desktopPercent;
	}

	public void setDesktopPercent(int desktopPercent) {
		this.desktopPercent = desktopPercent;
	}

	@Column(name = "desktopVerifiedPercent", nullable = true)
	public int getDesktopVerifiedPercent() {
		return this.desktopVerifiedPercent;
	}

	public void setDesktopVerifiedPercent(int desktopVerifiedPercent) {
		this.desktopVerifiedPercent = desktopVerifiedPercent;
	}

	@Column(name = "officePercent", nullable = true)
	public int getOfficePercent() {
		return this.officePercent;
	}

	public void setOfficePercent(int officePercent) {
		this.officePercent = officePercent;
	}

	@Column(name = "officeVerifiedPercent", nullable = true)
	public int getOfficeVerifiedPercent() {
		return this.officeVerifiedPercent;
	}

	public void setOfficeVerifiedPercent(int officeVerifiedPercent) {
		this.officeVerifiedPercent = officeVerifiedPercent;
	}

	@Column(name = "daPercent", nullable = true)
	public int getDaPercent() {
		return this.daPercent;
	}

	public void setDaPercent(int daPercent) {
		this.daPercent = daPercent;
	}

	@Column(name = "daVerifiedPercent", nullable = true)
	public int getDaVerifiedPercent() {
		return this.daVerifiedPercent;
	}

	public void setDaVerifiedPercent(int daVerifiedPercent) {
		this.daVerifiedPercent = daVerifiedPercent;
	}

	@Column(name = "pqfPercent", nullable = true)
	public int getPqfPercent() {
		return this.pqfPercent;
	}

	public void setPqfPercent(int pqfPercent) {
		this.pqfPercent = pqfPercent;
	}

	@Column(name = "hasNCMSDesktop", nullable = true, length = 3)
	public String getHasNcmsdesktop() {
		return this.hasNcmsdesktop;
	}

	public void setHasNcmsdesktop(String hasNcmsdesktop) {
		this.hasNcmsdesktop = hasNcmsdesktop;
	}

	@Column(name = "isNewOfficeAudit", nullable = true, length = 3)
	public String getIsNewOfficeAudit() {
		return this.isNewOfficeAudit;
	}

	public void setIsNewOfficeAudit(String isNewOfficeAudit) {
		this.isNewOfficeAudit = isNewOfficeAudit;
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
	@Column(name = "membershipDate", nullable = false, length = 10)
	public Date getMembershipDate() {
		return this.membershipDate;
	}

	public void setMembershipDate(Date membershipDate) {
		this.membershipDate = membershipDate;
	}

	@Column(name = "newBillingAmount", nullable = true)
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

	@Temporal(TemporalType.DATE)
	@Column(name = "welcomeCallDate", nullable = true, length = 10)
	public Date getWelcomeCallDate() {
		return this.welcomeCallDate;
	}

	public void setWelcomeCallDate(Date welcomeCallDate) {
		this.welcomeCallDate = welcomeCallDate;
	}

	@Column(name = "welcomeAuditor_id", nullable = true)
	public int getWelcomeAuditorId() {
		return this.welcomeAuditorId;
	}

	public void setWelcomeAuditorId(int welcomeAuditorId) {
		this.welcomeAuditorId = welcomeAuditorId;
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

	
	@Column(name = "daRequired", nullable = true)
	public String getDaRequired() {
		return daRequired;
	}

	public void setDaRequired(String daRequired) {
		this.daRequired = daRequired;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "tempAuditDateTime", nullable = true, length = 10)
	public Date getTempAuditDateTime() {
		return tempAuditDateTime;
	}

	public void setTempAuditDateTime(Date tempAuditDateTime) {
		this.tempAuditDateTime = tempAuditDateTime;
	}

	@Column(name = "oqEmployees", nullable = true)
	public String getOqEmployees() {
		return oqEmployees;
	}

	public void setOqEmployees(String oqEmployees) {
		this.oqEmployees = oqEmployees;
	}

	
	
	


}
