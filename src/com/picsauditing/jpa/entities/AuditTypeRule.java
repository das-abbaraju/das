package com.picsauditing.jpa.entities;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.actions.auditType.AuditRuleColumn;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type_rule")
public class AuditTypeRule extends AuditRule implements AuditRuleTable {

	private AuditType dependentAuditType;
	private AuditStatus dependentAuditStatus;

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

	public void update(AuditRule source) {
		super.update(source);
		dependentAuditType = ((AuditTypeRule) source).dependentAuditType;
		dependentAuditStatus = ((AuditTypeRule) source).dependentAuditStatus;
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
		if (dependentAuditType != null)
			out += " when AuditType " + dependentAuditType + " is " + dependentAuditStatus;
		return out;
	}

	@Override
	@Transient
	public Map<AuditRuleColumn, List<String>> getMapping() {
		Map<AuditRuleColumn, List<String>> map = super.getMapping();

		if (getDependentAuditType() != null) {
			map.get(AuditRuleColumn.DependentAudit).add(getDependentAuditTypeLabel());
			map.get(AuditRuleColumn.DependentAudit).add(getDependentAuditStatusLabel());
		}

		return map;
	}
}
