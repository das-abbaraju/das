package com.picsauditing.rbic;

import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;

public class ContractorModelTest {
    @Mock
    private ContractorAccount contractor;
    @Mock
    private InsuranceCriteriaContractorOperatorDAO insuranceCriteriaContractorOperatorDAO;

    private ContractorModel contractorModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        contractorModel = new ContractorModel();
    }

    @Test
    public void testHasTag() throws Exception {
        addContractorTagsToContractor(1, 2, 3);
        contractorModel.setContractor(contractor);

        assertTrue(contractorModel.hasTag(2));
    }

    @Test
    public void testHasTag_TagMissing() throws Exception {
        addContractorTagsToContractor(1, 2, 3);
        contractorModel.setContractor(contractor);

        assertFalse(contractorModel.hasTag(4));
    }

    private void addContractorTagsToContractor(Integer... operatorTagIds) {
        List<ContractorTag> contractorTags = new ArrayList<>();
        for (Integer operatorTagId : operatorTagIds) {
            OperatorTag operatorTag = new OperatorTag();
            operatorTag.setId(operatorTagId);
            ContractorTag contractorTag = new ContractorTag();
            contractorTag.setTag(operatorTag);
            contractorTags.add(contractorTag);
        }
        when(contractor.getOperatorTags()).thenReturn(contractorTags);
    }

    @Test
    public void testWorksFor() throws Exception {
        addOperatorsToContractor(1, 2, 3);
        contractorModel.setContractor(contractor);

        assertTrue(contractorModel.worksFor(2));
    }

    @Test
    public void testWorksFor_OperatorMissing() throws Exception {
        addOperatorsToContractor(1, 2, 3);
        contractorModel.setContractor(contractor);

        assertFalse(contractorModel.worksFor(4));
    }

    private void addOperatorsToContractor(Integer... operatorIds) {
        List<ContractorOperator> contractorOperators = new ArrayList<>();
        for (Integer operatorId : operatorIds) {
            OperatorAccount operator = new OperatorAccount();
            operator.setId(operatorId);
            ContractorOperator contractorOperator = new ContractorOperator();
            contractorOperator.setOperatorAccount(operator);
            contractorOperators.add(contractorOperator);
        }
        when(contractor.getOperators()).thenReturn(contractorOperators);
    }

    @Test
    public void testFindPqfQuestionAnswer() throws Exception {
        List<ContractorAudit> contractorAudits = new ArrayList<>();

        ContractorAudit pqf = Mockito.mock(ContractorAudit.class);
        AuditType pqfAuditType = Mockito.mock(AuditType.class);
        when(pqf.getAuditType()).thenReturn(pqfAuditType);
        when(pqfAuditType.isPqf()).thenReturn(true);

        contractorAudits.add(pqf);
        when(contractor.getAudits()).thenReturn(contractorAudits);

        addAuditDataToContractorAudit(pqf, 1, 2, 3);

        contractorModel.setContractor(contractor);

        assertEquals("Yes", contractorModel.findPqfQuestionAnswer(2));
    }

    @Test
    public void testFindPqfQuestionAnswer_NoPQF() throws Exception {
        int questionId = 1;
        List<ContractorAudit> contractorAudits = new ArrayList<>();

        ContractorAudit audit = Mockito.mock(ContractorAudit.class);
        AuditType auditType = Mockito.mock(AuditType.class);
        when(audit.getAuditType()).thenReturn(auditType);
        when(auditType.isPqf()).thenReturn(false);

        contractorAudits.add(audit);
        when(contractor.getAudits()).thenReturn(contractorAudits);

        contractorModel.setContractor(contractor);

        assertTrue(contractorModel.findPqfQuestionAnswer(questionId) == null);
    }

    @Test
    public void testFindPqfQuestionAnswer_MissingQuestion() throws Exception {
        List<ContractorAudit> contractorAudits = new ArrayList<>();

        ContractorAudit pqf = Mockito.mock(ContractorAudit.class);
        AuditType pqfAuditType = Mockito.mock(AuditType.class);
        when(pqf.getAuditType()).thenReturn(pqfAuditType);
        when(pqfAuditType.isPqf()).thenReturn(true);

        contractorAudits.add(pqf);
        when(contractor.getAudits()).thenReturn(contractorAudits);


        addAuditDataToContractorAudit(pqf, 1, 2, 3);

        contractorModel.setContractor(contractor);

        assertTrue(contractorModel.findPqfQuestionAnswer(4) == null);
    }

    private void addAuditDataToContractorAudit(ContractorAudit contractorAudit, Integer... questionIds) {
        List<AuditData> auditDataList = new ArrayList<>();

        for (Integer questionId : questionIds) {
            AuditQuestion auditQuestion = new AuditQuestion();
            auditQuestion.setId(questionId);
            AuditData auditData = new AuditData();
            auditData.setQuestion(auditQuestion);
            auditData.setAnswer("Yes");
            auditDataList.add(auditData);
        }
        when(contractorAudit.getData()).thenReturn(auditDataList);
    }
}
