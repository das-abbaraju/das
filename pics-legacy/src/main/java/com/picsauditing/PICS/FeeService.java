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
        Map<FeeClass, InvoiceFee> foundFeeClasses = new HashMap<FeeClass, InvoiceFee>();

        int payingFacilities = contractor.getPayingFacilities();

        boolean foundMembership = false;
        boolean foundMembershipDate = false;
        boolean foundPaymentExpires = false;

        for (Invoice invoice : contractor.getSortedInvoices()) {
            if (!invoice.getStatus().isVoid()) {
                for (InvoiceItem invoiceItem : invoice.getItems()) {
                    InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();
                    FeeClass feeClass = invoiceFee.getFeeClass();

                    if (!foundMembership && invoiceFee.isMembership()) {
                        payingFacilities = identifyMembership(foundFeeClasses, payingFacilities, invoiceFee, feeClass);

                        if (!foundPaymentExpires && invoiceItem.getPaymentExpires() != null) {
                            contractor.setPaymentExpires(invoiceItem.getPaymentExpires());
                            foundPaymentExpires = true;
                        }
                    }
                    if (!foundMembershipDate && (invoiceFee.isActivation() || invoiceFee.isReactivation()))
                        foundMembershipDate = setMembershipDate(contractor, foundMembershipDate, invoice);

                    // Checking for ImportPQF fee and potentially others
                    if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.ImportFee) && contractor.getFees().containsKey(FeeClass.ImportFee)) {
                        foundFeeClasses.put(FeeClass.ImportFee, null);
                        payingFacilities = 1;
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
    }

    private int identifyMembership(Map<FeeClass, InvoiceFee> foundFeeClasses, int payingFacilities, InvoiceFee invoiceFee, FeeClass feeClass) {
        if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.ListOnly))
            foundFeeClasses.put(FeeClass.ListOnly, null);
        else if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.BidOnly))
            foundFeeClasses.put(FeeClass.BidOnly, null);
        else if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.DocuGUARD)) {

            if (invoiceFee.isLegacyMembership()) {
                foundFeeClasses.put(FeeClass.DocuGUARD, null);
                foundFeeClasses.put(FeeClass.InsureGUARD, null);
            } else {
                foundFeeClasses.put(FeeClass.DocuGUARD, invoiceFee);
            }
        } else if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.AuditGUARD)) {

            if (invoiceFee.isLegacyMembership()) {
                foundFeeClasses.put(FeeClass.AuditGUARD, null);
                foundFeeClasses.put(FeeClass.DocuGUARD, null);
                foundFeeClasses.put(FeeClass.InsureGUARD, null);

                switch(invoiceFee.getId()) {
                    case 5: payingFacilities = 1; break;
                    case 105: payingFacilities = 1; break;
                    case 6: payingFacilities = 2; break;
                    case 7: payingFacilities = 5; break;
                    case 8: payingFacilities = 9; break;
                    case 9: payingFacilities = 13; break;
                    case 10: payingFacilities = 20; break;
                    case 11: payingFacilities = 50; break;
                }
            }
            else {
                foundFeeClasses.put(FeeClass.AuditGUARD, invoiceFee);
            }
        } else if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.InsureGUARD))
            foundFeeClasses.put(FeeClass.InsureGUARD, invoiceFee);
        else if (hasFeeClass(foundFeeClasses, feeClass, FeeClass.EmployeeGUARD))
            foundFeeClasses.put(FeeClass.EmployeeGUARD, invoiceFee);
        return payingFacilities;
    }

    private void buildFeeCurrentLevels(ContractorAccount contractor, Map<FeeClass, InvoiceFee> foundFeeClasses, int payingFacilities) {
        for (FeeClass feeClass : CONTRACTOR_FEE_CLASSES) {
            if (feeClass == FeeClass.ImportFee && !contractor.getFees().containsKey(FeeClass.ImportFee))
                continue;

            if (foundFeeClasses.containsKey(feeClass)) {
                InvoiceFee fee = foundFeeClasses.get(feeClass);
                if (fee == null) {
                    fee = feeDAO.findByNumberOfOperatorsAndClass(feeClass, payingFacilities);
                }

                setFee(contractor, fee, FeeService.getAdjustedFeeAmountIfNecessary(contractor, fee), true);
            }
            else
                clearFee(contractor, feeClass, true);
        }
    }

    private boolean setMembershipDate(ContractorAccount contractor, boolean foundMembershipDate, Invoice invoice) {
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

    private boolean hasFeeClass(Map<FeeClass, InvoiceFee> foundFeeClasses, FeeClass feeClass, FeeClass targetFeeClass) {
        return feeClass.equals(targetFeeClass) && !foundFeeClasses.containsKey(targetFeeClass);
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

        buildFeeNewLevels(contractor, payingFacilities, hasEmployeeAudits, hasHseCompetency, requiresOQ, feeClasses, operatorsRequiringInsureGUARD);
    }

    private void buildFeeNewLevels(ContractorAccount contractor, int payingFacilities, boolean hasEmployeeAudits, boolean hasHseCompetency, boolean requiresOQ, Set<FeeClass> feeClasses, Set<OperatorAccount> operatorsRequiringInsureGUARD) {
        for (FeeClass feeClass : CONTRACTOR_FEE_CLASSES) {
            if (feeClass == FeeClass.ImportFee && feeClasses.contains(feeClass)) {
                InvoiceFee newLevel = feeDAO.find(InvoiceFee.IMPORTFEE);

                if (!contractor.getFees().containsKey(feeClass)) {
                    InvoiceFee currentLevel = feeDAO.find(InvoiceFee.IMPORTFEEZEROLEVEL);

                    ContractorFee importConFee = buildContractorFee(contractor, currentLevel, FeeService.getAdjustedFeeAmountIfNecessary(contractor,currentLevel));
                    feeDAO.save(importConFee);

                    contractor.getFees().put(feeClass, importConFee);
                }

                setFee(contractor, newLevel, FeeService.getAdjustedFeeAmountIfNecessary(contractor, newLevel), false);

            }
            else if (feeClasses.contains(feeClass) && feeClass != FeeClass.ImportFee) {
                InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(feeClass, payingFacilities);
                BigDecimal newAmount = FeeService.getAdjustedFeeAmountIfNecessary(contractor, newLevel);

                if (!feeClass.isExcludedFor(contractor, newLevel, operatorsRequiringInsureGUARD))
                    setFee(contractor, newLevel, newAmount, false);
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
            if (operator.getId() == OperatorAccount.AI || operator.getId() == OperatorAccount.CINTAS_CANADA)
                continue;

            if (operator.isDescendantOf(OperatorAccount.AI))
                continue;

            return false;
        }
        return true;
    }

    private void setFee(ContractorAccount contractor, InvoiceFee fee, BigDecimal amount, boolean isCurrent) {
        Map<FeeClass, ContractorFee> contractorFees = contractor.getFees();
        if (isMissingFee(contractorFees, fee))
            setNewContractorFeeOnContractor(contractor, fee, amount);
        else {
            ContractorFee contractorFee = contractorFees.get(fee.getFeeClass());

            if (isCurrent) {
                contractorFee.setCurrentLevel(fee);
                contractorFee.setCurrentAmount(amount);
            }
            else {
                contractorFee.setNewLevel(fee);
                contractorFee.setNewAmount(amount);
            }
        }
    }

    private void clearFee(ContractorAccount contractor, FeeClass feeClass, boolean isCurrent) {
        BigDecimal amount = BigDecimal.ZERO;

        InvoiceFee invoiceFee = feeDAO.findByNumberOfOperatorsAndClass(feeClass, 0);
        setFee(contractor, invoiceFee, amount, isCurrent);
    }

    public boolean isMissingFee(Map<FeeClass, ContractorFee> contractorFees, InvoiceFee invoiceFee) {
        return invoiceFee == null || invoiceFee.getFeeClass() == null
                || isMissingFee(contractorFees, invoiceFee.getFeeClass());
    }

    public boolean isMissingFee(Map<FeeClass, ContractorFee> contractorFees, FeeClass fee) {
        return MapUtils.isEmpty(contractorFees) || !contractorFees.containsKey(fee);
    }

    public void setNewContractorFeeOnContractor(ContractorAccount contractor, InvoiceFee invoiceFee, BigDecimal amount) {
        if (invoiceFee == null || invoiceFee.getFeeClass() == null) {
            return;
        }

        ContractorFee contractorFee = buildContractorFee(contractor, invoiceFee, amount);

        Map<FeeClass, ContractorFee> contractorFees = contractor.getFees();
        if (contractorFees == null) {
            contractorFees = new TreeMap<FeeClass, ContractorFee>();
            contractor.setFees(contractorFees);
        }

        contractorFees.put(invoiceFee.getFeeClass(), contractorFee);
    }

    private ContractorFee buildContractorFee(ContractorAccount contractor, InvoiceFee invoiceFee, BigDecimal amount) {
        ContractorFee contractorFee = new ContractorFee();
        contractorFee.setAuditColumns(contractor.getUpdatedBy());
        contractorFee.setContractor(contractor);
        contractorFee.setFeeClass(invoiceFee.getFeeClass());
        contractorFee.setCurrentLevel(invoiceFee);
        contractorFee.setNewLevel(invoiceFee);
        contractorFee.setCurrentAmount(amount);
        contractorFee.setNewAmount(amount);
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
}