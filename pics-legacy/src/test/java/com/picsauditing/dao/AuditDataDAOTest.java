package com.picsauditing.dao;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class AuditDataDAOTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private Query fakeQuery;

    private AuditDataDAO auditDataDAO = new AuditDataDAO();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(entityManager.createQuery(anyString())).thenReturn(fakeQuery);
        auditDataDAO.setEntityManager(entityManager);
    }

    // Covers this use case: PICS-12652.
    @Test
    public void testfindAnswersByContractor_auditForFilledOut() {
        AuditData auditData = AuditData.builder()
                .audit(ContractorAudit.builder()
                        .auditFor("hello")
                        .auditType(AuditType.builder()
                                .id(1)
                                .build()
                        )
                        .build()
                )
                .question(AuditQuestion.builder().id(1).build())
                .build();

        when(fakeQuery.getResultList()).thenReturn(Arrays.asList(new AuditData[]{auditData}));
        assertEquals(1, auditDataDAO.findAnswersByContractor(1, Arrays.asList(new Integer[]{1})).size());
    }

    @Test
    public void testfindAnswersByContractor_TwoAuditDataWithSameQuestion() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(new Date());
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        AuditData auditData1 = AuditData.builder()
                .answer("1")
                .audit(ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .id(1)
                                .build()
                        )
                        .build())
                .creationDate(new Date())
                .question(AuditQuestion.builder().id(1).build())
                .build();

        AuditData auditData2 = AuditData.builder()
                .answer("2")
                .audit(ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .id(1)
                                .build()
                        )
                        .build())
                .creationDate(yesterday.getTime())
                .question(AuditQuestion.builder().id(1).build())
                .build();

        when(fakeQuery.getResultList()).thenReturn(Arrays.asList(new AuditData[]{auditData1, auditData2}));
        Map<Integer,AuditData> answersByContractor = auditDataDAO.findAnswersByContractor(1, Arrays.asList(new Integer[]{1}));
        assertEquals(1, answersByContractor.size());
        assertEquals("1", answersByContractor.get(1).getAnswer());
    }
}
