package com.picsauditing.jpa.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("C")
public class AuditCategoryMatrixCompetencies extends AuditCategoryMatrix {
	private OperatorCompetency operatorCompetency;

	@ManyToOne
	@JoinColumn(name = "foreignKeyID")
	public OperatorCompetency getOperatorCompetency() {
		return operatorCompetency;
	}

	public void setOperatorCompetency(OperatorCompetency operatorCompetency) {
		this.operatorCompetency = operatorCompetency;
	}
}
