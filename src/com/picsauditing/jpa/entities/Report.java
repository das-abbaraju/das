package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.picsauditing.report.models.ModelType;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	private String summary;
	private String description;
	private String parameters;
	private Account sharedWith;
	private String devParams;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType type) {
		this.modelType = type;
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

	public String getDevParams() {
		return devParams;
	}

	public void setDevParams(String devParams) {
		this.devParams = devParams;
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
