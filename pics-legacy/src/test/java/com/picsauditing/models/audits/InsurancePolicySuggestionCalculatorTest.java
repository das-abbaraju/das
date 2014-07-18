package com.picsauditing.models.audits;

import com.picsauditing.jpa.entities.*;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class InsurancePolicySuggestionCalculatorTest {

    private AuditType auditType;
    private FlagCriteria insuranceCriteria;
    private ContractorAudit contractorAudit;
    private ContractorAccount contractor;

    @Test
    public void testCalculate_Simple_JustOneOperator() {
        OperatorAccount operator = makeOperator(2);
        ContractorOperator contractorOperator = makeContractorOperator(operator, FlagColor.Green);

        ContractorAuditOperator cao = makeContractorAuditOperator(operator);

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperator}));

        setAuditsAndOperators(cao);
        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(FlagColor.Green, cao.getFlag());
    }

    @Test
    public void testCalculate_TwoOperatorsUnderSameCaoWithSameFlags() {
        OperatorAccount operatorA = makeOperator(2);
        ContractorOperator contractorOperatorA = makeContractorOperator(operatorA, FlagColor.Red);

        OperatorAccount operatorB = makeOperator(3);
        ContractorOperator contractorOperatorB = makeContractorOperator(operatorB, FlagColor.Red);

        OperatorAccount picsUs = OperatorAccount.builder()
                .id(4)
                .corporate()
                .build();

        ContractorAuditOperator cao = makeContractorAuditOperator(picsUs, operatorA, operatorB);

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperatorA, contractorOperatorB}));

        setAuditsAndOperators(cao);
        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(FlagColor.Red, cao.getFlag());
    }

    @Test
    public void testCalculate_TwoOperatorsUnderSameCaoWithDifferentFlags() {
        OperatorAccount operatorA = makeOperator(2);
        ContractorOperator contractorOperatorA = makeContractorOperator(operatorA, FlagColor.Red);

        OperatorAccount operatorB = makeOperator(3);
        ContractorOperator contractorOperatorB = makeContractorOperator(operatorB, FlagColor.Green);

        OperatorAccount picsUs = OperatorAccount.builder()
                .id(4)
                .corporate()
                .build();

        ContractorAuditOperator cao = makeContractorAuditOperator(picsUs, operatorA, operatorB);

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperatorA, contractorOperatorB}));

        setAuditsAndOperators(cao);
        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(FlagColor.Red, cao.getFlag());
    }

    @Test
    public void testCalculate_AuditNotPolicy() {
        OperatorAccount operatorA = makeOperator(2);
        ContractorOperator contractorOperatorA = makeContractorOperator(operatorA, FlagColor.Red);

        OperatorAccount operatorB = makeOperator(3);
        ContractorOperator contractorOperatorB = makeContractorOperator(operatorB, FlagColor.Green);

        OperatorAccount picsUs = OperatorAccount.builder()
                .id(4)
                .corporate()
                .build();

        ContractorAuditOperator cao = makeContractorAuditOperator(picsUs, operatorA, operatorB);

        auditType.setClassType(AuditTypeClass.Audit);

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperatorA, contractorOperatorB}));

        setAuditsAndOperators(cao);
        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(null, cao.getFlag());
    }

    @Test
    public void testCalculate_AuditIsExpired() {
        OperatorAccount operatorA = makeOperator(2);
        ContractorOperator contractorOperatorA = makeContractorOperator(operatorA, FlagColor.Red);

        OperatorAccount operatorB = makeOperator(3);
        ContractorOperator contractorOperatorB = makeContractorOperator(operatorB, FlagColor.Green);

        OperatorAccount picsUs = OperatorAccount.builder()
                .id(4)
                .corporate()
                .build();

        ContractorAuditOperator cao = makeContractorAuditOperator(picsUs, operatorA, operatorB);

        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(new Date());
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        contractorAudit.setExpiresDate(yesterday.getTime());

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperatorA, contractorOperatorB}));
        setAuditsAndOperators(cao);

        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(null, cao.getFlag());
    }

    private void setAuditsAndOperators(ContractorAuditOperator cao) {
        contractor.setAudits(Arrays.asList(new ContractorAudit[]{contractorAudit}));
        contractorAudit.setOperators(Arrays.asList(new ContractorAuditOperator[]{cao}));
    }

    @Test
    public void testCalculate_AuditIsPending() {
        OperatorAccount operatorA = makeOperator(2);
        ContractorOperator contractorOperatorA = makeContractorOperator(operatorA, FlagColor.Red);

        OperatorAccount operatorB = makeOperator(3);
        ContractorOperator contractorOperatorB = makeContractorOperator(operatorB, FlagColor.Green);

        OperatorAccount picsUs = OperatorAccount.builder()
                .id(4)
                .corporate()
                .build();

        ContractorAuditOperator cao = makeContractorAuditOperator(picsUs, operatorA, operatorB);
        cao.setStatus(AuditStatus.Pending);

        contractor.setOperators(Arrays.asList(new ContractorOperator[]{contractorOperatorA, contractorOperatorB}));

        setAuditsAndOperators(cao);
        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        assertEquals(null, cao.getFlag());
    }


    @Before
    public void setup() {
        auditType = AuditType.builder()
                .auditClass(AuditTypeClass.Policy)
                .build();

        insuranceCriteria = FlagCriteria.builder()
                .question(AuditQuestion.builder()
                        .category(AuditCategory.builder()
                                .auditType(auditType)
                                .build())
                        .build())
                .insurance()
                .build();

        contractorAudit = ContractorAudit.builder()
                .auditType(auditType)
                .build();
        contractor = ContractorAccount.builder()
                .id(1)
                .build();
    }

    private ContractorOperator makeContractorOperator(OperatorAccount operator, FlagColor green) {
        return ContractorOperator.builder()
                .operator(operator)
                .contractor(contractor)
                .flagData(FlagData.builder()
                        .criteria(insuranceCriteria)
                        .flag(green)
                        .build())
                .build();
    }

    private OperatorAccount makeOperator(int id) {
        return OperatorAccount.builder()
                .id(id)
                .operator()
                .build();
    }

    private ContractorAuditOperator makeContractorAuditOperator(OperatorAccount operator) {
        return ContractorAuditOperator.builder()
                .status(AuditStatus.Approved)
                .audit(contractorAudit)
                .operator(operator)
                .caop()
                .build();
    }

    private ContractorAuditOperator makeContractorAuditOperator(OperatorAccount parent, OperatorAccount... children) {
        return ContractorAuditOperator.builder()
                .status(AuditStatus.Approved)
                .audit(contractorAudit)
                .operator(parent)
                .caop(children)
                .build();
    }
}
