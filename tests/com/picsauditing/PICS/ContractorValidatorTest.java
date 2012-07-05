package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

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
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;

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
		AuditData officeLocationAnswer = setupOfficeLocationTest("CA", "AB",
				"CA", "BC", true);
		
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);

		assertEquals("YesWithOffice", officeLocationAnswer.getAnswer());
	}

	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_SwitchToCalifornia() {
		AuditData officeLocationAnswer = setupOfficeLocationTest("US", "CA",
				"US", "TX", true);
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
		
		assertEquals("YesWithOffice", officeLocationAnswer.getAnswer());
	}
	
	
	@Test
	public void testSetOfficeLocationInPqfBasedOffOfAddress_NoAuditData() {
		setupOfficeLocationTest("US", "CA",	"US", "TX", false);
		
		ArgumentCaptor<AuditData> officeLocationAnswer = ArgumentCaptor.forClass(AuditData.class);
				
		contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
		verify(auditDataDao).save(officeLocationAnswer.capture());
				
		assertEquals("YesWithOffice", officeLocationAnswer.getValue().getAnswer());
	}

	@SuppressWarnings("unchecked")
	private AuditData setupOfficeLocationTest(String newCountry,
			String newState, String oldCountry, String oldState, boolean auditDataFindable) {
		setCountryAndState(contractor, newCountry, newState);
		ContractorAccount previousContractorSettings = new ContractorAccount();
		setCountryAndState(previousContractorSettings, oldCountry, oldState);

		when(contractorAccountDao.find(contractor.getId())).thenReturn(
				previousContractorSettings);

		List<String> uniqueCodes = new ArrayList<String>();
		uniqueCodes.add(newCountry + "." + newState);
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

	private void setCountryAndState(ContractorAccount contractor,
			String countryIsoCode, String stateIsoCode) {
		contractor.setCountry(new Country(countryIsoCode));
		contractor.setState(new State(stateIsoCode));
	}
}
