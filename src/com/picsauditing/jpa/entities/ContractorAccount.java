package com.picsauditing.jpa.entities;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import javax.persistence.FetchType;
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
	private String mustPay = "Yes";
	private Date paymentExpires;
	private Date lastAnnualUpdateEmailDate;
	private int requestedById;
	private int billingCycle = 1;
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

	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
	protected List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	protected List<Certificate> certificates = new ArrayList<Certificate>();
	protected Map<OperatorAccount, ContractorOperatorFlag> flags = new HashMap<OperatorAccount, ContractorOperatorFlag>();

	// Transient helper methods
	protected Map<String, OshaAudit> oshas = null;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contractorAccount", cascade = { CascadeType.REMOVE })
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
	 * Obsolete???
	 * 
	 * @return
	 */
	@Deprecated
	@Column(name = "accountNewComplete", nullable = true, length = 1)
	public char getAccountNewComplete() {
		return this.accountNewComplete;
	}

	@Deprecated
	public void setAccountNewComplete(char accountNewComplete) {
		this.accountNewComplete = accountNewComplete;
	}

	/**
	 * Is this account only doing certificates? TODO ask Jared, if these
	 * accounts get audits created for them?
	 * 
	 * @return
	 */
	@Column(name = "isOnlyCerts", nullable = true, length = 3)
	public String getIsOnlyCerts() {
		return this.isOnlyCerts;
	}

	public void setIsOnlyCerts(String isOnlyCerts) {
		this.isOnlyCerts = isOnlyCerts;
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

	/**
	 * Is this account a PQF-only account? Yes/No
	 * 
	 * @deprecated use the BillContractor.requiresAudit() method instead
	 * @return
	 */
	@Deprecated
	@Column(name = "isExempt", nullable = false, length = 3)
	public String getIsExempt() {
		return this.isExempt;
	}

	@Deprecated
	public void setIsExempt(String isExempt) {
		this.isExempt = isExempt;
	}

	@Column(name = "payingFacilities", nullable = false)
	public int getPayingFacilities() {
		return this.payingFacilities;
	}

	public void setPayingFacilities(int payingFacilities) {
		this.payingFacilities = payingFacilities;
	}

	/**
	 * The date this is first activated. This is never reset afterwards.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "accountDate", nullable = true, length = 10)
	public Date getAccountDate() {
		return this.accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "membershipDate", nullable = true, length = 10)
	public Date getMembershipDate() {
		return this.membershipDate;
	}

	public void setMembershipDate(Date membershipDate) {
		this.membershipDate = membershipDate;
	}

	// INVOICING Billing info
	/**
	 * Is this account on a 1 or 3 year billing cycle
	 */
	@Column(name = "billingCycle", nullable = false)
	public int getBillingCycle() {
		return this.billingCycle;
	}

	public void setBillingCycle(int billingCycle) {
		this.billingCycle = billingCycle;
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
	@Column(name = "lastInvoiceDate", nullable = true, length = 10)
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
	@Column(name = "paid", nullable = false, length = 3)
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
	@Column(name = "lastPayment", nullable = true, length = 10)
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
	@Column(name = "lastPaymentAmount", nullable = true)
	public int getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}

	public void setLastPaymentAmount(int lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	/**
	 * The date the lastPayment expires and the contractor is due to pay another
	 * "period's" membership fee
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "paymentExpires", nullable = true, length = 10)
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

	@OneToMany(mappedBy = "contractorAccount", cascade = { CascadeType.REMOVE })
	public List<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<Certificate> certificates) {
		this.certificates = certificates;
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
	 * Get a map of the last 3 years of applicable osha data (verified or not)
	 */
	public Map<String, OshaAudit> getOshas() {
		if (oshas != null)
			return oshas;
		
		oshas = new TreeMap<String, OshaAudit>();
		
		for (ContractorAudit audit : getAudits()) {
			if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM
				&& audit.getAuditStatus().isActiveSubmitted()) {
				// Store the corporate OSHA rates into a map for later use
				for(OshaAudit osha : audit.getOshas())
					if (osha.isCorporate() && osha.isApplicable())
						oshas.put(audit.getAuditFor(), osha);
			}
		}
		
		int count = oshas.size();
		if (count > 3) {
			System.out.println("Unhandled error getting OSHA logs for contractor " + id);
			// TODO handle this situation somehow
			// like remove submitted records, or don't consider years before 3 years ago
		}
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
			
			for(String key : oshas.keySet()) {
				OshaAudit osha = oshas.get(key);
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
			
			oshas.put(OshaAudit.AVG, avg);
		}
		return oshas;
	}
	
	@Transient
	/**
	 * Get a map of the last 3 years of applicable emr data (verified or not)
	 */
	public Map<String, AuditData> getEmrs() throws Exception {
		if (emrs != null)
			return emrs;
		
		emrs = new HashMap<String, AuditData>();
		
		for (ContractorAudit audit : getAudits()) {
			if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM
				&& audit.getAuditStatus().isActiveSubmitted()) {
				// Store the EMR rates into a map for later use
				for(AuditData answer : audit.getData())
					if (answer.getQuestion().getQuestionID() == AuditQuestion.EMR)
						emrs.put(audit.getAuditFor(), answer);
			}
		}
		int count = emrs.size();
		if (count > 3) {
			System.out.println("Unhandled error getting EMR data for contractor " + id);
			// TODO handle this situation somehow
			// like remove submitted records, or don't consider years before 3 years ago
		}
		if (count > 0) {
			AuditData avg = new AuditData();
			avg.setVerified(true);
			float rateTotal = 0;
			// Reset this to zero because we're not sure how many answers we'll actually be able to parse
			count = 0;
			for(String key : emrs.keySet()) {
				AuditData emr = emrs.get(key);
				avg.setAudit(emr.getAudit());
				AuditQuestion avgQuestion = new AuditQuestion();
				avgQuestion.setQuestionID(AuditQuestion.EMR_AVG);
				// We may just want to query the EMR question from the DB
				avgQuestion.setQuestion("Average EMR");
				avg.setQuestion(avgQuestion);
				if (emr.isUnverified())
					avg.setVerified(false);
				try {
					float rate = Float.parseFloat(emr.getVerifiedAnswerOrAnswer());
					rateTotal += rate;
					count++;
				} catch (Exception e) {
					System.out.println("Failed to parse EMR rate:" + emr.getVerifiedAnswerOrAnswer() + " for contractor " + id);
				}
			}
			float avgRateFloat = rateTotal / count;
			//avgRateFloat = Math.round(100 * avgRateFloat) / 100;
			avg.setAnswer(Float.toString(avgRateFloat));
			emrs.put(OshaAudit.AVG, avg);
		}
		return emrs;
	}
}
