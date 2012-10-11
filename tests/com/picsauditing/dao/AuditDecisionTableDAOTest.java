package com.picsauditing.dao;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditDecisionTableDAOTest {
	
	private AuditDecisionTableDAO auditDecisionTableDAO;
	
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private Query mockQuery;
	@Mock
	private OperatorAccount testOperator;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		auditDecisionTableDAO = new AuditDecisionTableDAO();
		auditDecisionTableDAO.setEntityManager(mockEntityManager);
		when(mockEntityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
	}
	
	@Test
	public void testGetAuditTypesForOperatorIdsInQuery_OpeatorAccount() {
		auditDecisionTableDAO.getAuditTypes(testOperator);
		
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		verify(mockEntityManager).createNativeQuery((argument.capture()));
		
		assertTrue(argument.getValue().contains("caop.opID IN (SELECT f.opID FROM facilities f WHERE f.corporateID "));
	}
	
	@Test
	public void testGetAuditTypesForOperatorIdsInQuery_CorporateAccount() {
		when(testOperator.isCorporate()).thenReturn(true);
		
		auditDecisionTableDAO.getAuditTypes(testOperator);
		
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		verify(mockEntityManager).createNativeQuery((argument.capture()));
		
		assertTrue(argument.getValue().contains("caop.opID IN (SELECT f.opID FROM facilities f WHERE f.corporateID "));
	}
}
