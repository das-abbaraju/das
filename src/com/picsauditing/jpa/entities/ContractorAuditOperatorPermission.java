package com.picsauditing.jpa.entities;

import com.picsauditing.jpa.entities.builders.ContractorAuditOperatorPermissionBuilder;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator_permission")
public class ContractorAuditOperatorPermission implements Serializable {

	private int id;
	private ContractorAuditOperator cao;
	private OperatorAccount operator;
	private ContractorAuditOperator previousCao;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "caoID")
	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "previousCaoID")
	public ContractorAuditOperator getPreviousCao() {
		return previousCao;
	}

	public void setPreviousCao(ContractorAuditOperator previousCao) {
		this.previousCao = previousCao;
	}

    public static ContractorAuditOperatorPermissionBuilder builder() {
        return new ContractorAuditOperatorPermissionBuilder();
    }
}
