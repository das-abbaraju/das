package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.AuditData;
import com.picsauditing.flagcalculator.entities.AuditQuestion;
import com.picsauditing.flagcalculator.entities.AuditType;
import com.picsauditing.flagcalculator.entities.ContractorAudit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class FlagEtlDAOTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private Query fakeQuery;

    private FlagEtlDAO flagEtlDAO = new FlagEtlDAO();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(entityManager.createQuery(anyString())).thenReturn(fakeQuery);
        flagEtlDAO.setEntityManager(entityManager);
    }

    // Covers this use case: PICS-12652.
    @Test
    public void testfindAnswersByContractor_auditForFilledOut() {
        AuditQuestion auditQuestion = new AuditQuestion();
        auditQuestion.setId(1);
        AuditType auditType = new AuditType();
        auditType.setId(1);
        ContractorAudit contractorAudit = new ContractorAudit();
        contractorAudit.setAuditType(auditType);
        contractorAudit.setAuditFor("hello");
        AuditData auditData = new AuditData();
        auditData.setAudit(contractorAudit);
        auditData.setQuestion(auditQuestion);

        when(fakeQuery.getResultList()).thenReturn(Arrays.asList(new AuditData[]{auditData}));
        assertEquals(1, flagEtlDAO.findAnswersByContractor(1, Arrays.asList(new Integer[]{1})).size());
    }

    @Test
    public void testfindAnswersByContractor_TwoAuditDataWithSameQuestion() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(new Date());
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        AuditQuestion auditQuestion = new AuditQuestion();
        auditQuestion.setId(1);
        AuditType auditType = new AuditType();
        auditType.setId(1);
        ContractorAudit contractorAudit = new ContractorAudit();
        contractorAudit.setAuditType(auditType);
        AuditData auditData1 = new AuditData();
        auditData1.setAnswer("1");
        auditData1.setAudit(contractorAudit);
        auditData1.setCreationDate(new Date());
        auditData1.setQuestion(auditQuestion);
        AuditData auditData2 = new AuditData();
        auditData2.setAnswer("2");
        auditData2.setAudit(contractorAudit);
        auditData2.setCreationDate(yesterday.getTime());
        auditData2.setQuestion(auditQuestion);

        when(fakeQuery.getResultList()).thenReturn(Arrays.asList(new AuditData[]{auditData1, auditData2}));
        Map<Integer,AuditData> answersByContractor = flagEtlDAO.findAnswersByContractor(1, Arrays.asList(new Integer[]{1}));
        assertEquals(1, answersByContractor.size());
        assertEquals("1", answersByContractor.get(1).getAnswer());
    }
}
