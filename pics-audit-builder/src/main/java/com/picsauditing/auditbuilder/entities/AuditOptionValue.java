package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_option_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditOptionValue extends BaseTable {

	private AuditOptionGroup group;
	private String uniqueCode;
	private int score = 0;

	@ManyToOne
	@JoinColumn(name = "typeID", nullable = false)
	public AuditOptionGroup getGroup() {
		return group;
	}

	public void setGroup(AuditOptionGroup group) {
		this.group = group;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(nullable = false)
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}