package com.picsauditing.jpa.entities;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.Permissions;

@SuppressWarnings("deprecation")
public class ContractorAuditOperatorTest {

	ContractorAuditOperator cao;
	
	@Mock Permissions permissions;
	@Mock ContractorAudit audit;
	@Mock User user;
	@Mock OperatorAccount operator;
	
	@Before
	public void setUp() throws Exception {
		cao = new ContractorAuditOperator();
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testChangeStatus_SameStatus() {
		cao.setStatus(AuditStatus.Submitted);
		assertNull(cao.changeStatus(AuditStatus.Submitted, permissions));
		assertEquals(AuditStatus.Submitted, cao.getStatus());
	}
	
	@Test
	public void testChangeStatus_AuditSubStatusShouldBeNull() {
		when(audit.getAuditType()).thenReturn(new AuditType(145));
		cao.setAudit(audit);
		cao.setAuditSubStatus(AuditSubStatus.LimitsNotMet);
		
		cao.changeStatus(AuditStatus.Approved, permissions);
		
		assertNull(cao.getAuditSubStatus());
		assertEquals(AuditStatus.Approved, cao.getStatus());
		assertNotNull(cao.getEffectiveDate());
	}
	
	@Test
	public void testChangeStatus_AuditTypePQF() {
		when(audit.getAuditType()).thenReturn(new AuditType(AuditType.PQF));
		cao.setAudit(audit);
		cao.setId(123);
		cao.setStatus(AuditStatus.Pending);
		cao.setAuditSubStatus(AuditSubStatus.Other);
		when(permissions.getUserId()).thenReturn(48789);
		
		ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Complete, permissions);
	
		assertEquals(123, caow.getCao().getId());
		assertEquals(AuditStatus.Pending, caow.getPreviousStatus());
		assertEquals(AuditStatus.Complete, caow.getStatus());
		assertEquals(48789, caow.getCreatedBy().getId());
		assertNotNull(cao.getStatusChangedDate());
		assertNotNull(cao.getUpdateDate());
		assertNull(cao.getAuditSubStatus());
	}
	
	@Test
	public void testChangeStatus_AuditTypeAnnualUpdate() {
		when(audit.getAuditType()).thenReturn(new AuditType(AuditType.ANNUALADDENDUM));
		cao.setAudit(audit);
		cao.setId(123);
		cao.setStatus(AuditStatus.Pending);
		cao.setAuditSubStatus(AuditSubStatus.Other);
		when(permissions.getUserId()).thenReturn(48789);
		
		ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Complete, permissions);
	
