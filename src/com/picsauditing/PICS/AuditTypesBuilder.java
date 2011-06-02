package com.picsauditing.PICS;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditTypesBuilder {

	public class AuditTypeDetail {

		public AuditTypeRule rule;
		/**
		 * Operator Accounts, not corporate, may be the same as the CAO
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}
}
