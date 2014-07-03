package com.picsauditing.auditbuilder.dao;

import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
		when(mockEntityManager.createQuery(anyString())).thenReturn(mockQuery);
	}
	
	@Test
	public void testGetAuditTypesForOperatorIdsInQuery_OpeatorAccount() {
		setupTestOperatorAccount();
		
		auditDecisionTableDAO.getAuditTypes(testOperator);
		
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		verify(mockEntityManager).createQuery((argument.capture()));
		
		assertTrue(argument.getValue().contains("opID IN (11,12,13)"));
	}
	
	@Test
	public void testGetAuditTypesForOperatorIdsInQuery_CorporateAccount() {
		setupTestOperatorAccount();
		when(testOperator.isCorporate()).thenReturn(true);
		
		auditDecisionTableDAO.getAuditTypes(testOperator);
		
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		verify(mockEntityManager).createQuery((argument.capture()));
		
		assertTrue(argument.getValue().contains("opID IN (11,12,13)"));
	}
	
	public void setupTestOperatorAccount() {
		List<Integer> parentFacilitiesIds= new ArrayList<Integer>();
		parentFacilitiesIds.add(11);
		parentFacilitiesIds.add(12);
		parentFacilitiesIds.add(13);
		
		when(testOperator.getOperatorHeirarchy()).thenReturn(parentFacilitiesIds);
	}
}
