package com.picsauditing.PICS;

import static org.junit.Assert.*;
import static com.picsauditing.util.Assert.*;

import java.util.Date;

//import org.apache.struts2.StrutsSpringJUnit4TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Workflow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/tests.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
public class OpenTasksTest /* extends  StrutsSpringJUnit4TestCase */ {
	private static final int ANTEA_SPECIFIC_AUDIT = 181;
	private OpenTasks action;
	private ContractorAccount contractor;
	// private Map<OperatorAccount, Set<OperatorAccount>> caos;
	private User user;
	private Permissions permissions;
	private OperatorAccount operator;

	@Before
	public void setUp() throws Exception {
		action = new OpenTasks();
	}

	@Test
	public void testIsOpenTaskNeeded_PendingManualAudit() throws Exception {
		setUpBasicModelObjects();
		ContractorAudit manualAudit = setUpAudit(AuditType.DESKTOP, Workflow.MANUAL_AUDIT_WORKFLOW);
		manualAudit.getAuditType().setCanContractorEdit(false);
		
		ContractorAuditOperator cao = EntityFactory.addCao(manualAudit, operator);
		cao.changeStatus(AuditStatus.Pending,permissions);
		assertTrue(action.isOpenTaskNeeded(manualAudit, user ,permissions ));
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesMissing() throws Exception {
		setUpBasicModelObjects();
		action.initializeForGatheringTasks(contractor, user);
		action.gatherTasksAboutDeclaringTrades();
		assertEquals(1,action.openTasks.size());
		assertContains("<a href=\"https://www.picsorganizer.com/ContractorTrades.action?id="+contractor.getId()+"\">",action.openTasks.get(0));
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedButNeedsUpdate() throws Exception {
		setUpBasicModelObjects();
		contractor.getTrades().add(new ContractorTrade());
		action.initializeForGatheringTasks(contractor, user);
		action.gatherTasksAboutDeclaringTrades();
		assertEquals(1,action.openTasks.size());
		assertContains("<a href=\"https://www.picsorganizer.com/ContractorTrades.action?id="+contractor.getId()+"\">",action.openTasks.get(0));
	}

	/*
	 * PICS-4748 Antea-Specific Audit -- Contractor is seeing an outstanding task for Antea when there is actually nothing left to do.
	 */
	@Test
	public void testIsOpenTaskNeeded_SubmittedAnteaSpecificAudit() throws Exception {
		setUpBasicModelObjects();
		ContractorAudit audit = setUpAudit(ANTEA_SPECIFIC_AUDIT, Workflow.PQF_WORKFLOW);
		
		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
		cao.changeStatus(AuditStatus.Submitted,permissions);
		
		assertFalse(action.isOpenTaskNeeded(audit, user ,permissions ));
	}


	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedAndUpToDate() throws Exception {
		setUpBasicModelObjects();
		contractor.getTrades().add(new ContractorTrade());
		contractor.setTradesUpdated(new Date());
		action.initializeForGatheringTasks(contractor, user);
		action.gatherTasksAboutDeclaringTrades();
		assertEquals(0,action.openTasks.size());
		assertTrue(true);
	}

	private ContractorAudit setUpAudit(int auditType, int workflowId) {
		ContractorAudit audit = EntityFactory.makeContractorAudit(auditType,contractor);
		
		Workflow workFlow = new Workflow();
		workFlow.setId(workflowId);

		audit.getAuditType().setWorkFlow(workFlow);
		audit.getAuditType().setCanContractorEdit(true);
		return audit;
	}
	private void setUpBasicModelObjects() {
		user = EntityFactory.makeUser();
		permissions = EntityFactory.makePermission(user);

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
	}


}
