package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditType extends BaseTable implements Comparable<AuditType>, java.io.Serializable {
	public static final int PQF = 1;
	public static final int DESKTOP = 2;
	public static final int OFFICE = 3;
	public static final int NCMS = 4;
	public static final int DA = 6;
	public static final int WELCOME = 9;
	public static final int ANNUALADDENDUM = 11;
	public static final int HUNTSMAN_EBIX = 31;
	public static final int COR = 72;
	public static final int SUPPLEMENTCOR = 84;
	public static final int BPIISNSPECIFIC = 87;
	public static final int BPIISNCASEMGMT = 96;
	
	public static String DEFAULT_AUDITTYPE = "- Audit -";

	public AuditType() {
	}

	public AuditType(String name) {
		this.auditName = name;
	}

	public AuditType(int id) {
		this.id = id;
	}

	protected String auditName;
	protected AuditTypeClass classType = AuditTypeClass.Audit;
	protected int displayOrder = 100;
	protected String description;
	protected boolean hasMultiple;
	protected boolean isScheduled;
	protected boolean hasAuditor;
	protected boolean hasRequirements;
	protected boolean canContractorView;
	protected boolean canContractorEdit;
	protected Integer monthsToExpire;
	protected boolean mustVerify;
	protected boolean renewable = true;
	protected Account account;

	protected List<AuditCategory> categories = new ArrayList<AuditCategory>();

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	@Enumerated(EnumType.STRING)
	public AuditTypeClass getClassType() {
		return classType;
	}

	public void setClassType(AuditTypeClass classType) {
		this.classType = classType;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * More than one audit of this type can be active for a contractor at a time
	 * @return
	 */
	public boolean isHasMultiple() {
		return hasMultiple;
	}

	public void setHasMultiple(boolean hasMultiple) {
		this.hasMultiple = hasMultiple;
	}

	@Column(name = "isScheduled")
	public boolean isScheduled() {
		return isScheduled;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public boolean isHasAuditor() {
		return hasAuditor;
	}

	public void setHasAuditor(boolean hasAuditor) {
		this.hasAuditor = hasAuditor;
	}

	public boolean isHasRequirements() {
		return hasRequirements;
	}

	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
	}

	public boolean isCanContractorView() {
		return canContractorView;
	}

	public void setCanContractorView(boolean canContractorView) {
		this.canContractorView = canContractorView;
	}

	public boolean isCanContractorEdit() {
		return canContractorEdit;
	}

	public void setCanContractorEdit(boolean canContractorEdit) {
		this.canContractorEdit = canContractorEdit;
	}

	public Integer getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(Integer monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	public boolean isMustVerify() {
		return mustVerify;
	}

	public void setMustVerify(boolean mustVerify) {
		this.mustVerify = mustVerify;
	}
	
	/**
	 * Can the existing audit be renewed (aka extended aka resubmitted)?
	 * <br>Examples of true: PQF, Site Specific Audits
	 * <br>Examples of false: Desktop, Office, Field, Policies
	 * @return
	 */
	public boolean isRenewable() {
		return renewable;
	}

	public void setRenewable(boolean renewable) {
		this.renewable = renewable;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = true)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@OneToMany(mappedBy = "auditType")
	@OrderBy("number")
	public List<AuditCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategory> categories) {
		this.categories = categories;
	}

	/**
	 * Return the name of the icon we use on reports for each audit type
	 * 
	 * @param auditTypeID
	 * @return
	 */
	public static String getIcon(int auditTypeID) {
		String auditType = "";
		if (auditTypeID == AuditType.PQF)
			auditType = "PQF";
		if (auditTypeID == AuditType.DESKTOP)
			auditType = "Desktop";
		if (auditTypeID == AuditType.OFFICE)
			auditType = "Office";
		if (auditTypeID == AuditType.DA)
			auditType = "DA";
		return "icon_" + auditType + ".gif";
	}

	@Transient
	public boolean isShowManual() {
		if (this.id == OFFICE)
			return true;
		if (this.id == DESKTOP)
			return true;
		if(this.id == BPIISNCASEMGMT)
			return true;
		return false;
	}

	@Transient
	public boolean isPqf() {
		return (id == PQF);
	}

	@Transient
	public boolean isDesktop() {
		return (id == DESKTOP);
	}

	@Transient
	public boolean isAnnualAddendum() {
		return (id == ANNUALADDENDUM);
	}

	@Transient
	public boolean isDynamicCategories() {
		if (isPqf())
			return true;
		if (id == DESKTOP)
			return true;
		return false;
	}

	@Override
	public int compareTo(AuditType o) {
		if(!this.getClassType().equals(o.getClassType())) {
			return getClassType().ordinal() - o.getClassType().ordinal();
		}
		
		int i = new Integer(this.getDisplayOrder()).compareTo(new Integer(o.getDisplayOrder()));
		if (i == 0)
			return new Integer(this.getId()).compareTo(new Integer(o.getId()));
		
		return i;
	}

	@Override
	public String toString() {
		return auditName + "(" + id + ")";
	}
}
