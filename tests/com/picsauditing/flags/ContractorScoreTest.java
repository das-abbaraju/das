package com.picsauditing.flags;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

// see http://intranet.picsauditing.com/display/organizer/PICS+Confidence+Rating for business rules
public class ContractorScoreTest {
    private List<ContractorAudit> contractorAudits;
    private List<ContractorAuditOperator> contractorAuditOperators;

    @Mock
    private ContractorAccount contractorAccount;
    @Mock
    private ContractorAudit contractorAudit;
    @Mock
    private AuditType auditType;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contractorAudits = new ArrayList<ContractorAudit>();
        contractorAudits.add(contractorAudit);
        contractorAuditOperators = new ArrayList<ContractorAuditOperator>();

        when(contractorAccount.getAudits()).thenReturn(contractorAudits);
        when(contractorAudit.isExpired()).thenReturn(false);
        when(contractorAudit.getOperatorsVisible()).thenReturn(contractorAuditOperators);
    }

    @Test
    public void testCalculate_CompletePqfAdds100ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForPqfTests();

        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Complete);

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 100), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_ResubmittedPqfAdds100ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForPqfTests();

        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Resubmitted);

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 100), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_ResubmitPqfAdds100ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForPqfTests();

        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Resubmit);

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 100), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_SubmittedPqfAdds75ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForPqfTests();

        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Submitted);

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 75), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_PendingPqfAddsHalfOfPercentageCompleteToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForPqfTests();
        when(contractorAuditOperator.getPercentComplete()).thenReturn(80);
        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Pending);

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 40), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_AnnualAddendumForLastYear_CaoStatusNotComplete_Adds0ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForSingleVisibleOperator();
        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Pending);
        when(auditType.isPicsPqf()).thenReturn(false);
        when(auditType.isAnnualAddendum()).thenReturn(true);
        when(contractorAudit.getAuditFor()).thenReturn((DateBean.getCurrentYear() - 1)+"");

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE), is(equalTo(getScore())));
    }

    @Test
    public void testCalculate_AnnualAddendumForLastYear_CaoStatusComplete_Adds25ToScore() throws Exception {
        ContractorAuditOperator contractorAuditOperator = commonSetupForSingleVisibleOperator();
        when(contractorAuditOperator.getStatus()).thenReturn(AuditStatus.Complete);
        when(auditType.isPicsPqf()).thenReturn(false);
        when(auditType.isAnnualAddendum()).thenReturn(true);
        when(contractorAudit.getAuditFor()).thenReturn((DateBean.getCurrentYear() - 1)+"");

        ContractorScore.calculate(contractorAccount);

        assertThat((ContractorScore.BASE_SCORE + 25), is(equalTo(getScore())));
    }

    private ContractorAuditOperator commonSetupForPqfTests() {
        ContractorAuditOperator contractorAuditOperator = commonSetupForSingleVisibleOperator();
        when(auditType.isPicsPqf()).thenReturn(true);
        return contractorAuditOperator;
    }

    private ContractorAuditOperator commonSetupForSingleVisibleOperator() {
        addContractorAuditOperator(1);
        ContractorAuditOperator contractorAuditOperator = contractorAuditOperators.get(0);
        when(contractorAudit.getAuditType()).thenReturn(auditType);
        return contractorAuditOperator;
    }

    private int getScore() {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(contractorAccount).setScore(captor.capture());
        return captor.getValue();
    }

    private void addContractorAuditOperator(int numberToAdd) {
        for (int i = 0; i < numberToAdd; i++) {
            contractorAuditOperators.add(mock(ContractorAuditOperator.class));
        }
    }
}
