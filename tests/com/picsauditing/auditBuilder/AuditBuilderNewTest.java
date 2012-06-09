package com.picsauditing.auditBuilder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;

/* 
 * When we feel this test is doing its job, we should delete the old one and rename this one - GAM
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class AuditBuilderNewTest {
	private AuditBuilder auditBuilder;
	final private int MOCK_CONTRACTOR_ID = 4567; // arbitrary number 
	
	@Mock
	private ContractorAudit conAudit;
	@Mock
	private AuditType auditType;
	@Mock
	private AuditDataDAO auditDataDAO;
	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private AuditData auditData;
	@Mock
	private AuditTypeRuleCache typeRuleCache;
	@Mock
	private AuditCategoryRuleCache categoryRuleCache;
	@Mock
	private AuditTypeDetail AuditTypeDetail;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		auditBuilder = new AuditBuilder();
	}

	@Test
	public void testIsValidCorAudit_IsValidAuditDataAnswerIsYes() throws Exception {
		when(auditType.getId()).thenReturn(AuditType.COR);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(conAudit.isExpired()).thenReturn(false);
		when(conAudit.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getId()).thenReturn(MOCK_CONTRACTOR_ID);
		when(auditDataDAO.findAnswerByConQuestion(MOCK_CONTRACTOR_ID, AuditQuestion.COR))
			.thenReturn(auditData);
		when(auditData.getAnswer()).thenReturn("Yes");
		Whitebox.setInternalState(auditBuilder, "auditDataDAO", auditDataDAO);
		
		Boolean isValid = 
				Whitebox.invokeMethod(auditBuilder, "isValidCorAudit", conAudit);
		
		assertTrue(isValid);
	}
	
	@Test
	public void testIsValidCorAudit_NotValidAuditDataAnswerNotYes() throws Exception {
		when(auditType.getId()).thenReturn(AuditType.COR);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(conAudit.isExpired()).thenReturn(false);
		when(conAudit.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getId()).thenReturn(MOCK_CONTRACTOR_ID);
		when(auditDataDAO.findAnswerByConQuestion(MOCK_CONTRACTOR_ID, AuditQuestion.COR))
			.thenReturn(auditData);
		when(auditData.getAnswer()).thenReturn("No");
		Whitebox.setInternalState(auditBuilder, "auditDataDAO", auditDataDAO);
		
		Boolean isValid = 
				Whitebox.invokeMethod(auditBuilder, "isValidCorAudit", conAudit);
		
		assertFalse(isValid);
	}
	
	@Test
	public void testIsValidCorAudit_IsValidDueToNullAuditData() throws Exception {
		when(auditType.getId()).thenReturn(AuditType.COR);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(conAudit.isExpired()).thenReturn(false);
		when(conAudit.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getId()).thenReturn(MOCK_CONTRACTOR_ID);
		when(auditDataDAO.findAnswerByConQuestion(MOCK_CONTRACTOR_ID, AuditQuestion.COR))
			.thenReturn(null);
		Whitebox.setInternalState(auditBuilder, "auditDataDAO", auditDataDAO);
		
		Boolean isValid = 
				Whitebox.invokeMethod(auditBuilder, "isValidCorAudit", conAudit);
		
		assertTrue(isValid);
	}
	
	@Test
	public void testIsValidCorAudit_NotValidIsCorButExpired() throws Exception {
		when(auditType.getId()).thenReturn(AuditType.COR);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(conAudit.isExpired()).thenReturn(true);
		
		Boolean isValid = 
				Whitebox.invokeMethod(auditBuilder, "isValidCorAudit", conAudit);
		
		assertFalse(isValid);
	}
	
	@Test
	public void testIsValidCorAudit_NotValidTypeBecauseNotCor() throws Exception {
		when(auditType.getId()).thenReturn(AuditType.PQF);
		when(conAudit.getAuditType()).thenReturn(auditType);
		
		Boolean isValid = 
				Whitebox.invokeMethod(auditBuilder, "isValidCorAudit", conAudit);
		
		assertFalse(isValid);
	}
	
	@Test
	public void testX_UC() throws Exception {
		assertTrue(true);
	}

}
