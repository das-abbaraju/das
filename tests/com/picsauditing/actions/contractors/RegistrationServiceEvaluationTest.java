package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.util.PermissionToViewContractor;

public class RegistrationServiceEvaluationTest extends PicsTest{
	RegistrationServiceEvaluation serviceEvaluation;
	
	@Mock
	private Permissions permissions;
	@Mock
	protected ContractorAccountDAO contractorAccountDao;
	@Mock
	PermissionToViewContractor permissionToViewContractor;
	@Mock
	protected ContractorAuditDAO auditDao;
	@Mock
	protected BasicDAO dao;

	private ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		serviceEvaluation = new RegistrationServiceEvaluation();
		autowireEMInjectedDAOs(serviceEvaluation);

		contractor = EntityFactory.makeContractor();
		contractor.setAccountLevel(AccountLevel.Full);
		contractor.setStatus(AccountStatus.Pending);
		serviceEvaluation.setId(contractor.getId());
		
		when(permissionToViewContractor.check(Matchers.anyBoolean())).thenReturn(true);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(contractorAccountDao.find(Matchers.anyInt())).thenReturn(contractor);
		
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "permissions",
				permissions);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractorAccountDao",
				contractorAccountDao);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "permissionToViewContractor",
				permissionToViewContractor);
}

	@Test
	public void testNextStep_MaterialSupplier() throws Exception  {
		contractor.setProductRisk(LowMedHigh.None);
		List<ContractorType> serviceTypes = new ArrayList<ContractorType>();
		serviceTypes.add(ContractorType.Supplier);
		contractor.setAccountTypes(serviceTypes);
		
		Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
		answerMap.put(7679, EntityFactory.makeAuditData("High", 7679));
		
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "answerMap", answerMap);
		
		Whitebox.invokeMethod(serviceEvaluation, "calculateRiskLevels");
		assertEquals("High", contractor.getProductRisk().toString());
	}

	@Test
	public void testNextStep_TransportationServices() throws Exception  {
		contractor.setTransportationRisk(LowMedHigh.None);
		List<ContractorType> serviceTypes = new ArrayList<ContractorType>();
		serviceTypes.add(ContractorType.Transportation);
		contractor.setAccountTypes(serviceTypes);
		
//		Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
//		answerMap.put(7679, EntityFactory.makeAuditData("High", 7679));
		
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
//		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "answerMap", answerMap);
		
		Whitebox.invokeMethod(serviceEvaluation, "calculateRiskLevels");
		assertEquals("Low", contractor.getTransportationRisk().toString());
	}
	
	@Test
	public void testValidateAnswers_MaterialSupplier() {
		contractor.setProductRisk(LowMedHigh.High);
		List<ContractorType> serviceTypes = new ArrayList<ContractorType>();
		serviceTypes.add(ContractorType.Supplier);
		contractor.setAccountTypes(serviceTypes);

		AuditCategory productCategory = new AuditCategory();
		productCategory.setId(1683);
		AuditCategory businessCategory = new AuditCategory();
		businessCategory.setId(1682);
		
		productCategory.getQuestions().add(createQuestion(7679));
		businessCategory.getQuestions().add(createQuestion(7660));
		businessCategory.getQuestions().add(createQuestion(7661));
		
		Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
		answerMap.put(7679, EntityFactory.makeAuditData("High", 7679));
		answerMap.put(7660, EntityFactory.makeAuditData("Yes", 7660));
		answerMap.put(7661, EntityFactory.makeAuditData("Yes", 7661));

		when(dao.find(AuditCategory.class, 1683)).thenReturn(productCategory);
		when(dao.find(AuditCategory.class, 1682)).thenReturn(businessCategory);

		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "answerMap", answerMap);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "dao", dao);

		boolean valid = serviceEvaluation.validateAnswers();
		assertTrue(valid);
	}
	
	private AuditQuestion createQuestion(int id) {
		AuditQuestion question = new AuditQuestion();
		question.setId(id);
		Calendar effective = Calendar.getInstance();
		effective.add(Calendar.YEAR, -10);
		question.setEffectiveDate(effective.getTime());
		Calendar expires = Calendar.getInstance();
		expires.add(Calendar.YEAR, 10);
		question.setExpirationDate(expires.getTime());
		
		return question;
	}
}
