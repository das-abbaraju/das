package com.picsauditing.actions.contractors;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.menu.builder.AuditMenuBuilder;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

public class ContractorActionSupportTest extends PicsTest {
	ContractorActionSupport contractorActionSupport;

	ContractorAccount contractor;
	OperatorAccount operator;
	OperatorAccount anotherOperator;
	List<ContractorOperator> operators = new ArrayList<ContractorOperator>();

	List<Certificate> certList = new ArrayList<Certificate>();
	Map<Integer, List<Integer>> opIdsByCertIds = new HashMap<Integer, List<Integer>>();
	List<AuditData> eventQuestions = new ArrayList<AuditData>();

	@Mock
	ContractorAudit audit;
	@Mock
	private Permissions permissions;
	@Mock
	private AuditBuilder auditBuilder;
	@Mock
	protected ContractorAccountDAO contractorAccountDAO;
	@Mock
	protected ContractorAuditDAO auditDAO;
	@Mock
	private CertificateDAO certDAO;
	@Mock
	private OperatorAccountDAO operatorDAO;
	@Mock
	private AuditDataDAO auditDataDAO;
	@Mock
	private NoteDAO noteDAO;
	@Mock
	private AuditPercentCalculator auditPercentCalculator;
	@Mock
	private FeatureToggle featureToggle;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();

