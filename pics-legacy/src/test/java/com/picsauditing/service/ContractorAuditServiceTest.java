package com.picsauditing.service;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class ContractorAuditServiceTest {
    @Mock
    protected CertificateDAO certificateDAO;
    @Mock
    protected ContractorAuditDAO contractorAuditDAO;
    @Mock
    ContractorAccount contractor;


    ContractorAuditService service;
    List<Certificate> certificates = new ArrayList<>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void setUp() throws Exception {
        service = new ContractorAuditService();
        MockitoAnnotations.initMocks(this);

        PicsTestUtil.forceSetPrivateField(service, "certificateDAO", certificateDAO);
        PicsTestUtil.forceSetPrivateField(service, "contractorAuditDAO", contractorAuditDAO);

        when(certificateDAO.findByConId(anyInt())).thenReturn(certificates);
    }

    @Test
    public void testUpdateAuditEffectiveDateBasedOnAnswer_policyEffectiveDate() {
        Date date = DateBean.parseDate("2013-12-25");
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("policyEffectiveDate");
        data.setQuestion(question);

        service.updateAuditEffectiveDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getEffectiveDate()));
    }

    @Test
    public void testUpdateAuditExpirationDateBasedOnAnswer_expirationDate() {
        Date date = DateBean.setToEndOfDay(DateBean.parseDate("2013-12-25"));
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("expirationDate");
        data.setQuestion(question);

        service.updateAuditExpirationDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getExpiresDate()));
    }

    @Test
    public void testUpdateAuditExpirationDateBasedOnAnswer_policyExpirationDate() {
        Date date = DateBean.getNextDayMidnight(DateBean.parseDate("2013-12-25"));
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("policyExpirationDate");
        data.setQuestion(question);

        service.updateAuditExpirationDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getExpiresDate()));
    }

    @Test
    public void testUpdateAuditExpirationDateBasedOnAnswer_policyExpirationDatePlus120() {
        Date date = DateBean.parseDate("2014-04-25");
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("policyExpirationDatePlus120");
        data.setQuestion(question);

        service.updateAuditExpirationDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getExpiresDate()));
    }

    @Test
    public void testUpdateAuditExpirationDateBasedOnAnswer_policyExpirationDatePlusMonthsToExpire() {
        Date date = DateBean.getNextDayMidnight(DateBean.setToEndOfDay(DateBean.parseDate("2016-12-25")));
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        audit.getAuditType().setMonthsToExpire(36);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("policyExpirationDatePlusMonthsToExpire");
        data.setQuestion(question);
        data.setAudit(audit);

        service.updateAuditExpirationDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getExpiresDate()));
    }

    @Test
    public void testUpdateAuditExpirationDateBasedOnAnswer_exipireMonths12() {
        Date date = DateBean.setToEndOfDay(DateBean.parseDate("2014-12-25"));
        ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
        AuditData data = EntityFactory.makeAuditData("2013-12-25");
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.setUniqueCode("exipireMonths12");
        data.setQuestion(question);

        service.updateAuditExpirationDateBasedOnAnswer(audit, data);
        assertTrue(date.equals(audit.getExpiresDate()));
    }

    @Test
    public void testExtendExpirationOfAttachedCertificates_CertNoDateAuditNoDate() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 5);
        Date expected = cal.getTime();

        ContractorAudit audit = createCertAudit(null, null);
        service.extendExpirationOfAttachedCertificates(audit);
        assertEquals(1, compareDate(expected));
    }

    @Test
    public void testExtendExpirationOfAttachedCertificates_NoExtendDate() throws Exception {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, 6);
        Date auditExpiration = cal.getTime();

        cal.add(Calendar.MONTH, 2);
        Date certExpiration = cal.getTime();

        ContractorAudit audit = createCertAudit(auditExpiration, certExpiration);
        service.extendExpirationOfAttachedCertificates(audit);
        assertEquals(0, compareDate(certExpiration));
    }

    @Test
    public void testExtendExpirationOfAttachedCertificates_ExtendDate() throws Exception {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, 6);
        Date certExpiration = cal.getTime();

        cal.add(Calendar.MONTH, 2);
        Date auditExpiration = cal.getTime();

        ContractorAudit audit = createCertAudit(auditExpiration, certExpiration);
        service.extendExpirationOfAttachedCertificates(audit);
        assertEquals(1, compareDate(certExpiration));
    }

    private int compareDate(Date expectedDate) {
        Date actualDate = certificates.get(0).getExpirationDate();
        return actualDate.compareTo(expectedDate);
    }

    private ContractorAudit createCertAudit(Date expirationDate, Date certificateDate){
        Certificate certificate = new Certificate();
        certificate.setId(1);
        certificate.setExpirationDate(certificateDate);

        certificates.clear();
        certificates.add(certificate);

        ContractorAudit audit = new ContractorAudit();
        audit.setExpiresDate(expirationDate);

        ContractorAccount contractor = new ContractorAccount();
        audit.setContractorAccount(contractor);

        AuditQuestion question = new AuditQuestion();
        question.setQuestionType(AuditQuestion.TYPE_FILE_CERTIFICATE);

        AuditData data = new AuditData();
        data.setAnswer("" + certificate.getId());
        data.setQuestion(question);

        audit.getData().add(data);

        return audit;
    }
}
