package com.picsauditing.actions.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailException;
import com.picsauditing.models.audits.CaoSaveModel;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.service.i18n.EchoTranslationService;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "CaoSaveTest-context.xml" })
public class CaoSaveTest extends PicsTest {
	@Mock
    private AuditPercentCalculator auditPercentCalculator;
    @Mock
    private ContractorAuditDAO auditDao;
    @Mock
    private Permissions permissions;
    @Mock
    private ContractorAccountDAO contractorAccountDao;
    @Mock
    private ContractorAuditOperatorDAO caoDAO;
    @Mock
    private ContractorAuditOperatorWorkflowDAO caowDAO;
    @Mock
    private CaoSaveModel caoSaveModel;

    private ContractorAccount contractor;
    private ContractorAudit audit;
    private ContractorAuditOperator cao;
    private WorkflowStep step;
    private CaoSave caoSave;


	@Before
	public void setUp() throws Exception {
        super.setUp();
		MockitoAnnotations.initMocks(this);

		caoSave = new CaoSave();
		Whitebox.setInternalState(caoSave, "auditPercentCalculator", auditPercentCalculator);
        Whitebox.setInternalState(caoSave, "caoSaveModel", caoSaveModel);
        Whitebox.setInternalState(caoSave, "caoDAO", caoDAO);
        Whitebox.setInternalState(caoSave, "caowDAO", caowDAO);
	}

    @Test
    public void save_PqfCaoAutoAdvancing() throws IOException, NoRightsException, RecordNotFoundException, EmailException {
        OperatorAccount operatorOne = OperatorAccount.builder().id(31).build();
        OperatorAccount operatorTwo = OperatorAccount.builder().id(32).build();
        contractor  = ContractorAccount.builder()
                .id(21)
                .operator(operatorOne)
                .operator(operatorTwo)
                .build();
        ContractorAuditOperator caoOne = ContractorAuditOperator.builder()
                .id(1)
                .operator(operatorOne)
                .status(AuditStatus.Pending)
                .percentComplete(100)
                .percentVerified(100)
                .build();
        ContractorAuditOperator caoTwo = ContractorAuditOperator.builder()
                .id(2)
                .operator(operatorTwo)
                .status(AuditStatus.Pending)
                .percentComplete(100)
                .percentVerified(100)
                .build();
        audit = ContractorAudit.builder()
                .id(11)
                .contractor(contractor)
                .auditType(createPqfAuditType())
                .cao(caoOne)
                .cao(caoTwo)
                .category(AuditCatData.builder()
                        .numberRequired(5)
                        .numberAnswered(5)
                        .numberVerified(5)
                        .build()
                )
                .build();

        caoOne.setAudit(audit);
        caoTwo.setAudit(audit);

        when(auditDao.find(audit.getId())).thenReturn(audit);
        when(permissions.isContractor()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(contractor.getId());
        when(contractorAccountDao.findOperators(contractor, permissions,
                " AND operatorAccount.type IN ('Operator')")).thenReturn(contractor.getOperators());
        when(contractorAccountDao.isContained(contractor)).thenReturn(true);


        Whitebox.setInternalState(caoSave, "auditDao", auditDao);
        Whitebox.setInternalState(caoSave, "contractorAccountDao", contractorAccountDao);


        caoSave.setCaoID(1);
        caoSave.setAuditID(audit.getId());
        caoSave.setContractor(contractor);
        caoSave.setPermissions(permissions);
        caoSave.setId(contractor.getId());
        caoSave.setStatus(AuditStatus.Submitted);
        caoSave.save();

        assertEquals(AuditStatus.Complete, caoOne.getStatus());
        assertEquals(AuditStatus.Complete, caoTwo.getStatus());
    }

    private AuditType createPqfAuditType() {
        return AuditType.builder()
                .id(1)
                .contractorCanView()
                .contractorCanEdit()
                .name("Test PQF")
                .workflow(Workflow.builder()
                        .step(WorkflowStep.builder()
                                .oldStatus(null)
                                .newStatus(AuditStatus.Pending)
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Pending)
                                .newStatus(AuditStatus.Submitted)
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Submitted)
                                .newStatus(AuditStatus.Incomplete)
                                .noteRequired()
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Submitted)
                                .newStatus(AuditStatus.Complete)
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Incomplete)
                                .newStatus(AuditStatus.Submitted)
                                .noteRequired()
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Resubmitted)
                                .newStatus(AuditStatus.Complete)
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Resubmitted)
                                .newStatus(AuditStatus.Resubmit)
                                .noteRequired()
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Resubmit)
                                .newStatus(AuditStatus.Resubmitted)
                                .build()
                        )
                        .step(WorkflowStep.builder()
                                .oldStatus(AuditStatus.Pending)
                                .newStatus(AuditStatus.NotApplicable)
                                .noteRequired()
                                .build()
                        )
                        .build()
                )
                .build();
    }

    private void setupIncompletSubmit() {
		contractor = EntityFactory.makeContractor();
		audit = EntityFactory.makeContractorAudit(11, contractor);
		cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());
	}

	@Test
	public void testIncompleteSubmit() throws Exception {
		setupIncompletSubmit();

		step = new WorkflowStep();
		step.setNewStatus(AuditStatus.Submitted);
		step.setOldStatus(AuditStatus.Pending);

		cao.setPercentComplete(50);
		cao.setStatus(AuditStatus.Pending);


		Whitebox.invokeMethod(caoSave, "doWorkflowStepValidation", cao, step);
		assertTrue(caoSave.getActionErrors().size() > 0);
	}
}
