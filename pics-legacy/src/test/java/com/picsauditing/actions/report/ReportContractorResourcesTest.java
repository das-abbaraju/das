package com.picsauditing.actions.report;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ReportContractorResourcesTest extends PicsActionTest {
    private static final int OPERATOR_ONE_ID = 1;
    private static final int OPERATOR_TWO_ID = 2;
    public static final int OPERATOR_THREE_ID = 3;

    private ReportContractorResources reportContractorResources;

    @Before
    public void setup() {
        reportContractorResources = new ReportContractorResources();
    }

    @Test
    public void testExtractOperatorIdsFromContractorOperators() throws Exception {
        reportContractorResources.contractor = ContractorAccount.builder()
                .operator(OperatorAccount.builder()
                        .id(OPERATOR_ONE_ID)
                        .build())
                .operator(OperatorAccount.builder()
                        .id(OPERATOR_TWO_ID)
                        .build(),
                        ApprovalStatus.N)
                .operator(OperatorAccount.builder()
                        .id(OPERATOR_THREE_ID)
                        .build())
                .build();
        Set<Integer> idsSet = Whitebox.invokeMethod(reportContractorResources, "extractOperatorIdsFromContractorOperators");

        Integer[] expectedIds = {Account.PicsID, OPERATOR_ONE_ID, OPERATOR_TWO_ID, OPERATOR_THREE_ID};
        assertTrue(idsSet.contains(OPERATOR_ONE_ID));
        assertTrue(idsSet.contains(OPERATOR_TWO_ID));
        assertTrue(idsSet.contains(OPERATOR_THREE_ID));
    }
}
