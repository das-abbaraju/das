package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_competency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorCompetency extends BaseTable {

	private String category;
	private String label;
	private String description;
	private OperatorAccount operator;
	private String helpPage;
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
	
	public String getHelpPage() {
		return helpPage;
	}
	
	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	
}
