package com.picsauditing.jpa.entities;

import com.picsauditing.jpa.entities.builders.AuditTypeBuilder;
import com.picsauditing.jpa.entities.builders.AuditTypeRuleBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type_rule")
public class AuditTypeRule extends AuditRule {

	private AuditType dependentAuditType;
	private AuditStatus dependentAuditStatus;
	private boolean manuallyAdded = false;

	@ManyToOne
	@JoinColumn(name = "dependentAuditTypeID")
	public AuditType getDependentAuditType() {
		return dependentAuditType;
	}

	@Transient
	public String getDependentAuditTypeLabel() {
		return dependentAuditType == null ? "*" : dependentAuditType.toString();
	}

	public void setDependentAuditType(AuditType dependentAuditType) {
		this.dependentAuditType = dependentAuditType;
	}

	@Enumerated(EnumType.STRING)
	public AuditStatus getDependentAuditStatus() {
		return dependentAuditStatus;
	}

	@Transient
	public String getDependentAuditStatusLabel() {
		return dependentAuditStatus == null ? "*" : dependentAuditStatus.toString();
	}

	public void setDependentAuditStatus(AuditStatus dependentAuditStatus) {
		this.dependentAuditStatus = dependentAuditStatus;
	}

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	public void update(AuditRule source) {
		super.update(source);
		dependentAuditType = ((AuditTypeRule) source).dependentAuditType;
		dependentAuditStatus = ((AuditTypeRule) source).dependentAuditStatus;
		manuallyAdded = ((AuditTypeRule) source).manuallyAdded;
	}

	@Override
	public void calculatePriority() {
		super.calculatePriority();
		if (dependentAuditType != null) {
			level++;
			priority += 108;
		}
	}

	@Override
	public String toString() {
		String out = super.toString();
		if (dependentAuditType != null) {
			if (!out.contains("when"))
				out += " when";
			else
				out += " and";
			out += " Dependent Audit Type is " + dependentAuditType
					+ (dependentAuditStatus == null ? "" : " is " + dependentAuditStatus);
		}
		if (manuallyAdded)
			out += " [MANUALLY added]";
		return out;
	}

    public static AuditTypeRuleBuilder builder() {
        return new AuditTypeRuleBuilder();
    }
}
