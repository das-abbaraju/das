package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "audit_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class AuditType implements Comparable<AuditType> {
	public static final int PQF = 1;
	public static final int DESKTOP = 2;
	public static final int OFFICE = 3;
	public static final int NCMS = 4;
	public static final int DA = 6;
	public static final int WELCOME = 9;
	public static final int ANNUALADDENDUM = 11;

	public static String DEFAULT_AUDITTYPE = "- Audit -";

	public AuditType() {
	}

	public AuditType(String name) {
		this.auditName = name;
	}

	public AuditType(int id) {
		this.auditTypeID = id;
	}

	protected int auditTypeID;

	protected String auditName;
	protected String description;
	protected boolean hasMultiple;
	protected boolean isScheduled;
	protected boolean hasAuditor;
	protected boolean hasRequirements;
	protected boolean canContractorView;
	protected boolean canContractorEdit;
	protected Integer monthsToExpire;
	@Deprecated
	protected Date dateToExpire;
	protected int displayOrder = 100;

	protected List<AuditCategory> categories = new ArrayList<AuditCategory>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	@Temporal(value = TemporalType.DATE)
	public Date getDateToExpire() {
		return dateToExpire;
	}

	public void setDateToExpire(Date dateToExpire) {
		this.dateToExpire = dateToExpire;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
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
		if (this.auditTypeID == OFFICE)
			return true;
		if (this.auditTypeID == DESKTOP)
			return true;
		return false;
	}

	@Transient
	public boolean isPqf() {
		return (auditTypeID == PQF);
	}

	@Transient
	public boolean isDynamicCategories() {
		if (isPqf())
			return true;
		if (auditTypeID == DESKTOP)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + auditTypeID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditType other = (AuditType) obj;
		if (auditTypeID != other.auditTypeID)
			return false;
		return true;
	}

	@Override
	public int compareTo(AuditType o) {
		return (this.displayOrder - o.displayOrder);
	}

}
