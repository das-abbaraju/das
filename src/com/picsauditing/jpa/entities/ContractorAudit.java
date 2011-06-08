package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.Location;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAudit extends BaseTable {

	private AuditType auditType;
	private ContractorAccount contractorAccount;
	private Date expiresDate;
	private Date effectiveDate;
	private User auditor;
	private User closingAuditor;
	private Date assignedDate;
	private Date paidDate;
	private OperatorAccount requestingOpAccount;
	private int score;
	private boolean manuallyAdded;
	protected boolean needsIndexing = true;
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
	private Integer ruleID;

	private List<AuditCatData> categories = new ArrayList<AuditCatData>();
	private List<AuditData> data = new ArrayList<AuditData>();
	private List<OshaAudit> oshas = new ArrayList<OshaAudit>();
	private List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
	private Map<AuditStatus, Integer> caoStats = null;

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

	@OneToMany(mappedBy = "conAudit", cascade = { CascadeType.ALL })
	@OrderBy("location")
	public List<OshaAudit> getOshas() {
		return oshas;
	}

	public void setOshas(List<OshaAudit> oshas) {
		this.oshas = oshas;
	}

	@OneToMany(mappedBy = "audit", cascade = { CascadeType.ALL })
	public List<ContractorAuditOperator> getOperators() {
		return operators;
	}

	// TODO replace old uses of getOperators with sorted one
	@Transient
	public List<ContractorAuditOperator> getSortedOperators() {
		List<ContractorAuditOperator> caos = operators;
		Collections.sort(caos, getComparator());
		return caos;
	}

	public void setOperators(List<ContractorAuditOperator> operators) {
		this.operators = operators;
	}

	@Transient
	public List<ContractorAuditOperator> getOperatorsVisible() {
		return new Grepper<ContractorAuditOperator>() {

			public boolean check(ContractorAuditOperator t) {
				return t.isVisible();
			}
		}.grep(this.operators);
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Transient
	public boolean isExpired() {
		if (expiresDate == null)
			return false;
		return expiresDate.before(new Date());
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

	/**
	 * We may need to move this over to CAO someday
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
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

		if (DateBean.getDateDifference(expiresDate) <= 60)
			return true;

		return false;
	}

	/**
	 * 
	 * @see isAboutToExpire() they are basically the same thing, but we decided
	 *      to keep both since isAboutToExpire accounts for the year which is
	 *      important when dealing with PQF class audits
	 */
	@Transient
	public boolean willExpireSoon() {
		int daysToExpiration = 0;
		if (getExpiresDate() == null)
			daysToExpiration = 1000;
		else
			daysToExpiration = DateBean.getDateDifference(expiresDate);

		if (auditType.getClassType() == AuditTypeClass.Policy) {
			return daysToExpiration <= 15;
		} else if (auditType.getId() == AuditType.COR) {
			return daysToExpiration <= 45;
		} else {
			return daysToExpiration <= 60;
		}
	}

	@Transient
	public Date getValidDate() {
		if (auditType.isAnnualAddendum())
			return effectiveDate;
		if (hasCaoStatusAfter(AuditStatus.Incomplete)) {
			if (effectiveDate == null)
				return new Date();
			else
				return effectiveDate;
		} else
			return new Date();
	}

	@Transient
	public Date getEffectiveDateLabel() {
		if (this.auditType.isDesktop() && effectiveDate != null) {
			return effectiveDate;
		}
		for (ContractorAuditOperator cao : operators) {
			if (cao.getStatus().equals(AuditStatus.Complete))
				return cao.getStatusChangedDate();
		}
		for (ContractorAuditOperator cao : operators) {
			if (cao.getStatus().after(AuditStatus.Incomplete))
				return cao.getStatusChangedDate();
		}
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
					if (cao.hasCaop(co.getOperatorAccount().getId())) {
						currentCaos.add(cao);
						break;
					}
				}
			}
		}

		Collections.sort(currentCaos, getComparator());

		return currentCaos;
	}

	@Transient
	public List<ContractorAuditOperator> getViewableOperators(Permissions permissions) {
		List<ContractorAuditOperator> currentCaos = new ArrayList<ContractorAuditOperator>();

		for (ContractorAuditOperator cao : getOperators()) {
			if (cao.isVisibleTo(permissions)) {
				currentCaos.add(cao);
			}
		}

		Collections.sort(currentCaos, getComparator());
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

	public Integer getRuleID() {
		return ruleID;
	}

	public void setRuleID(Integer ruleID) {
		this.ruleID = ruleID;
	}

	public static Comparator<ContractorAuditOperator> getComparator() {
		return new Comparator<ContractorAuditOperator>() {

			public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
				if (o1.getOperator().getId() < 10)
					return new Integer(o1.getOperator().getId()).compareTo(new Integer(o2.getOperator().getId()));
				return o1.getOperator().getName().compareTo(o2.getOperator().getName());
			}
		};
	}

	@Transient
	public String getPrintableScore() {
		String tempScore = "";

		if (score <= 0)
			tempScore = "None";
		if (score < 50)
			tempScore = "Red";
		else if (score < 100)
			tempScore = "Yellow";
		else
			tempScore = "Green";

		return tempScore;
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

	@Transient
	public String getIndexType() {
		return "AU";
	}

	@Transient
	public boolean isCategoryApplicable(int catID) {
		for (AuditCatData acd : this.categories) {
			if (acd.getCategory().getId() == catID && acd.isApplies())
				return true;
		}
		return false;
	}

	@Transient
	public ContractorAuditOperator getCao(OperatorAccount operator) {
		return getCao(operator.getOperatorHeirarchy());
	}

	@Transient
	public ContractorAuditOperator getCao(List<Integer> sortedCaoOperatorCandidates) {
		for (Integer parent : sortedCaoOperatorCandidates) {
			for (ContractorAuditOperator cao : this.operators) {
				if (cao.getOperator().getId() == parent)
					return cao;
			}
		}
		for (ContractorAuditOperator cao : this.operators) {
			if (cao.getOperator().getId() == OperatorAccount.PicsConsortium)
				return cao;
		}
		return null;
	}

	@Transient
	public boolean hasCaoStatus(AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : this.operators) {
			if (cao.isVisible() && cao.getStatus().equals(auditStatus))
				return true;
		}
		return false;
	}

	@Transient
	public boolean hasCaoStatusBefore(AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : this.operators) {
			if (cao.isVisible() && cao.getStatus().before(auditStatus))
				return true;
		}
		return false;
	}

	@Transient
	public boolean hasCaoStatusAfter(AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : this.operators) {
			if (cao.isVisible() && cao.getStatus().after(auditStatus))
				return true;
		}
		return false;
	}

	@Transient
	public boolean isVisibleTo(Permissions permissions) {
		if (permissions.isContractor())
			return getAuditType().isCanContractorView();

		if (permissions.isPicsEmployee())
			return true;

		for (ContractorAuditOperator cao : getOperators()) {
			if (cao.isVisibleTo(permissions)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasOnlyInvisibleCaos() {
		if (this.operators.size() > 0 && this.getOperatorsVisible().size() == 0)
			return true;
		return false;
	}

	@Transient
	public Map<AuditCategory, AuditCatData> getApplicableCategories(Permissions permissions,
			Set<AuditCategory> requiredCategories) {
		Map<AuditCategory, AuditCatData> categories = new LinkedHashMap<AuditCategory, AuditCatData>();
		List<AuditCatData> sortedCats = getCategories();
		Collections.sort(sortedCats, new Comparator<AuditCatData>() {
			@Override
			public int compare(AuditCatData o1, AuditCatData o2) {
				return ((Integer) o1.getCategory().getNumber()).compareTo(o2.getCategory().getNumber());
			}
		});

		for (AuditCatData auditCatData : sortedCats) {
			boolean add = false;
			if (!permissions.isContractor() && (auditCatData.getCategory().getId() == AuditCategory.WORK_HISTORY)) {
				if (permissions.hasPermission(OpPerms.ViewFullPQF))
					add = true;
			} else if (permissions.isAdmin() || permissions.isAuditor()) {
				add = true;
			} else {
				if (auditCatData.isApplies()) {
					if (permissions.isOperatorCorporate()) {
						if (requiredCategories.contains(auditCatData.getCategory()))
							add = true;
					} else
						add = true;
				}
			}
			if (add)
				categories.put(auditCatData.getCategory(), auditCatData);
		}

		return categories;
	}

	@Transient
	public Map<AuditStatus, Integer> getCaoStats(Permissions permissions) {
		if (caoStats == null) {
			caoStats = new TreeMap<AuditStatus, Integer>();
			for (ContractorAuditOperator cao : operators) {
				if (cao.isVisibleTo(permissions)) {
					if (caoStats.get(cao.getStatus()) == null)
						caoStats.put(cao.getStatus(), 1);
					else
						caoStats.put(cao.getStatus(), caoStats.get(cao.getStatus()) + 1);
				}
			}
		}
		return caoStats;
	}

	@Transient
	public Integer getIndependentClosingAuditor(User u) {
		if (u != null) {
			if (u.getId() == 10600) // Mike Casey
				return 34067; // James Smith
			else if (u.getId() == 910) // Dennis Dooley
				return 34067; // James Smith
			else if (u.getId() == 902) // John McCaughey
				return 1029; // Mina Mina
			else
				return u.getId();
		}
		return null;
	}
}
