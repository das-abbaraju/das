package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Workflow;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenTasks.class)
public class OpenTasksTest {
	
	OpenTasks openTasks;

	@Mock Account account;
	@Mock AuditType auditType;
	@Mock ContractorAccount contractor;
	@Mock ContractorAudit audit;
	@Mock ContractorAuditOperator cao;
	@Mock User user;
	@Mock Permissions permissions;
	@Mock OperatorAccount operator;
	@Mock Workflow workFlow;

	private static final int ANTEA_SPECIFIC_AUDIT = 181;
	
	@Before
	public void setUp() throws Exception {
		openTasks = new OpenTasks();
		
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testIsOpenTaskNeeded() throws Exception {
		when(audit.getOperators()).thenReturn(null);
		Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
	}
	
	@Test
	public void testIsOpenTaskNeeded_PendingManualAudit() throws Exception {
		when(cao.isVisible()).thenReturn(true);
		when(cao.getStatus()).thenReturn(AuditStatus.Pending);
		when(audit.getOperators()).thenReturn(Arrays.asList(cao));
		when(workFlow.getId()).thenReturn(Workflow.MANUAL_AUDIT_WORKFLOW);
		when(auditType.isCanContractorEdit()).thenReturn(true);
		when(auditType.getId()).thenReturn(AuditType.DESKTOP);
		when(audit.getAuditType()).thenReturn(auditType);
		when(account.isAdmin()).thenReturn(false);
		when(user.getAccount()).thenReturn(account);
		when(permissions.hasPermission(any(OpPerms.class)))
				.then(buildAnswerForSpecificPermission(OpPerms.ContractorInsurance));
	
		boolean result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
		assertTrue(result);
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesMissing() throws Exception {
		OpenTasks mock = Mockito.mock(OpenTasks.class);
		
		@SuppressWarnings("unchecked")
		Set<ContractorTrade> mockSet = Mockito.mock(HashSet.class);
		when(mockSet.size()).thenReturn(0);
		
		when(contractor.getTrades()).thenReturn(mockSet);	
		when(contractor.getId()).thenReturn(12);
		PicsTestUtil.forceSetPrivateField(mock, "contractor", contractor);
		ArrayList<String> something = new ArrayList<String>();
		PicsTestUtil.forceSetPrivateField(mock, "openTasks", something);
		when(mock.getTextParameterized(any(String.class), anyVararg())).thenReturn("Trades missing!");
		Whitebox.invokeMethod(mock, "gatherTasksAboutDeclaringTrades");	
		
		assertEquals("Trades missing!", something.get(0));
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedButNeedsUpdate() throws Exception {
		OpenTasks mock = Mockito.mock(OpenTasks.class);
		
		@SuppressWarnings("unchecked")
		Set<ContractorTrade> mockSet = Mockito.mock(HashSet.class);
		when(mockSet.size()).thenReturn(1);
		
		when(contractor.getTrades()).thenReturn(mockSet);	
		when(contractor.getId()).thenReturn(12);
		when(contractor.isNeedsTradesUpdated()).thenReturn(true);
		PicsTestUtil.forceSetPrivateField(mock, "contractor", contractor);
		ArrayList<String> something = new ArrayList<String>();
		PicsTestUtil.forceSetPrivateField(mock, "openTasks", something);
		when(mock.getTextParameterized(any(String.class), anyVararg())).thenReturn("Update your trades!");
		Whitebox.invokeMethod(mock, "gatherTasksAboutDeclaringTrades");	
		
		assertEquals("Update your trades!", something.get(0));
	}

	/*
	 * PICS-4748 Antea-Specific Audit -- Contractor is seeing an outstanding task for Antea when there is actually nothing left to do.
	 */
	@Test
	public void testIsOpenTaskNeeded_SubmittedAnteaSpecificAudit() throws Exception {
		when(cao.isVisible()).thenReturn(true);
		when(cao.getStatus()).thenReturn(AuditStatus.Submitted);
		when(audit.getOperators()).thenReturn(Arrays.asList(cao));
		when(workFlow.getId()).thenReturn(Workflow.PQF_WORKFLOW);
		when(auditType.isCanContractorEdit()).thenReturn(true);
		when(auditType.getId()).thenReturn(ANTEA_SPECIFIC_AUDIT);
		when(audit.getAuditType()).thenReturn(auditType);
		when(account.isAdmin()).thenReturn(false);
		when(user.getAccount()).thenReturn(account);
		when(permissions.hasPermission(any(OpPerms.class)))
				.then(buildAnswerForSpecificPermission(OpPerms.ContractorInsurance));
	
		boolean result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
		assertFalse(result);
	}

	@Test(expected = NullPointerException.class)
	public void testGatherTasksAboutDeclaringTrades_NullPointerException() throws Exception {
		when(contractor.getTrades()).thenReturn(null);
		PicsTestUtil.forceSetPrivateField(openTasks, "contractor", contractor);
		Whitebox.invokeMethod(openTasks, "gatherTasksAboutDeclaringTrades");
	}
	
	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedAndUpToDate() throws Exception {
		OpenTasks mock = Mockito.mock(OpenTasks.class);
		
		@SuppressWarnings("unchecked")
		Set<ContractorTrade> mockSet = Mockito.mock(HashSet.class);
		when(mockSet.size()).thenReturn(1);
		
		when(contractor.getTrades()).thenReturn(mockSet);	
		when(contractor.getId()).thenReturn(12);
		when(contractor.isNeedsTradesUpdated()).thenReturn(false);
		PicsTestUtil.forceSetPrivateField(mock, "contractor", contractor);
		ArrayList<String> something = new ArrayList<String>();
		PicsTestUtil.forceSetPrivateField(mock, "openTasks", something);

		Whitebox.invokeMethod(mock, "gatherTasksAboutDeclaringTrades");	
		
		assertTrue(something.isEmpty());
	}	
	
	private Answer<Boolean> buildAnswerForSpecificPermission(final OpPerms permission) {
		return new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if (invocation != null && ArrayUtils.isNotEmpty(invocation.getArguments())) {
					Object argument = invocation.getArguments()[0];
					if (argument instanceof OpPerms) {
						return (permission == (OpPerms) argument);
					}
				}
					
				return false;
			}
			
		};
	}
	
}
