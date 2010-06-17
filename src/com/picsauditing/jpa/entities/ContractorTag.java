package com.picsauditing.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_tag")
public class ContractorTag extends BaseTable implements java.io.Serializable {
	ContractorAccount contractor;
	OperatorTag tag;

	@ManyToOne(optional = false)
	@JoinColumn(name = "conID", nullable = false, updatable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "tagID", nullable = false, updatable = false)
	public OperatorTag getTag() {
		return tag;
	}

	public void setTag(OperatorTag tag) {
		this.tag = tag;
	}

}
