package com.picsauditing.PICS;

import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public class FeeService {
    @Autowired
    private InvoiceFeeDAO feeDAO;
    @Autowired
    protected static AuditTypeRuleCache ruleCache;
    @Autowired
    private BillingService billingService;

    public AuditTypeRuleCache getRuleCache() {
        if (ruleCache == null) {
            ruleCache = SpringUtils.getBean("AuditTypeRuleCache");
        }
        return ruleCache;
    }

    private static final ArrayList<FeeClass> CONTRACTOR_FEE_CLASSES = new ArrayList<FeeClass>() {{
        for (FeeClass feeClass : FeeClass.values()) {
            if (feeClass.isMembership()) {
                add(feeClass);
            }
        }

        add(FeeClass.ImportFee);
    }};

    public void syncMembershipFees(ContractorAccount contractor) {
        Map<FeeClass, Map<InvoiceFee, BigDecimal>> foundFeeClasses = new HashMap<>();

        BillingStatus currentBillingStatus = billingService.billingStatus(contractor);
        boolean foundMembership = false;
        boolean foundMembershipDate = false;
        boolean foundPaymentExpires = false;
        boolean foundPayingFacilities = false;
        int payingFacilities = 0;

        for (Invoice invoice : contractor.getSortedInvoices()) {
            if (foundMembershipDate) {
                break;
            }

            if (!invoice.getStatus().isVoid() && !BillingService.hasCreditMemosForFullAmount(invoice) && invoice.getInvoiceType().isMembershipType()) {
                if (!foundPayingFacilities) {
                    payingFacilities = invoice.getPayingFacilities();
                    foundPayingFacilities = true;
                }

                for (InvoiceItem invoiceItem : invoice.getItems()) {
                    InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();
                    FeeClass feeClass = invoiceFee.getFeeClass();

                    if (!foundMembership && invoiceFee.isMembership() || feeClass == FeeClass.ImportFee) {
                        if (foundFeeClasses.get(invoiceItem.getInvoiceFee().getFeeClass()) == null) {
                            HashMap<InvoiceFee, BigDecimal> feeClassMap = new HashMap<>();
                            BigDecimal amount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);

                            if (!invoiceItem.getAmount().equals(amount) || invoiceItem.getInvoiceFee().getFeeClass() != FeeClass.EmployeeGUARD) {
                                amount = invoiceItem.getOriginalAmount();
                            }

                            feeClassMap.put(invoiceItem.getInvoiceFee(), amount);
                            foundFeeClasses.put(invoiceItem.getInvoiceFee().getFeeClass(), feeClassMap);
                        }

                        if (!foundPaymentExpires && invoiceItem.getPaymentExpires() != null) {
                            contractor.setPaymentExpires(invoiceItem.getPaymentExpires());
                            foundPaymentExpires = true;
                        }
                    }

                    if (!foundMembershipDate && (invoiceFee.isActivation() || invoiceFee.isReactivation())) {
                        foundMembershipDate = setMembershipDate(contractor, invoice);
                    }
                }

                if (foundPaymentExpires)
                    foundMembership = true;

                invoice.getCommissionEligibleFees(true);
            }
        }

        buildFeeCurrentLevels(contractor, foundFeeClasses, payingFacilities);

        if (!foundPaymentExpires)
            contractor.setPaymentExpires(contractor.getCreationDate());

        if (!foundMembershipDate)
            contractor.setMembershipDate(null);

        calculateUpgradeDate(contractor, currentBillingStatus);
    }

    public void calculateUpgradeDate(ContractorAccount contractor, BillingStatus currentBillingStatus) {
        BillingStatus newBillingStatus = billingService.billingStatus(contractor);

        dropBlockSSManualAuditTagIfUpgrading(contractor, newBillingStatus);

        if (currentBillingStatus != newBillingStatus) {
            if (currentBillingStatus != BillingStatus.Upgrade && newBillingStatus == BillingStatus.Upgrade) {
                contractor.setLastUpgradeDate(new Date());
            }
            else if (currentBillingStatus == BillingStatus.Upgrade && newBillingStatus != BillingStatus.Upgrade) {
                contractor.setLastUpgradeDate(null);
            }
        }
    }

    private void buildFeeCurrentLevels(ContractorAccount contractor, Map<FeeClass, Map<InvoiceFee, BigDecimal>> foundFeeClasses, int payingFacilities) {
        for (FeeClass feeClass : CONTRACTOR_FEE_CLASSES) {
            if (feeClass == FeeClass.ImportFee && !foundFeeClasses.containsKey(FeeClass.ImportFee))
                continue;

            if (foundFeeClasses.containsKey(feeClass)) {
                Map<InvoiceFee,BigDecimal> feeLevel = foundFeeClasses.get(feeClass);
                InvoiceFee invoiceFee = feeLevel.keySet().iterator().next();

                setFee(contractor, feeDAO.findByNumberOfOperatorsAndClass(feeClass, payingFacilities), feeLevel.get(invoiceFee), payingFacilities, true);
            }
            else
                clearFee(contractor, feeClass, true);
        }
    }

    private boolean setMembershipDate(ContractorAccount contractor, Invoice invoice) {
        boolean foundMembershipDate = false;
        if (invoice.getPayments().size() > 0) {
            PaymentApplied payment = getLatestPayment(invoice);

            contractor.setMembershipDate(payment.getCreationDate());
            foundMembershipDate = true;
        }
        return foundMembershipDate;
    }

    private PaymentApplied getLatestPayment(Invoice invoice) {
        List<PaymentApplied> sortedPaymentList = new ArrayList<PaymentApplied>(invoice.getPayments());

        Collections.sort(sortedPaymentList, new Comparator<PaymentApplied>() {
            public int compare(PaymentApplied paymentOne, PaymentApplied paymentTwo) {
                return paymentTwo.getCreationDate().compareTo(paymentOne.getCreationDate());
            }
        });

        return sortedPaymentList.get(0);
    }

    public void calculateContractorInvoiceFees(ContractorAccount contractor) {
        calculateContractorInvoiceFees(contractor, true);
    }

    public void calculateContractorInvoiceFees(ContractorAccount contractor, boolean skipRequested) {
        if (skipRequested && contractor.getStatus().isRequested())
            return;

        int payingFacilities = setPayingFacilities(contractor);

        if (payingFacilities == 0) {
            clearAllFees(contractor);

            return;
        }

        BillingStatus currentBillingStatus = billingService.billingStatus(contractor);
        boolean hasEmployeeAudits = false;
        boolean hasHseCompetency = false;
        boolean requiresOQ = false;
        boolean isLinkedToSuncor = false;
        Set<FeeClass> feeClasses = new HashSet<FeeClass>();

        for (ContractorOperator co : contractor.getOperators()) {
            if (!requiresOQ && co.getOperatorAccount().isRequiresOQ())
                requiresOQ = true;

            if (!isLinkedToSuncor && co.getOperatorAccount().isDescendantOf(OperatorAccount.SUNCOR))
                isLinkedToSuncor = true;
        }

        if (contractor.getAccountLevel().isListOnly()) {
            feeClasses.add(FeeClass.ListOnly);
            payingFacilities = 1;
        }
        else if (contractor.getAccountLevel().isBidOnly()) {
            feeClasses.add(FeeClass.BidOnly);
            payingFacilities = 1;
        }
        else
            feeClasses.add(FeeClass.DocuGUARD);

        AuditTypesBuilder builder = new AuditTypesBuilder(getRuleCache(), contractor);
        Set<OperatorAccount> operatorsRequiringInsureGUARD = new HashSet<OperatorAccount>();
        Set<AuditTypeDetail> auditTypeDetails = builder.calculate();

        for (AuditTypeDetail detail : auditTypeDetails) {
            AuditType auditType = detail.rule.getAuditType();

            if (auditType == null)
                continue;

            if (auditType.isDesktop() || auditType.isImplementation() || auditType.isSsip() || (auditType.isCorIec() && isLinkedToSuncor))
                feeClasses.add(FeeClass.AuditGUARD);

            if (auditType.getClassType().equals(AuditTypeClass.Policy)) {
                operatorsRequiringInsureGUARD.addAll(detail.operators);

                if (qualifiesForInsureGuard(operatorsRequiringInsureGUARD))
                    feeClasses.add(FeeClass.InsureGUARD);
            }

            if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isEmployee())
                hasEmployeeAudits = true;

            if (auditType.getId() == AuditType.HSE_COMPETENCY)
                hasHseCompetency = true;
        }

        if (requiresOQ || hasHseCompetency || hasEmployeeAudits)
            feeClasses.add(FeeClass.EmployeeGUARD);

        for (ContractorAudit ca : contractor.getAudits()) {
            if (ca.getAuditType().getId() == AuditType.IMPORT_PQF && !ca.isExpired()) {
                feeClasses.add(FeeClass.ImportFee);
                break;
            }
        }

        buildFeeNewLevels(contractor, payingFacilities, feeClasses, operatorsRequiringInsureGUARD);

        calculateUpgradeDate(contractor, currentBillingStatus);
    }

    // TODO: THIS IS TO BE REMOVED BEFORE 2015
    private void dropBlockSSManualAuditTagIfUpgrading(ContractorAccount contractor, BillingStatus billingStatus) {
        if (billingStatus.isUpgrade()) {
            ContractorTag ssBlockTag = getSSBlockContractorTag(contractor);

            if (ssBlockTag != null) {
                contractor.getOperatorTags().remove(ssBlockTag);
                feeDAO.remove(ssBlockTag);
                billingService.syncBalance(contractor);
                calculateContractorInvoiceFees(contractor);
            }
        }
    }

    private ContractorTag getSSBlockContractorTag(ContractorAccount contractor) {
        for (ContractorTag tag : contractor.getOperatorTags()) {
            if (tag.getTag().isBlockSSManualAudit()) {
                return tag;
            }
        }
        return null;
    }

    private void buildFeeNewLevels(ContractorAccount contractor, int payingFacilities, Set<FeeClass> feeClasses, Set<OperatorAccount> operatorsRequiringInsureGUARD) {
        for (FeeClass feeClass : CONTRACTOR_FEE_CLASSES) {
            if (feeClass == FeeClass.ImportFee && feeClasses.contains(feeClass)) {
                InvoiceFee newLevel = feeDAO.find(InvoiceFee.IMPORTFEE);

                if (!contractor.getFees().containsKey(feeClass)) {
                    InvoiceFee currentLevel = feeDAO.find(InvoiceFee.IMPORTFEEZEROLEVEL);

                    ContractorFee importConFee = buildContractorFee(contractor, currentLevel, FeeService.getAdjustedFeeAmountIfNecessary(contractor,currentLevel), payingFacilities);
                    feeDAO.save(importConFee);

                    contractor.getFees().put(feeClass, importConFee);
                }

                setFee(contractor, newLevel, FeeService.getAdjustedFeeAmountIfNecessary(contractor, newLevel), payingFacilities, false);

            }
            else if (feeClasses.contains(feeClass) && feeClass != FeeClass.ImportFee) {
                InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(feeClass, payingFacilities);
                BigDecimal newAmount = FeeService.getAdjustedFeeAmountIfNecessary(contractor, newLevel);

                if (!feeClass.isExcludedFor(contractor, newLevel, operatorsRequiringInsureGUARD))
                    setFee(contractor, newLevel, newAmount, payingFacilities, false);
                else
                    clearFee(contractor, feeClass, false);
            } else
                clearFee(contractor, feeClass, false);
        }
    }

    private void clearAllFees(ContractorAccount contractor) {
        for (FeeClass feeClass : contractor.getFees().keySet()) {
            if (feeClass.isMembership()) {
                clearFee(contractor, feeClass, false);
            }
        }
    }

    public static BigDecimal getRegionalAmountOverride(ContractorAccount contractor, InvoiceFee invoiceFee) {
        for (InvoiceFeeCountry countryFeeAmountOverride : contractor.getCountry().getAmountOverrides()) {
            if (countryFeeAmountOverride.getInvoiceFee().equals(invoiceFee)) {
                return countryFeeAmountOverride.getAmount();
            }
        }

        return invoiceFee.getAmount();
    }

    protected boolean qualifiesForInsureGuard(Set<OperatorAccount> operatorsRequiringInsureGUARD) {
        return (!(IGisExemptedFor(operatorsRequiringInsureGUARD)));
    }

    private boolean IGisExemptedFor(Set<OperatorAccount> operators) {
        for (OperatorAccount operator : operators) {
            if (operator.getId() == OperatorAccount.CINTAS_CANADA)
                continue;

            return false;
        }
        return true;
    }

    private void setFee(ContractorAccount contractor, InvoiceFee fee, BigDecimal amount, int payingFacilities, boolean isCurrent) {
        Map<FeeClass, ContractorFee> contractorFees = contractor.getFees();
        if (isMissingFee(contractorFees, fee))
            setNewContractorFeeOnContractor(contractor, fee, amount, payingFacilities);
        else {
            ContractorFee contractorFee = contractorFees.get(fee.getFeeClass());

            if (isCurrent) {
                contractorFee.setCurrentLevel(fee);
                contractorFee.setCurrentAmount(amount);
                contractorFee.setCurrentFacilityCount(payingFacilities);
            }
            else {
                contractorFee.setNewLevel(fee);
                contractorFee.setNewAmount(amount);
                contractorFee.setNewFacilityCount(payingFacilities);
            }
        }
    }

    private void clearFee(ContractorAccount contractor, FeeClass feeClass, boolean isCurrent) {
        BigDecimal amount = BigDecimal.ZERO;

        InvoiceFee invoiceFee = feeDAO.findByNumberOfOperatorsAndClass(feeClass, 0);
        setFee(contractor, invoiceFee, amount, 0, isCurrent);
    }

    public boolean isMissingFee(Map<FeeClass, ContractorFee> contractorFees, InvoiceFee invoiceFee) {
        return invoiceFee == null || invoiceFee.getFeeClass() == null
                || isMissingFee(contractorFees, invoiceFee.getFeeClass());
    }

    public boolean isMissingFee(Map<FeeClass, ContractorFee> contractorFees, FeeClass fee) {
        return MapUtils.isEmpty(contractorFees) || !contractorFees.containsKey(fee);
    }

    public void setNewContractorFeeOnContractor(ContractorAccount contractor, InvoiceFee invoiceFee, BigDecimal amount, int payingFacilities) {
        if (invoiceFee == null || invoiceFee.getFeeClass() == null) {
            return;
        }

        ContractorFee contractorFee = buildContractorFee(contractor, invoiceFee, amount, payingFacilities);

        Map<FeeClass, ContractorFee> contractorFees = contractor.getFees();
        if (contractorFees == null) {
            contractorFees = new TreeMap<FeeClass, ContractorFee>();
            contractor.setFees(contractorFees);
        }

        contractorFees.put(invoiceFee.getFeeClass(), contractorFee);
    }

    private ContractorFee buildContractorFee(ContractorAccount contractor, InvoiceFee invoiceFee, BigDecimal amount, int payingFacilities) {
        ContractorFee contractorFee = new ContractorFee();
        contractorFee.setAuditColumns(contractor.getUpdatedBy());
        contractorFee.setContractor(contractor);
        contractorFee.setFeeClass(invoiceFee.getFeeClass());
        contractorFee.setCurrentLevel(invoiceFee);
        contractorFee.setNewLevel(invoiceFee);
        contractorFee.setCurrentAmount(amount);
        contractorFee.setNewAmount(amount);
        contractorFee.setCurrentFacilityCount(payingFacilities);
        contractorFee.setNewFacilityCount(payingFacilities);
        return contractorFee;
    }

    private int setPayingFacilities(ContractorAccount contractor) {
        List<OperatorAccount> payingOperators = calculatePayingFacilitiesCount(contractor);

        if (payingOperators.size() == 1) {
            if (payingOperators.get(0).getDoContractorsPay().equals("Multiple")) {
                return contractor.setPayingFacilities(0);
            }
        }

        return contractor.setPayingFacilities(payingOperators.size());
    }

    private List<OperatorAccount> calculatePayingFacilitiesCount(ContractorAccount contractor) {
        List<OperatorAccount> payingOperators = new ArrayList<OperatorAccount>();
        for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
            OperatorAccount operator = contractorOperator.getOperatorAccount();
            if (operator.getStatus().isActive() && !"No".equals(operator.getDoContractorsPay())) {
                payingOperators.add(operator);
            }
        }
        return payingOperators;
    }

    public static BigDecimal getAdjustedFeeAmountIfNecessary(ContractorAccount contractor, InvoiceFee fee) {
        if (fee.getFeeClass() == FeeClass.EmployeeGUARD) {
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
                return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
            }

            return FeeService.getRegionalAmountOverride(contractor, fee);
        }
        else if (fee.getFeeClass() == FeeClass.Activation) {
            Set<BigDecimal> discounts = new HashSet<BigDecimal>();
            for (OperatorAccount operator : contractor.getOperatorAccounts()) {
                if (operator.isHasDiscount()) {
                    discounts.add(operator.getDiscountPercent());
                } else {
                    OperatorAccount inheritedDiscountPercentOperator = operator.getInheritedDiscountPercentOperator();
                    if (inheritedDiscountPercentOperator != null) {
                        discounts.add(inheritedDiscountPercentOperator.getDiscountPercent());
                    } else {
                        return FeeService.getRegionalAmountOverride(contractor, fee);
                    }
                }
            }

            // This happens if there are no operators attached to this
            // contractor.
            // Unlikely, but if it never happened, I wouldn't be writing this.
            if (discounts.isEmpty()) {
                return FeeService.getRegionalAmountOverride(contractor, fee);
            }

            BigDecimal minimumDiscount = Collections.min(discounts);
            minimumDiscount = BigDecimal.ONE.subtract(minimumDiscount);
            return FeeService.getRegionalAmountOverride(contractor, fee).multiply(minimumDiscount).setScale(0, BigDecimal.ROUND_DOWN);
        }
        else {
            return FeeService.getRegionalAmountOverride(contractor, fee);
        }
    }

	public static List<FeeClass> NON_REFUNDABLE_FEE_CLASSES() {
		return TaxService.TAX_FEE_CLASSES;
	}

	public static boolean isRevRecDeferred(InvoiceFee fee) {
		return !fee.isTax() && !fee.isFree();
	}
}
