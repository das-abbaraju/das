package com.picsauditing.flagcalculator.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorAuditOperatorPermission")
@Table(name = "contractor_audit_operator_permission")
public class ContractorAuditOperatorPermission implements Serializable {
<<<<<<< HEAD

    private int id;
    private ContractorAuditOperator cao;
    private OperatorAccount operator;
//    private ContractorAuditOperator previousCao;
//
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
=======
    private OperatorAccount operator;
>>>>>>> 7ae760b... US831 Deprecated old FDC

    @ManyToOne
    @JoinColumn(name = "opID")
    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }
}