		contractorActionSupport = new ContractorActionSupport();
		autowireDAOsFromDeclaredMocks(contractorActionSupport, this);

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		anotherOperator = EntityFactory.makeOperator();
		operators.add(EntityFactory.addContractorOperator(contractor, operator));
		operators.add(EntityFactory.addContractorOperator(contractor, anotherOperator));

		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "auditPercentCalculator", auditPercentCalculator);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "auditBuilder", auditBuilder);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "featureToggle", featureToggle);

		eventQuestions = new ArrayList<AuditData>();
		for (int i = 0; i < 3; i++) {
			eventQuestions.add(EntityFactory.makeAuditData(""));
		}

	}

    @Test
    public void testPreviousStep_Clients() throws Exception {
        when(permissions.isContractor()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(contractor.getId());
        when(contractorAccountDAO.find(contractor.getId())).thenReturn(contractor);

        contractorActionSupport.setCurrentStep(ContractorRegistrationStep.Clients);
        String result = contractorActionSupport.previousStep();
        assertEquals(PicsActionSupport.SUCCESS, result);
    }

    @Test
    public void testPreviousStep_Risk() throws Exception {
        when(permissions.isContractor()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(contractor.getId());
        when(contractorAccountDAO.find(contractor.getId())).thenReturn(contractor);

        contractorActionSupport.setCurrentStep(ContractorRegistrationStep.Risk);
        String result = contractorActionSupport.previousStep();
        assertEquals(PicsActionSupport.REDIRECT, result);
    }

    @Test
    public void testPreviousStep_Payment() throws Exception {
        when(permissions.isContractor()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(contractor.getId());
        when(contractorAccountDAO.find(contractor.getId())).thenReturn(contractor);

        contractorActionSupport.setCurrentStep(ContractorRegistrationStep.Payment);
        String result = contractorActionSupport.previousStep();
        assertEquals(PicsActionSupport.REDIRECT, result);
    }

    @Test
	public void testGetCertificates() {
		initCertificates();

		List<Certificate> certificates = contractorActionSupport.getCertificates();
		assertEquals(1, certificates.size());
	}

	@Test
	public void testGetOperatorsUsingCertificate() {
		initCertificates();
		List<OperatorAccount> operators = contractorActionSupport.getOperatorsUsingCertificate(1);
		assertEquals(1, operators.size());
	}

	private void initCertificates() {
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "certificateDAO", certDAO);
		when(certDAO.findByConId(contractor.getId(), permissions, true)).thenReturn(certList);
		when(certDAO.findOpsMapByCert(Matchers.anyListOf(Integer.class))).thenReturn(opIdsByCertIds);

		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "certificateDAO", certDAO);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "operatorDAO", operatorDAO);
		PicsTestUtil.forceSetPrivateField(contractorActionSupport, "operators", operators);

		operator.setType("Operator");
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getTopAccountID()).thenReturn(operator.getId());
		when(operatorDAO.find(Matchers.anyInt())).thenReturn(operator);

		Certificate cert1 = new Certificate();
		Certificate cert2 = new Certificate();
		cert1.setId(1);
		cert2.setId(2);
		certList.add(cert1);
		certList.add(cert2);

		List<Integer> opIds = new ArrayList<Integer>();
		opIds.add(operator.getId());
		opIdsByCertIds.put(cert1.getId(), opIds);
		opIdsByCertIds.put(cert2.getId(), new ArrayList<Integer>());
	}

	@Test
	public void testReviewCategoriesNullPQF() {
		when(auditDAO.findPQF(anyInt())).thenReturn(null);
		contractorActionSupport.setContractor(contractor);
		contractorActionSupport.reviewCategories(EventType.Locations);
		verify(auditBuilder, never()).buildAudits(contractor);
		verify(auditDataDAO, never()).findWhere(anyString());
		verify(auditDAO, never()).save(any(ContractorAuditOperatorWorkflow.class));
		verify(noteDAO, never()).save(any(Note.class));
	}

	@Test
	public void testReviewCategoriesPendingPQF() {
		when(auditDAO.findPQF(anyInt())).thenReturn(audit);
		when(audit.hasCaoStatusAfter((AuditStatus) any())).thenReturn(false);
		contractorActionSupport.setContractor(contractor);
		contractorActionSupport.reviewCategories(EventType.Locations);
		verify(auditBuilder, never()).buildAudits(contractor);
	}

	@Test
	public void testReviewCategoriesNoEventQuestions() {
		when(auditDAO.findPQF(anyInt())).thenReturn(audit);
		when(audit.hasCaoStatusAfter((AuditStatus) any())).thenReturn(true);
		when(auditDataDAO.findWhere(anyString())).thenReturn(Collections.EMPTY_LIST);
		contractorActionSupport.setContractor(contractor);
		contractorActionSupport.reviewCategories(EventType.Locations);
		verify(auditBuilder, never()).buildAudits(contractor);
	}

	@Test
	public void testReviewCategoriesSuccess() {
		when(auditDAO.findPQF(anyInt())).thenReturn(audit);
		when(audit.hasCaoStatusAfter((AuditStatus) any())).thenReturn(true);
		when(auditDataDAO.findWhere(anyString())).thenReturn(eventQuestions);
		contractorActionSupport.setContractor(contractor);
		contractorActionSupport.reviewCategories(EventType.Locations);
		verify(auditBuilder, times(1)).buildAudits(contractor);
	}

	@Test
	public void testGetV6MenuComponents_DocuGUARDMenu() throws Exception {
		testMenu(AuditMenuBuilder.Service.DOCUGUARD, "AuditType.1.name", "DocuGUARD");
	}

	@Test
	public void testGetV6MenuComponents_InsureGUARDMenu() throws Exception {
		testMenu(AuditMenuBuilder.Service.INSUREGUARD, "global.InsureGUARD", "InsureGUARD");
	}


	@Test
	public void testGetV6MenuComponents_EmployeeGUARDMenu() throws Exception {
		testMenu(AuditMenuBuilder.Service.EMPLOYEEGUARD, "global.EmployeeGUARD", "EmployeeGUARD");
	}

	@Test
	public void testGetV6MenuComponents_AuditGUARDMenu() throws Exception {
		testMenu(AuditMenuBuilder.Service.AUDITGUARD, "global.AuditGUARD", "AuditGUARD");
	}

	@Test
	public void testGetV6MenuComponents_ClientReviewsMenu() throws Exception {
		testMenu(AuditMenuBuilder.Service.CLIENT_REVIEWS, "global.ClientReviews", "Client Reviews");
	}

	@Test
	public void testIsShowHeader_Contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		assertTrue(contractorActionSupport.isShowHeader());
	}

	@Test
	public void testShowContractorSubmenu_HasContractorDetailsPermission() {
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		OperatorAccount operator = mock(OperatorAccount.class);

		when(contractorAccountDAO.findOperators(contractor, permissions, " AND operatorAccount.type IN ('Operator')"))
				.thenReturn(Arrays.asList(contractorOperator));
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getId()).thenReturn(123);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.isUsingVersion7Menus()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorDetails)).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);

		assertTrue(contractorActionSupport.isShowContractorSubmenu());
	}

	@Test
	public void testShowContractorSubmenu_NullPermissions() {
		when(permissions.isContractor()).thenReturn(false);
		permissions = null;

		assertFalse(contractorActionSupport.isShowContractorSubmenu());
	}

	@Test
	public void testShowContractorSubmenu_UserIsContractor() {
		when(permissions.isContractor()).thenReturn(true);

		assertFalse(contractorActionSupport.isShowContractorSubmenu());
	}

	@Test
	public void testShowContractorSubmenu_DoesNotHaveV7Menus() {
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isUsingVersion7Menus()).thenReturn(false);

		assertFalse(contractorActionSupport.isShowContractorSubmenu());
	}

	private void testMenu(AuditMenuBuilder.Service service, String translationKey, String displayText) throws Exception {
		MenuComponent header = mock(MenuComponent.class);

		Map<AuditMenuBuilder.Service, List<MenuComponent>> menu = new HashMap<>();
		menu.put(service, new ArrayList<MenuComponent>());
		menu.get(service).add(header);

		when(translationService.hasKey(eq(translationKey), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq(translationKey), any(Locale.class), anyList())).thenReturn(displayText);

		List<MenuComponent> v6Menu = Whitebox.invokeMethod(contractorActionSupport, "getV6MenuComponents", menu);

		assertNotNull(v6Menu);
		assertFalse(v6Menu.isEmpty());

		verify(header).setName(displayText);
	}
}