		assertEquals(123, caow.getCao().getId());
		assertEquals(AuditStatus.Pending, caow.getPreviousStatus());
		assertEquals(AuditStatus.Complete, caow.getStatus());
		assertEquals(48789, caow.getCreatedBy().getId());
		assertNotNull(cao.getStatusChangedDate());
		assertNotNull(cao.getUpdateDate());
		assertNull(cao.getAuditSubStatus());
	}
	
	@Test
	public void testChangeStatus_Pending() {
		when(audit.getAuditType()).thenReturn(new AuditType(145));		
		cao.setAudit(audit);
		cao.setStatus(AuditStatus.Complete);
		cao.setAuditSubStatus(AuditSubStatus.CertificateHolder);
		cao.setId(9);
		
		ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Pending, permissions);
				
		verify(audit, times(0)).setEffectiveDate(any(Date.class));
		assertNull(cao.getAuditSubStatus());
		assertEquals(9, caow.getCao().getId());
	}
	
	@Test
	public void testGetEffectiveDate_PendingAuditNotNull() {
		Date auditAssignedDate = new Date(2012, 6, 10);
		
		when(user.getId()).thenReturn(789);
		when(audit.getAuditor()).thenReturn(user);
		when(audit.getAssignedDate()).thenReturn(auditAssignedDate);
		cao.setAudit(audit);
		
		assertEquals(auditAssignedDate, cao.getEffectiveDate());
	}
	
	@Test
	public void testGetEffectiveDate_NotPending() {
		Date statusChangedDate = new Date(2012, 5, 15);
		
		cao.setAudit(audit);
		cao.setStatusChangedDate(statusChangedDate);
		cao.setStatus(AuditStatus.Complete);
		
		assertEquals(statusChangedDate, cao.getEffectiveDate());
	}
	
	@Test
	public void testIsVisibleTo_InvisibleCAO() {
		cao.setVisible(false);
		assertFalse(cao.isVisibleTo(permissions));
	}
	
	@Test
	public void testIsVisibleTo_IsContractor() {
		when(permissions.isContractor()).thenReturn(true);
		assertTrue(cao.isVisibleTo(permissions));
	}
	
	@Test
	public void testIsVisibleTo_IsPicsEmployee() {
		when(permissions.isPicsEmployee()).thenReturn(true);
		assertTrue(cao.isVisibleTo(permissions));
	}
	
	@Test
	public void testIsVisibleTo_OperatorAccount() {
		when(operator.getId()).thenReturn(3);
		cao.setOperator(operator);
		when(permissions.getAccountId()).thenReturn(3);
		
		assertTrue(cao.isVisibleTo(permissions));
	}
	
	public void testIsVisibleTo_CorporateOperator() {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(cao.isVisibleTo(permissions));
		verify(permissions, times(1)).isOperatorCorporate();
	}
	
	@Test
	public void testIsVisibleTo_CorporateChildPermission() {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(new HashSet<Integer>(Arrays.asList(9444)));
		List<ContractorAuditOperatorPermission> caops = buildMockListOfCaop();
		cao.setCaoPermissions(caops);
		cao.setOperator(operator);
		
		assertTrue(cao.isVisibleTo(permissions));
		verify(permissions, times(1)).getOperatorChildren();
	}
	
	private List<ContractorAuditOperatorPermission> buildMockListOfCaop() {
		List<ContractorAuditOperatorPermission> caops = new ArrayList<ContractorAuditOperatorPermission>();
		
		ContractorAuditOperatorPermission caop = Mockito.mock(ContractorAuditOperatorPermission.class);
		when(operator.getId()).thenReturn(9444);
		when(caop.getOperator()).thenReturn(operator);
		
		caops.add(caop);
		
		return caops;
	}
	
	@Test
	public void testIsReadyToBeSubmitted_StatusApproved() {
		cao.setStatus(AuditStatus.Approved);
		cao.setPercentComplete(100);
		assertFalse(cao.isReadyToBeSubmitted());
	}
	
	@Test
	public void testIsReadyToBeSubmitted_Not_100_Percent() {
		cao.setStatus(AuditStatus.Pending);
		cao.setPercentComplete(99);
		assertFalse(cao.isReadyToBeSubmitted());
	}	
	
	@Test
	public void testIsReadyToBeSubmitte_Pending() {
		cao.setStatus(AuditStatus.Pending);
		cao.setPercentComplete(100);
		assertTrue(cao.isReadyToBeSubmitted());
	}
	
	@Test
	public void testIsReadyToBeSubmitte_Resubmit() {
		cao.setStatus(AuditStatus.Resubmit);
		cao.setPercentComplete(100);
		assertTrue(cao.isReadyToBeSubmitted());
	}
	
	@Test
	public void testIsReadyToBeSubmitte_Incomplete() {
		cao.setStatus(AuditStatus.Incomplete);
		cao.setPercentComplete(100);
		assertTrue(cao.isReadyToBeSubmitted());
	}
	
	@Test
	public void testIsTopCaowUserNote_NoCAOWs() {
		assertFalse(cao.isTopCaowUserNote());
		
		cao.setCaoWorkflow(null);
		assertFalse(cao.isTopCaowUserNote());
	}
	
	@Test
	public void testIsTopCaowUserNote() {
		List<ContractorAuditOperatorWorkflow> caows = buildMockCaows();
		when(caows.get(caows.size() - 1).getStatus()).thenReturn(AuditStatus.Incomplete);
		when(caows.get(caows.size() - 1).getPreviousStatus()).thenReturn(AuditStatus.Incomplete);
		when(caows.get(caows.size() - 1).getNotes()).thenReturn("Some notes");
		
		cao.setCaoWorkflow(caows);
		assertTrue(cao.isTopCaowUserNote());
	}
	
	@Test
	public void testIsTopCaowUserNote_EmptyNote() {
		List<ContractorAuditOperatorWorkflow> caows = buildMockCaows();
		when(caows.get(caows.size() - 1).getStatus()).thenReturn(AuditStatus.Incomplete);
		when(caows.get(caows.size() - 1).getPreviousStatus()).thenReturn(AuditStatus.Incomplete);
		when(caows.get(caows.size() - 1).getNotes()).thenReturn(" ");
		
		cao.setCaoWorkflow(caows);
		assertFalse(cao.isTopCaowUserNote());
	}
	
	@Test
	public void testIsTopCaowUserNote_NoneMatchingStatus() {
		List<ContractorAuditOperatorWorkflow> caows = buildMockCaows();
		when(caows.get(caows.size() - 1).getStatus()).thenReturn(AuditStatus.Approved);
		when(caows.get(caows.size() - 1).getPreviousStatus()).thenReturn(AuditStatus.Incomplete);
		when(caows.get(caows.size() - 1).getNotes()).thenReturn("Another Note");
		
		cao.setCaoWorkflow(caows);
		assertFalse(cao.isTopCaowUserNote());
	}
	
	private List<ContractorAuditOperatorWorkflow> buildMockCaows() {
		List<ContractorAuditOperatorWorkflow> caows = new ArrayList<ContractorAuditOperatorWorkflow>();
		caows.add(buildMockCaow(new Date(2010, 1, 1)));
		caows.add(buildMockCaow(new Date(2011, 1, 1)));
		caows.add(buildMockCaow(new Date(2012, 1, 1)));
		caows.add(buildMockCaow(new Date(2013, 1, 1)));
		
		return caows;
	}
	
	private ContractorAuditOperatorWorkflow buildMockCaow(Date creationDate) {
		ContractorAuditOperatorWorkflow caow = Mockito.mock(ContractorAuditOperatorWorkflow.class);
		when(caow.getCreationDate()).thenReturn(creationDate);
		return caow;
	}

}
