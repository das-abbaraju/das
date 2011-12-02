package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.picsauditing.report.QueryBase;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private QueryBase base;
	private String summary;
	private String description;
	private String parameters;
	private Account sharedWith;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public QueryBase getBase() {
		return base;
	}

	public void setBase(QueryBase base) {
		this.base = base;
	}

	@Column(nullable = false)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@ManyToOne
	@JoinColumn(name = "sharedWith")
	public Account getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(Account sharedWith) {
		this.sharedWith = sharedWith;
	}
}
