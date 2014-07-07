package com.picsauditing.service;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuditServiceTest extends PicsActionTest {

    AuditService auditService;

    @Mock
    private AuditDataDAO auditDataDAO;
    @Mock
    private NoteDAO noteDAO;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private ContractorAudit contractorAudit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        auditService = new AuditService();

        Whitebox.setInternalState(auditService, "auditDataDAO", auditDataDAO);
        Whitebox.setInternalState(auditService, "noteDAO", noteDAO);
    }

    @Test
    public void testSetSlaManualAudit() throws Exception {
        List<ContractorAuditOperator> caos = new ArrayList<>();
        List<ContractorAudit> audits = new ArrayList<>();
        when(contractor.getAudits()).thenReturn(audits);
        audits.add(contractorAudit);
        when(contractorAudit.getAuditType()).thenReturn(new AuditType(AuditType.MANUAL_AUDIT));
        when(contractorAudit.hasCaoStatus(AuditStatus.Pending)).thenReturn(true);
        when(contractorAudit.getCurrentOperators()).thenReturn(caos);

        Whitebox.invokeMethod(auditService, "setSlaManualAudit", contractor, permissions);
        Mockito.verify(contractorAudit).setSlaDate(null);
        assertNull(contractorAudit.getSlaDate());
    }

    @Test
    public void testManualAuditSlaReset_NeitherPQForSafetyManual() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao2 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());
        addCaoCaop(audit, needManualAuditCao2.getOperator());

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        // pqf & safety manual not complete, reset any existing SLA date
        audit.setSlaDate(new Date());
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() == null);

    }

    @Test
    public void testManualAuditSlaReset_SafetyManualNotVerified() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao2 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());
        addCaoCaop(audit, needManualAuditCao2.getOperator());

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        audit.setSlaDate(new Date());
        needManualAuditCao1.changeStatus(AuditStatus.Complete, null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() == null);
    }

    @Test
    public void testManualAuditSlaReset_PQFCompletButNotManualAuditOperatorOverlap() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator noNeedManualAuditCao = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao2 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());
        addCaoCaop(audit, needManualAuditCao2.getOperator());

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        audit.setSlaDate(new Date());
        noNeedManualAuditCao.changeStatus(AuditStatus.Complete, null);
        data.setAnswer("doc");
        data.setDateVerified(new Date());
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() == null);
    }

    @Test
    public void testManualAuditSlaReset_NoManualAuditCaos() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);

        audit.setSlaDate(new Date());
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() == null);
    }

    @Test
    public void testManualAuditSla_AdjustDate() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());

        ArrayList<ContractorOperator> operators = new ArrayList<>();
        ContractorOperator co = new ContractorOperator();
        co.setOperatorAccount(needManualAuditCao1.getOperator());
        operators.add(co);
        contractor.setOperators(operators);

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        Date currentSlaDate = new Date();
        audit.setSlaDate(currentSlaDate);
        data.setAnswer("doc");
        data.setDateVerified(new Date());
        needManualAuditCao1.changeStatus(AuditStatus.Complete, null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(currentSlaDate.before(audit.getSlaDate()));
    }

    @Test
    public void testManualAuditSlaReset_PqfNotComplete() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator noNeedManualAuditCao = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        ContractorAuditOperator needManualAuditCao2 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());
        addCaoCaop(audit, needManualAuditCao2.getOperator());

        AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        // pqf not complete, reset any existing SLA date
        audit.setSlaDate(new Date());
        needManualAuditCao1.changeStatus(AuditStatus.Pending, null);
        data.setAnswer("doc");
        data.setDateVerified(new Date());
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() == null);
    }

    @Test
    public void testManualAuditSlaValidPqfSafetyManual() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());

        needManualAuditCao1.changeStatus(AuditStatus.Complete, null);

        ArrayList<ContractorOperator> operators = new ArrayList<>();
        ContractorOperator co = new ContractorOperator();
        co.setOperatorAccount(needManualAuditCao1.getOperator());
        operators.add(co);
        contractor.setOperators(operators);

        AuditData data = EntityFactory.makeAuditData("doc", EntityFactory.makeAuditQuestion());
        data.setDateVerified(new Date());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        // pqf & safety manual complete, don't reset any existing SLA date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        Date currentSlaData = cal.getTime();
        audit.setSlaDate(currentSlaData);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate().equals(currentSlaData));

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 21);
        Date lestRecent = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, 7);
        Date mostRecent = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, 14);
        Date targetDate = DateBean.setToEndOfDay(date.getTime());

        // two weeks from pqf
        needManualAuditCao1.setStatusChangedDate(mostRecent);
        data.setDateVerified(lestRecent);
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));

        // two weeks from safety manual
        needManualAuditCao1.setStatusChangedDate(lestRecent);
        data.setDateVerified(mostRecent);
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));

        // from current date
        date = Calendar.getInstance();
        date.add(Calendar.DATE, 14);
        targetDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, -30);
        needManualAuditCao1.setStatusChangedDate(DateBean.setToEndOfDay(date.getTime()));
        data.setDateVerified(DateBean.setToEndOfDay(date.getTime()));
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));
    }

    @Test
    public void testManualAuditSlaValidPreviousAudit() throws Exception {
        contractor = EntityFactory.makeContractor();

        ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);
        ContractorAudit previousAudit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);

        ContractorAuditOperator needManualAuditCao1 = addCaoCaop(pqf, EntityFactory.makeOperator());
        addCaoCaop(audit, needManualAuditCao1.getOperator());
        addCaoCaop(previousAudit, needManualAuditCao1.getOperator());

        ArrayList<ContractorOperator> operators = new ArrayList<>();
        ContractorOperator co = new ContractorOperator();
        co.setOperatorAccount(needManualAuditCao1.getOperator());
        operators.add(co);
        contractor.setOperators(operators);

        needManualAuditCao1.changeStatus(AuditStatus.Complete, null);
        previousAudit.getOperators().get(0).changeStatus(AuditStatus.Complete, null);
        previousAudit.setId(3);

        AuditData data = EntityFactory.makeAuditData("doc", EntityFactory.makeAuditQuestion());
        data.setDateVerified(new Date());
        data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
        pqf.getData().add(data);

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 21);
        Date completionDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, 60);
        Date expirationDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, -30);
        Date targetDate = DateBean.setToEndOfDay(date.getTime());

        // 30 days from previous
        needManualAuditCao1.setStatusChangedDate(completionDate);
        data.setDateVerified(completionDate);
        previousAudit.setExpiresDate(expirationDate);
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));

        date = Calendar.getInstance();
        date.add(Calendar.DATE, 21);
        expirationDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, 60);
        completionDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, 14);
        targetDate = DateBean.setToEndOfDay(date.getTime());

        // from completion date
        needManualAuditCao1.setStatusChangedDate(completionDate);
        data.setDateVerified(completionDate);
        previousAudit.setExpiresDate(expirationDate);
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));

        date = Calendar.getInstance();
        date.add(Calendar.DATE, 14);
        targetDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, -21);
        completionDate = DateBean.setToEndOfDay(date.getTime());
        date.add(Calendar.DATE, -14);
        expirationDate = DateBean.setToEndOfDay(date.getTime());

        // from current date
        needManualAuditCao1.setStatusChangedDate(completionDate);
        data.setDateVerified(completionDate);
        previousAudit.setExpiresDate(expirationDate);
        audit.setSlaDate(null);
        Whitebox.invokeMethod(auditService, "checkSla", contractor, permissions);
        assertTrue(audit.getSlaDate() != null);
        assertTrue(dateMatches(targetDate, audit.getSlaDate()));
    }

    private boolean dateMatches(Date expectedDate, Date actualDate) {
        Calendar expected = Calendar.getInstance();
        expected.setTime(expectedDate);
        Calendar actual = Calendar.getInstance();
        actual.setTime(actualDate);
        if (expected.get(Calendar.YEAR) == actual.get(Calendar.YEAR) &&
                expected.get(Calendar.MONTH) == actual.get(Calendar.MONTH) &&
                expected.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    private ContractorAuditOperator addCaoCaop(ContractorAudit audit, OperatorAccount operator) {
        ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
        cao.changeStatus(AuditStatus.Pending, null);
        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        caop.setCao(cao);
        caop.setOperator(operator);
        cao.getCaoPermissions().add(caop);

        return cao;
    }
}
