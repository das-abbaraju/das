package com.picsauditing.jpa.entities;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.util.SpringUtils;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * The business logic in FeeClass needs to be pulled out into a service to make
 * it properly testable.
 *
 * @author TJB
 *
 */
public enum FeeClass implements Translatable {

	// TODO combine some of these fees
	Deprecated, Free, BidOnly, ListOnly, DocuGUARD, InsureGUARD {
		@Override
		public boolean isExcludedFor(ContractorAccount contractor, InvoiceFee newLevel, Set<OperatorAccount> operators) {
			if (contractor == null || contractor.getOperatorAccounts().isEmpty()) {
				return false;
			}

			if (contractor.getAccountLevel().isListOnly() || contractor.getAccountLevel().isBidOnly()) {
				return true;
			}

			if (contractor.getLastUpgradeDate() != null
					&& contractor.getLastUpgradeDate().before(InsureGUARDPricingEffectiveDate)
					&& contractor.getFees().get(newLevel.getFeeClass()).willBeUpgradedBy(newLevel)) {
				return true;
			}

			if (contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR) && contractor.getSoleProprietor()) {
				return true;
			}

			return isAllExclusionsApplicable(contractor, newLevel, operators);
		}

	},
	AuditGUARD, EmployeeGUARD {
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
				if (auditType == null) {
					continue;
				}
				if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isEmployee()
						|| auditType.getClassType().isIm()) {
					employeeAudits = true;
				}
				if (auditType.getId() == AuditType.HSE_COMPETENCY) {
					hseCompetency = true;
				}
			}

			for (ContractorOperator co : contractor.getOperators()) {
				if (co.getOperatorAccount().isRequiresOQ()) {
					oq = true;
				}
			}

			if (!hseCompetency && (employeeAudits || oq)) {
				return BigDecimal.ZERO;
			}

			return contractor.getCountry().getAmount(fee);
		}
	},
	Reviews, Activation {
		@Override
		public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
			Set<BigDecimal> discounts = new HashSet<BigDecimal>();
			for (OperatorAccount operator : contractor.getOperatorAccounts()) {
				if (operator.isHasDiscount()) {
					discounts.add(operator.getDiscountPercent());
				} else {
					OperatorAccount inheritedDiscountPercentOperator = operator.getInheritedDiscountPercentOperator();
					if (inheritedDiscountPercentOperator != null) {
						discounts.add(inheritedDiscountPercentOperator.getDiscountPercent());
					} else {
						return contractor.getCountry().getAmount(fee);
					}
				}
			}

			// This happens if there are no operators attached to this
			// contractor.
			// Unlikely, but if it never happened, I wouldn't be writing this.
			if (discounts.isEmpty()) {
				return contractor.getCountry().getAmount(fee);
			}

			BigDecimal minimumDiscount = Collections.min(discounts);
			minimumDiscount = BigDecimal.ONE.subtract(minimumDiscount);
			return contractor.getCountry().getAmount(fee).multiply(minimumDiscount).setScale(0, BigDecimal.ROUND_DOWN);
		}
	},
	Reactivation, LateFee, ReschedulingFee, ScanningFee, WebcamFee, ExpediteFee, ImportFee, SuncorDiscount, GST, CanadianTax, VAT, Misc;

	// after 2013-01-01, we can remove the insureGuard date-based pricing logic.
	private static final Date InsureGUARDPricingEffectiveDate = DateBean.parseDate("2012-01-01");
	private static final Date Jan2013InsureGUARDPricingEffectiveDate = DateBean.parseDate("2013-01-01");
	private static final Date BASFInsureGUARDAndAuditGUARDPricingEffectiveDate = DateBean.parseDate("2012-02-04");
	private static final Date SuncorInsureGUARDPricingEffectiveDate = DateBean.parseDate("2012-02-01");

	public boolean isPaymentExpiresNeeded() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == Activation || this == Reactivation;
	}

	public boolean isMembership() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == AuditGUARD || this == EmployeeGUARD
				|| this == InsureGUARD;
	}

	public boolean isExcludedFor(ContractorAccount contractor, InvoiceFee newLevel, Set<OperatorAccount> operators) {
		return false;
	}

	public BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
		return contractor.getCountry().getAmount(fee);
	}

	@Override
	public String getI18nKey() {
		return (!getClass().getSimpleName().isEmpty() ? getClass().getSimpleName() : getClass().getSuperclass()
				.getSimpleName()) + "." + this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	// TODO: This probably needs to be refactored into rules as it continues to
	// grow
	public boolean isAllExclusionsApplicable(ContractorAccount contractor, InvoiceFee newLevel,
			Set<OperatorAccount> operators) {
		boolean isUpgrade = false;
        if (contractor.getFees() != null && newLevel.getFeeClass() != null && contractor.getFees().containsKey(newLevel.getFeeClass())) {
            isUpgrade = contractor.getFees().get(newLevel.getFeeClass()).willBeUpgradedBy(newLevel);
        }
		Map<Integer, Date> exclusions = getExclusions();

		for (OperatorAccount operator : operators) {
			Date exclusionExpirationDate = exclusions.get(operator.getTopAccount().getId());

			// do I have an operator outside the exclusions list?
			if (!exclusions.containsKey(operator.getTopAccount().getId())) {
				return false;
				// is it time to start charging this operator for insureguard?
			} else if (!isUpgrade && new Date().after(exclusionExpirationDate)) {
				return false;
			} else if ((isUpgrade || !contractor.getStatus().isActiveOrDemo())
					&& (contractor.getLastUpgradeDate() == null || contractor.getLastUpgradeDate().after(
							exclusionExpirationDate))) {
				return false;
			}
		}

		return true;
	}

	public static List<String> getCommissionableServiceLines() {
		List<String> commissionableServiceLines = new ArrayList<String>();
		commissionableServiceLines.add("All");
		commissionableServiceLines.add(AuditGUARD.name());
		commissionableServiceLines.add(EmployeeGUARD.name());
		commissionableServiceLines.add(InsureGUARD.name());

		return commissionableServiceLines;
	}

	protected Map<Integer, Date> getExclusions() {
		Map<Integer, Date> exclusions = new HashMap<Integer, Date>();
		exclusions.put(OperatorAccount.BASF, BASFInsureGUARDAndAuditGUARDPricingEffectiveDate);
		exclusions.put(OperatorAccount.AI, Jan2013InsureGUARDPricingEffectiveDate);
		exclusions.put(OperatorAccount.OLDCASTLE, Jan2013InsureGUARDPricingEffectiveDate);
		exclusions.put(OperatorAccount.SUNCOR, SuncorInsureGUARDPricingEffectiveDate);
		exclusions.put(OperatorAccount.SUNOCO, Jan2013InsureGUARDPricingEffectiveDate);
		return exclusions;
	}

	public static Set<FeeClass> getContractorPriceTableFeeTypes() {
		Set<FeeClass> feeTypes = new HashSet<FeeClass>();

		feeTypes.add(DocuGUARD);
		feeTypes.add(AuditGUARD);
		feeTypes.add(EmployeeGUARD);
		feeTypes.add(Activation);
		feeTypes.add(InsureGUARD);

		return feeTypes;
	}

	public static String getContractorPriceTableFeeTypesForDatabase() {
		Set<FeeClass> feeClasses = getContractorPriceTableFeeTypes();

		String feeTypesForDatabase = "";
		for (FeeClass feeClass : feeClasses) {
			if (StringUtils.isNotEmpty(feeTypesForDatabase)) {
				feeTypesForDatabase += ",";
			}

			feeTypesForDatabase += "'" + feeClass.toString() + "'";
		}

		return feeTypesForDatabase;
	}
}
