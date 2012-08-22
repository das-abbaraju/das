package com.picsauditing.dao;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;


public class AuditRejectionCodeDAOTest {

	AuditRejectionCodeDAO auditRejectionCodeDAO;
	
	@Before
	public void setUp() throws Exception {
		auditRejectionCodeDAO = new AuditRejectionCodeDAO();
	}
	
	@Test
	public void testGetCaopOperatorIds() throws Exception {
		List<Integer> result = Whitebox.invokeMethod(auditRejectionCodeDAO, "getCaopOperatorIds", buildFakeList());
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.get(0));
		assertTrue(2 == result.get(1));
	}
	
	private List<ContractorAuditOperatorPermission> buildFakeList() {
		ContractorAuditOperatorPermission caop1 = Mockito.mock(ContractorAuditOperatorPermission.class);
		ContractorAuditOperatorPermission caop2 = Mockito.mock(ContractorAuditOperatorPermission.class);
		
		OperatorAccount operator1 = Mockito.mock(OperatorAccount.class);
		OperatorAccount operator2 = Mockito.mock(OperatorAccount.class);
		
		when(operator1.getId()).thenReturn(1);
		when(operator2.getId()).thenReturn(2);
		
		when(caop1.getOperator()).thenReturn(operator1);
		when(caop2.getOperator()).thenReturn(operator2);
		
		return new ArrayList<ContractorAuditOperatorPermission>(Arrays.asList(caop1, caop2));
	}

}
