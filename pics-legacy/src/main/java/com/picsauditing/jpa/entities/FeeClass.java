package com.picsauditing.jpa.entities;

import com.picsauditing.PICS.DateBean;

import java.util.*;

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
	AuditGUARD, EmployeeGUARD,Reviews, Activation,Reactivation, LateFee, ReschedulingFee, ScanningFee, WebcamFee, ExpediteFee, ImportFee, SuncorDiscount, GST, CanadianTax, VAT, Misc, SSIPDiscountFee;

    // after 2013-01-01, we can remove the insureGuard date-based pricing logic.
	private static final Date InsureGUARDPricingEffectiveDate = DateBean.parseDate("2012-01-01");
	private static final Date Jan2013InsureGUARDPricingEffectiveDate = DateBean.parseDate("2013-01-01");
	private static final Date BASFInsureGUARDAndAuditGUARDPricingEffectiveDate = DateBean.parseDate("2012-02-04");
	private static final Date SuncorInsureGUARDPricingEffectiveDate = DateBean.parseDate("2012-02-01");
    protected static final Set<FeeClass> CONTRACTOR_PRICE_TABLE_FEE_TYPES = Collections
            .unmodifiableSet(new TreeSet<>(Arrays.asList(DocuGUARD, AuditGUARD, EmployeeGUARD, Activation, InsureGUARD)));

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

	@Override
	public String getI18nKey() {
		return (!getClass().getSimpleName().isEmpty() ? getClass().getSimpleName() : getClass().getSuperclass()
				.getSimpleName()) + "." + this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	// TODO: This probably needs to be refactored into rules as it continues to grow
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
		exclusions.put(OperatorAccount.OLDCASTLE, Jan2013InsureGUARDPricingEffectiveDate);
		exclusions.put(OperatorAccount.SUNCOR, SuncorInsureGUARDPricingEffectiveDate);
		return exclusions;
	}

    public static Set<FeeClass> getContractorPriceTableFeeTypes() {
        return CONTRACTOR_PRICE_TABLE_FEE_TYPES;
    }
}
