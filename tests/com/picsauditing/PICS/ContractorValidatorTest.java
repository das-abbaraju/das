package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.CountrySubdivision;

public class ContractorValidatorTest extends PicsTest {

	
	@Mock
	private ContractorAccount contractor;
	@Mock
	private AuditDataDAO auditDataDao;
	@Mock
	private ContractorAuditDAO contractorAuditDao;
	@Mock
	private AuditQuestionDAO auditQuestionDao;
	@Mock
	private ContractorAccountDAO contractorAccountDao;

	private ContractorValidator contractorValidator;
	
	private static final String YES_WITH_OFFICE = "YesWithOffice";
	
	@Before
	public void setup() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		contractor = EntityFactory.makeContractor();
		contractorValidator = new ContractorValidator();
		autowireEMInjectedDAOs(contractorValidator);

		PicsTestUtil.forceSetPrivateField(contractorValidator, "auditDataDao",
				auditDataDao);
		PicsTestUtil.forceSetPrivateField(contractorValidator,
				"contractorAuditDao", contractorAuditDao);
		PicsTestUtil.forceSetPrivateField(contractorValidator,
				"auditQuestionDao", auditQuestionDao);
		PicsTestUtil.forceSetPrivateField(contractorValidator,
				"contractorAccountDao", contractorAccountDao);
	}

	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_SwitchToAlberta() {
		AuditData officeLocationAnswer = setupOfficeLocationTest(
				new CountrySubdivision("CA-AB"),
				new CountrySubdivision("CA-BC"), true);
		
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);

		assertEquals(YES_WITH_OFFICE, officeLocationAnswer.getAnswer());
	}

	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_SwitchToCalifornia() {
		AuditData officeLocationAnswer = setupOfficeLocationTest(
				new CountrySubdivision("US-CA"), new CountrySubdivision("US-TX"), true);
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
		
		assertEquals(YES_WITH_OFFICE, officeLocationAnswer.getAnswer());
	}
	
	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_NoAuditData() {
		setupOfficeLocationTest(new CountrySubdivision("US-CA"), new CountrySubdivision("US-TX"), false);
		
		ArgumentCaptor<AuditData> officeLocationAnswer = ArgumentCaptor.forClass(AuditData.class);
				
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
		verify(auditDataDao).save(officeLocationAnswer.capture());
				
		assertEquals(YES_WITH_OFFICE, officeLocationAnswer.getValue().getAnswer());
	}
		
	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_NullContractor() {
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(null);
	}
	
	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_ValidateQuestionUniqeCode() {
		setupOfficeLocationTest(new CountrySubdivision("US-CA"), new CountrySubdivision("US-TX"), false);
		@SuppressWarnings({ "unchecked" })
		ArgumentCaptor<List<String>> uniqueCodePassedToAuditQuestionDao = (ArgumentCaptor<List<String>>) (Object) ArgumentCaptor.forClass(List.class);
		
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
		
		verify(auditQuestionDao).findQuestionsByUniqueCodes(uniqueCodePassedToAuditQuestionDao.capture());
		
		assertEquals("US-TX", uniqueCodePassedToAuditQuestionDao.getValue().get(0));
	}
	
	@SuppressWarnings("unchecked")
	private AuditData setupOfficeLocationTest(CountrySubdivision oldCountrySubdivision,
			CountrySubdivision newCountrySubdivision, boolean auditDataFindable) {
		contractor.setCountrySubdivision(newCountrySubdivision);
		ContractorAccount previousContractorSettings = new ContractorAccount();
		previousContractorSettings.setCountrySubdivision(oldCountrySubdivision);

		when(contractorAccountDao.find(contractor.getId())).thenReturn(
				previousContractorSettings);

		List<String> uniqueCodes = new ArrayList<String>();
		uniqueCodes.add(newCountrySubdivision.toString());
		List<AuditQuestion> mockResultSetForAuditQuestionDao = new ArrayList<AuditQuestion>();
		AuditQuestion officeLocationQuestion = new AuditQuestion();
		mockResultSetForAuditQuestionDao.add(officeLocationQuestion);
		when(auditQuestionDao.findQuestionsByUniqueCodes(any(List.class)))
				.thenReturn(mockResultSetForAuditQuestionDao);

		List<ContractorAudit> mockSetforContractorAuditDao = new ArrayList<ContractorAudit>();
		ContractorAudit testContractorsPqf = new ContractorAudit();
		AuditType pqfAuditType = new AuditType();
		pqfAuditType.setId(AuditType.PQF);
		testContractorsPqf.setAuditType(pqfAuditType);
		mockSetforContractorAuditDao.add(testContractorsPqf);

		when(contractorAuditDao.findByContractor(contractor.getId()))
				.thenReturn(mockSetforContractorAuditDao);

		AuditData officeLocationAnswer = new AuditData();
		if (auditDataFindable) {
			when(
				auditDataDao.findAnswerByAuditQuestion(
						testContractorsPqf.getId(),
						officeLocationQuestion.getId())).thenReturn(
				officeLocationAnswer);
		} else {
			when(
				auditDataDao.findAnswerByAuditQuestion(
						testContractorsPqf.getId(),
						officeLocationQuestion.getId())).thenReturn(
				null);
		}
		return officeLocationAnswer;
	}
}
