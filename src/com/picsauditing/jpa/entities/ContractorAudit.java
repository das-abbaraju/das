package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.Location;
import com.picsauditing.util.Strings;


@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAudit extends BaseTable implements java.io.Serializable {
	private AuditType auditType;
	private ContractorAccount contractorAccount;
	private AuditStatus auditStatus = AuditStatus.Pending;
	private Date expiresDate;
	private User auditor;
	private User closingAuditor;
	private Date assignedDate;
	private Date completedDate;
	private Date closedDate;
	private Date paidDate;
	private OperatorAccount requestingOpAccount;
	private int percentComplete;
	private int percentVerified;
	private float score;
	private boolean manuallyAdded;
	private String auditFor;
	private Date lastRecalculation;

	private Date contractorConfirm;
	private Date auditorConfirm;
	private Date scheduledDate;
	private String auditLocation;
	private boolean needsCamera = false;
	private String contractorContact;
	private String address;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private float latitude;
	private float longitude;
	private String phone;
	private String phone2;

	private List<AuditCatData> categories = new ArrayList<AuditCatData>();
	private List<AuditData> data = new ArrayList<AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>();
	private List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractor) {
		this.contractorAccount = contractor;
	}

	@OneToMany(mappedBy = "conAudit", cascade = { CascadeType.REMOVE })
	@OrderBy("location")
	public List<OshaAudit> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaAudit> oshas) {
		this.oshas = oshas;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.REMOVE })
	public List<ContractorAuditOperator> getOperators() {
		return operators;
	}

	public void setOperators(List<ContractorAuditOperator> operators) {
		this.operators = operators;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.AuditStatus") })
	@Enumerated(EnumType.STRING)
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	/**
	 * Don't use this! Use changeStatus instead
	 * @param auditStatus
	 * @see ContractorAudit.changeStatus()
	 */
	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Transient
	public void changeStatus(AuditStatus auditStatus, User user) {
		// If we're changing the status to Submitted or Active, then we need
		// to set the dates
		if (auditStatus.isPending() || auditStatus.isIncomplete()) {
			if (closedDate != null)
				closedDate = null;
			if (completedDate != null)
				completedDate = null;
			if (getAuditType().isRenewable()) {
				expiresDate = null;
			}
		}
		if (auditStatus.isSubmitted() || auditStatus.isResubmitted()) {
			// If we're going "forward" then (re)set the completedDate
			completedDate = new Date();
			setExpirationDate();
		}
		if (auditStatus.isActive()) {
			// If we're going "forward" then (re)set the closedDate
			if (closedDate == null || this.auditStatus.isPendingSubmittedResubmitted())
				closedDate = new Date();

			if (completedDate == null)
				// If we're closed, there should always be a completedDate,
				// so fill it in if it hasn't already been set
				completedDate = closedDate;
			setExpirationDate();
		}
		if (auditStatus.isExempt()) {
			setExpirationDate();
		}
		if (auditStatus.isExpired()) {
			if (expiresDate == null)
				// This should never happen, but if it does, let's just cleanup the date
				expiresDate = new Date();
		}
		
		setAuditColumns(user);
		setAuditStatus(auditStatus);
	}
	
	private void setExpirationDate() {
		if (getExpiresDate() != null && !auditType.isRenewable())
			// Example: Desktop with expiration date already set
			return;
		Integer months = getAuditType().getMonthsToExpire();
		if (months != null && months > 0) {
			expiresDate = DateBean.addMonths(getCompletedDate(), months);
		} else {
			// check months first, then do date if empty
			expiresDate = DateBean.getMarchOfNextYear(new Date());
		}
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	@ManyToOne
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	@ManyToOne
	@JoinColumn(name = "closingAuditorID")
	public User getClosingAuditor() {
		return closingAuditor;
	}

	public void setClosingAuditor(User closingAuditor) {
		this.closingAuditor = closingAuditor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByOpID")
	public OperatorAccount getRequestingOpAccount() {
		return requestingOpAccount;
	}

	public void setRequestingOpAccount(OperatorAccount requestingOpAccount) {
		this.requestingOpAccount = requestingOpAccount;
	}

	@Transient
	public boolean isConductedOnsite() {
		// we should save auditLocation as a boolean in the DB
		if (auditLocation == null)
			return false;
		if (auditLocation.equals("Web"))
			return false;
		return true;
	}

	public String getAuditLocation() {
		return auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

	public boolean isNeedsCamera() {
		return needsCamera;
	}

	public void setNeedsCamera(boolean needsCamera) {
		this.needsCamera = needsCamera;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(int percentVerified) {
		this.percentVerified = percentVerified;
	}

	// Child tables

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<AuditCatData> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCatData> categories) {
		this.categories = categories;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<AuditData> getData() {
		return data;
	}

	public void setData(List<AuditData> data) {
		this.data = data;
	}

	// TRANSIENT ///////////////////////////////

	@Transient
	public int getPercent() {
		if (AuditStatus.Pending.equals(auditStatus))
			return this.percentComplete;
		if (AuditStatus.Submitted.equals(auditStatus))
			return this.percentVerified;
		if (AuditStatus.Resubmitted.equals(auditStatus))
			return this.percentVerified;

		return 100;
	}

	@Transient
	// I think we should move this to AuditActionSupport instead (Trevor 5/7/08)
	public boolean isCanView(Permissions permissions) {
		if (permissions.isContractor() && (getAuditType().isCanContractorView() == false))
			return false;
		else
			return true;
	}

	/**
	 * 
	 * 
	 * @return True if audit expires this year and it's before March 1
	 * @see willExpireSoon() they are basically the same thing
	 */
	@Transient
	public boolean isAboutToExpire() {
		if (expiresDate == null)
			return false;

		if(DateBean.getDateDifference(expiresDate) <= 60)
			return true;

		return false;
	}

	/**
	 * 
	 * @see isAboutToExpire() they are basically the same thing, 
	 * but we decided to keep both since isAboutToExpire accounts 
	 * for the year which is important when dealing with PQF class audits
	 */
	@Transient
	public boolean willExpireSoon() {
		int daysToExpiration = 0;
		if (getExpiresDate() == null)
			daysToExpiration = 1000;
		else
			daysToExpiration = DateBean.getDateDifference(expiresDate);

		if (getAuditType().getClassType() == AuditTypeClass.Policy) {
			return daysToExpiration <= 15;
		} else {
			return daysToExpiration <= 60;
		}
	}

	@Transient
	public Date getEffectiveDate() {
		if (getAuditType().getClassType() == AuditTypeClass.Policy)
			return creationDate;

		if (auditStatus.equals(AuditStatus.Exempt))
			return creationDate;

		if (auditStatus.equals(AuditStatus.Pending)) {
			if (auditor != null && assignedDate != null)
				return assignedDate;
			return creationDate;
		}
		if (auditStatus.equals(AuditStatus.Submitted) 
				|| (auditType.isDesktop() || auditType.getId() == AuditType.OFFICE))
			return completedDate;
		return closedDate;
	}

	@Transient
	public Date getValidDate() {
		if (auditType.getClassType().isPqf())
			return new Date();
		else
			return creationDate;
	}

	@Transient
	public boolean getExpiringPolicies() {
		if (expiresDate == null)
			return false;
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.WEEK_OF_YEAR, -2);
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.DATE, 26);
		if (expiresDate.after(cal1.getTime()) && expiresDate.before(cal2.getTime()))
			return true;
		return false;
	}

	@Transient
	public List<ContractorAuditOperator> getCurrentOperators() {
		List<ContractorAuditOperator> currentCaos = new ArrayList<ContractorAuditOperator>();

		for (ContractorAuditOperator cao : getOperators()) {
			// Check to see if the CAO is visible
			if (cao.isVisible()) {
				// BASF Corporate still needs insurance
				for (ContractorOperator co : getContractorAccount().getNonCorporateOperators()) {
					// Iterate over gencon tables
					// co.getOperatorAccount() == BASF Abbotsford that's
					// attached to Ancon Marine
					if (co.getOperatorAccount().getCanSeeInsurance().isTrue()
							&& co.getOperatorAccount().getInheritInsurance().equals(cao.getOperator())) {
						currentCaos.add(cao);
						break;
					}
				}
			}
		}

		Collections.sort(currentCaos, new Comparator<ContractorAuditOperator>() {
			@Override
			public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
				return o1.getOperator().compareTo(o2.getOperator());
			}
		});

		return currentCaos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getContractorConfirm() {
		return contractorConfirm;
	}

	public void setContractorConfirm(Date contractorConfirm) {
		this.contractorConfirm = contractorConfirm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAuditorConfirm() {
		return auditorConfirm;
	}

	public void setAuditorConfirm(Date auditorConfirm) {
		this.auditorConfirm = auditorConfirm;
	}

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	/**
	 * Who, what, or when is this audit for? Examples: OSHA/EMR for "2005" IM
	 * for "John Doe"
	 * 
	 * @return
	 */
	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public Date getLastRecalculation() {
		return lastRecalculation;
	}

	public void setLastRecalculation(Date lastRecalculation) {
		this.lastRecalculation = lastRecalculation;
	}

	@Transient
	public String getFullAddress() {
		if (Strings.isEmpty(address))
			return contractorAccount.getFullAddress();
		
		// We may want to extract this out and create a String address formatter
		StringBuffer full = new StringBuffer();
		full.append(address);
		if (!Strings.isEmpty(address2))
			full.append(" ").append(address2);
		if (!Strings.isEmpty(city))
			full.append(", ").append(city);
		if (!Strings.isEmpty(state))
			full.append(", ").append(state);
		if (!Strings.isEmpty(country) && !country.equals("US") && !country.startsWith("United"))
			full.append(", ").append(country);
		if (!Strings.isEmpty(zip))
			full.append(" ").append(zip);

		return full.toString();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getContractorContact() {
		return contractorContact;
	}

	public void setContractorContact(String contractorContact) {
		this.contractorContact = contractorContact;
	}

	@Transient
	public Location getLocation() {
		return new Location(latitude, longitude);
	}
	
	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Transient
	public String getStatusDescription() {
		String statusDescription = "";
		if (auditStatus.isActive())
			if (auditType.isMustVerify())
				if (auditType.isPqf() || auditType.isAnnualAddendum())
					statusDescription = "Annual requirements have been verified. " + this.getAuditType().getClassType()
							+ " is closed.";
				else
					statusDescription = this.getAuditType().getClassType() + " has been verified.";
			else if (auditType.isHasRequirements())
				statusDescription = "All the requirements for this " + this.getAuditType().getClassType().toString()
						+ " have been met. " + this.getAuditType().getClassType() + " closed.";
			else
				statusDescription = this.getAuditType().getClassType() + " closed.";

		if (auditStatus.isExempt())
			statusDescription = this.getAuditType().getClassType() + " is not required.";

		if (auditStatus.isExpired())
			statusDescription = this.getAuditType().getClassType() + " is no longer active.";

		if (auditStatus.isPending())
			if (auditType.isMustVerify())
				statusDescription = this.getAuditType().getClassType() + " has not been submitted.";
			else
				statusDescription = this.getAuditType().getClassType() + " has not been started.";

		if (auditStatus.isSubmitted())
			if(contractorAccount.isAcceptsBids()) {
				statusDescription = this.getAuditType().getClassType().toString()
				+ " has been submitted.";
			}
			else if (auditType.isMustVerify())
				statusDescription = this.getAuditType().getClassType().toString()
						+ " has been sent.  Awaiting verification.";
			else
				statusDescription = this.getAuditType().getClassType().toString()
						+ " has been submitted but there are requirements pending.";

		if (auditStatus.isResubmitted())
			statusDescription = "Policy updated; pending approval of changes.";

		if (auditStatus.isIncomplete())
			statusDescription = "Rejected " + this.getAuditType().getClassType() + " during verification";
		return statusDescription;
	}

	@Transient
	public String getPrintableScore() {
		int tempScore = Math.round(score);

		Map<Integer, String> map = new HashMap<Integer, String>() {
			{
				put(-1, "None");
				put(0, "Red");
				put(1, "Yellow");
				put(2, "Green");
			}
		};

		return map.get(tempScore);
	}
	
	@Transient
	public int getAuditorPayment() {
		if (auditType.isDesktop())
			return 75;
		if (auditType.getId() == AuditType.OFFICE) {
			if (isConductedOnsite())
				return 225;
			else
				return 175;
		}
		return 0;
	}
	

	public void setConductedOnsite(boolean conductedOnsite) {
		auditLocation = conductedOnsite ? "Onsite" : "Web";
	}
}
