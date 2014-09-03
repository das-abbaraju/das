package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditOptionValue")
@Table(name = "audit_option_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class DocumentOptionValue extends BaseTable {

	private DocumentOptionGroup group;
	private String uniqueCode;
	private int score = 0;

	@ManyToOne
	@JoinColumn(name = "typeID", nullable = false)
	public DocumentOptionGroup getGroup() {
		return group;
	}

	public void setGroup(DocumentOptionGroup group) {
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