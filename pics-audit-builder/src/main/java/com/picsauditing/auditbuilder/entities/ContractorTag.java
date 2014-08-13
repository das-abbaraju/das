package com.picsauditing.auditbuilder.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorTag")
@Table(name = "contractor_tag")
public class ContractorTag extends BaseTable implements java.io.Serializable {

	OperatorTag tag;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tagID", nullable = false, updatable = false)
	public OperatorTag getTag() {
		return tag;
	}

	public void setTag(OperatorTag tag) {
		this.tag = tag;
	}
}