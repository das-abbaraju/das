package com.picsauditing.flagcalculator.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAuditOperatorPermission")
@Table(name = "contractor_audit_operator_permission")
public class ContractorAuditOperatorPermission implements Serializable {

    private int id;
    private ContractorAuditOperator cao;
    private OperatorAccount operator;

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
}