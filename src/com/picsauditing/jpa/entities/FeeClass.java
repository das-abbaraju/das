package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.util.SpringUtils;

public enum FeeClass implements Translatable {
	// TODO combine some of these fees
	Deprecated,
	Free,
	BidOnly,
	ListOnly,
	DocuGUARD,
	InsureGUARD {
		@Override
		public boolean isExcludedFor(ContractorAccount contractor) {
			if (contractor == null || contractor.getOperatorAccounts().isEmpty())
				return false;

			if (new Date().before(BASFInsureGUARDPricingEffectiveDate)
					&& contractor.isAssociatedExclusivelyWith(OperatorAccount.BASF))
				return true;

			if (new Date().before(AIAndOldcasteInsureGUARDPricingEffectiveDate)
					&& (contractor.isAssociatedExclusivelyWith(OperatorAccount.AI) || contractor
							.isAssociatedExclusivelyWith(OperatorAccount.Oldcastle)))
				return true;

			return false;
		}

		@Override
		public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
			for (OperatorAccount operator : contractor.getOperatorAccounts()) {
				if (operator.getTopAccount().getId() != OperatorAccount.BASF) {
					return fee.getAmount();
				}
			}

			return new BigDecimal(299).setScale(2);
		}
	},
	AuditGUARD {
		@Override
		public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
			if (contractor.getPayingFacilities() == 1) {
				Date now = new Date();
				if (BillingCalculatorSingle.CONTRACT_RENEWAL_BASF.after(now)) {
					for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
						if (contractorOperator.getOperatorAccount().getName().startsWith("BASF")) {
							return new BigDecimal(299).setScale(2);
						}
					}
				}
			}

			return fee.getAmount();
		}
	},
	EmployeeGUARD {
		@Override
		public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
			AuditTypeRuleCache ruleCache = (AuditTypeRuleCache) SpringUtils.getBean("AuditTypeRuleCache");
			AuditDecisionTableDAO auditDAO = (AuditDecisionTableDAO) SpringUtils.getBean("AuditDecisionTableDAO");
			ruleCache.initialize(auditDAO);
			AuditTypesBuilder builder = new AuditTypesBuilder(ruleCache, contractor);

			boolean employeeAudits = false;
			boolean oq = false;
			boolean hseCompetency = false;

			for (AuditTypeDetail detail : builder.calculate()) {
				AuditType auditType = detail.rule.getAuditType();
				if (auditType == null)
					continue;
				if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isEmployee()
						|| auditType.getClassType().isIm())
					employeeAudits = true;
				if (auditType.getId() == AuditType.HSE_COMPETENCY)
					hseCompetency = true;
			}

			for (ContractorOperator co : contractor.getOperators()) {
				if (co.getOperatorAccount().isRequiresOQ())
					oq = true;
			}

			if (!hseCompetency && (employeeAudits || oq))
				return BigDecimal.ZERO;

			return fee.getAmount();
		}
	},
	Activation,
	Reactivation,
	LateFee,
	ReschedulingFee,
	ScanningFee,
	WebcamFee,
	ExpediteFee,
	ImportFee,
	SuncorDiscount,
	GST,
	Misc;

	private static final Date BASFInsureGUARDPricingEffectiveDate = DateBean.parseDate("2012-02-01");
	private static final Date AIAndOldcasteInsureGUARDPricingEffectiveDate = DateBean.parseDate("2013-01-01");

	public boolean isPaymentExpiresNeeded() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == Activation || this == Reactivation;
	}

	public boolean isMembership() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == AuditGUARD || this == EmployeeGUARD
				|| this == InsureGUARD;
	}

	public boolean isExcludedFor(ContractorAccount contractor) {
		return false;
	}

	public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
		return fee.getAmount();
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
