package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class InsuranceCriteriaCheckerTest {

    @Test
    public void testMeetsCriteria_FailCriteria() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("1,000,000").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaContractorOperator(2000000);
        assertFalse(InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria));
    }

    @Test
    public void testMeetsCriteria_MeetsCriteria() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("2,000,000").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaContractorOperator(2000000);
        assertTrue(InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria));
    }

    @Test
    public void testMeetsCriteria_MeetsCriteria_WithExcess() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("1,000,000").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaWithExcess(
                FlagCriteriaOptionCode.ExcessAggregate, 2_000_000, "1,000,000", AuditQuestion.EXCESS_AGGREGATE);
        assertTrue(InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria));
    }

    @Test
    public void testMeetsCriteria_FailsCriteria_WithExcess() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("1,000,000").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaWithExcess(
                FlagCriteriaOptionCode.ExcessAggregate, 3_000_000, "1,000,000", AuditQuestion.EXCESS_AGGREGATE);
        assertFalse(InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria));
    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMeetsCriteria_InvalidContractorLimitAnswer() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("pickles").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaContractorOperator(2000000);

        thrown.expect(InvalidAuditDataAnswer.class);
        thrown.expectMessage("Unable to parse audit data id: 0 with answer: pickles");

        InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria);
    }

    @Test
    public void testMeetsCriteria_InvalidContractorExcessLimitAnswer() throws InvalidAuditDataAnswer {
        AuditData contractorsLimit = AuditData.builder().answer("1,000,000").build();
        InsuranceCriteriaContractorOperator insuranceCriteria = buildInsuranceCriteriaWithExcess(
                FlagCriteriaOptionCode.ExcessAggregate, 3_000_000, "tacos", AuditQuestion.EXCESS_AGGREGATE);

        thrown.expect(InvalidAuditDataAnswer.class);
        thrown.expectMessage("Unable to parse audit data id: 0 with answer: tacos");

        InsuranceCriteriaChecker.meetsCriteria(contractorsLimit, insuranceCriteria);
    }

    private InsuranceCriteriaContractorOperator buildInsuranceCriteriaWithExcess(FlagCriteriaOptionCode flagCriteriaOptionCode,
                                                                                 int criteriaLimit,
                                                                                 String contractorExcessLimit,
                                                                                 int excessQuestionId) {
        return InsuranceCriteriaContractorOperator.builder()
                .criteria(FlagCriteria.builder()
                        .insurance()
                        .optionCode(flagCriteriaOptionCode)
                        .build())
                .limit(criteriaLimit)
                .contractor(ContractorAccount.builder()
                        .audit(ContractorAudit.builder()
                                .auditType(AuditType.builder()
                                        .id(AuditType.EXCESS_LIABILITY)
                                        .build())
                                .data(AuditData.builder()
                                        .answer(contractorExcessLimit)
                                        .question(AuditQuestion.builder()
                                                .id(excessQuestionId)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    private InsuranceCriteriaContractorOperator buildInsuranceCriteriaContractorOperator(int criteriaLimit) {
        return InsuranceCriteriaContractorOperator.builder()
                .criteria(FlagCriteria.builder()
                        .build())
                .limit(criteriaLimit)
                .build();
    }

}
