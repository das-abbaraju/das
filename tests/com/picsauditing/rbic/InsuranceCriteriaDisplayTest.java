package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.*;
import org.junit.Test;

import java.util.List;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class InsuranceCriteriaDisplayTest {
    @Test
    public void testGetInsuranceCriteriaMap() throws Exception {
       int[] expectedLimitsInOrder = {5_000_000, 3_000_000, 2_000_000, 1_000_000};
       int[] expectedSizesOfListsInOrder = {3, 2, 1, 3};

        AuditQuestion question = AuditQuestion.builder().build();
        FlagCriteria flagCriteria = FlagCriteria.builder().question(question).build();
        ContractorAccount contractor = ContractorAccount.builder()
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 1_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 1_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 5_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 5_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 2_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 3_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 1_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 3_000_000)
                .insuranceCriteriaOperator(flagCriteria, OperatorAccount.builder().build(), 5_000_000)
                .build();
        SortedMap<Integer, List<InsuranceCriteriaContractorOperator>> resultsMap = InsuranceCriteriaDisplay.
                getInsuranceCriteriaMap(question, contractor);
        int index = 0;
        for (Integer limit: resultsMap.keySet()) {
            assertEquals(expectedLimitsInOrder[index],(long) limit);
            assertEquals(expectedSizesOfListsInOrder[index], resultsMap.get(limit).size());
            index++;
        }
    }
}
