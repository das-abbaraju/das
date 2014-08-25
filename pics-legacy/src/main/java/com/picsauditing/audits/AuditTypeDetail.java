package com.picsauditing.audits;

import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;

import java.util.HashSet;
import java.util.Set;

public class AuditTypeDetail {
    /**
     * The AuditTypeRule that is responsible for including this auditType for this contractor
     */
    public AuditTypeRule rule;
    /**
     * Operator Accounts that require this audit (CAOPs)
     */
    public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
}
