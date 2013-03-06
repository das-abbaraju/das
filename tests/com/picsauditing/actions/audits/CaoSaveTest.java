package com.picsauditing.actions.audits;

import static org.junit.Assert.assertTrue;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class CaoSaveTest extends PicsTest {
	CaoSave caoSave;
	@Mock
	AuditPercentCalculator auditPercentCalculator;

	ContractorAccount contractor;
	ContractorAudit audit;
	ContractorAuditOperator cao;
	WorkflowStep step;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		caoSave = new CaoSave();
		Whitebox.setInternalState(caoSave, "auditPercentCalculator", auditPercentCalculator);
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
