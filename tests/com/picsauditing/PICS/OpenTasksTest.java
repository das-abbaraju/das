package com.picsauditing.PICS;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Workflow;

public class OpenTasksTest {
	private OpenTasks action;
	private ContractorAccount contractor;
	// private Map<OperatorAccount, Set<OperatorAccount>> caos;
	private User user;
	private Permissions permissions;
	private OperatorAccount operator;

	@Before
	public void setUp() throws Exception {
		action = new OpenTasks();
		contractor = EntityFactory.makeContractor();
		user = EntityFactory.makeUser();
		permissions = EntityFactory.makePermission(user);
		operator = EntityFactory.makeOperator();

	}

	@Test
	public void testIsOpenTaskNeeded_PendingManualAudit() throws Exception {
		ContractorAudit manualAudit = setUpAudit(AuditType.DESKTOP);
		
		ContractorAuditOperator cao = EntityFactory.addCao(manualAudit, operator);
		cao.changeStatus(AuditStatus.Pending,permissions);
		assertTrue(action.isOpenTaskNeeded(manualAudit, user ,permissions ));
	}

	private ContractorAudit setUpAudit(int auditType) {
		ContractorAudit audit = EntityFactory.makeContractorAudit(auditType,contractor);
		
		Workflow workFlow = new Workflow();
		if (auditType == AuditType.DESKTOP) {
			workFlow.setId(Workflow.MANUAL_AUDIT_WORKFLOW);
		}
		audit.getAuditType().setWorkFlow(workFlow);
		return audit;
	}


}
