package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorAuditOperatorPermission")
@Table(name = "contractor_audit_operator_permission")
public class ContractorDocumentOperatorPermission implements Serializable {

    private int id;
	private ContractorDocumentOperator cao;
	private OperatorAccount operator;
	private ContractorDocumentOperator previousCao;

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
	public ContractorDocumentOperator getCao() {
		return cao;
	}

	public void setCao(ContractorDocumentOperator cao) {
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
	public ContractorDocumentOperator getPreviousCao() {
		return previousCao;
	}

	public void setPreviousCao(ContractorDocumentOperator previousCao) {
		this.previousCao = previousCao;
	}
}