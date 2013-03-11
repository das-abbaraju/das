package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import org.mockito.internal.util.reflection.Whitebox;

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
    // This test covers ticket: PICS-9473
    public void testGetAuditMenu_OperatorViewingPendingContractorAccount() {
        Permissions permissions = Mockito.mock(Permissions.class);
        when(permissions.isOperatorCorporate()).thenReturn(true);

        contractor = Mockito.mock(ContractorAccount.class);
        when(contractor.getStatus()).thenReturn(AccountStatus.Pending);

        ContractorActionSupport contractorActionSupport = new ContractorActionSupport();

        contractorActionSupport.setContractor(contractor);
        Whitebox.setInternalState(contractorActionSupport, "permissions", permissions);

        List<MenuComponent> result = contractorActionSupport.getAuditMenu();

        assertTrue(result.isEmpty());
    }

    @Test
    // This test covers ticket: PICS-9473
    public void testGetAuditMenu_OperatorViewingRequestedContractorAccount() {
        Permissions permissions = Mockito.mock(Permissions.class);
        when(permissions.isOperatorCorporate()).thenReturn(true);

        contractor = Mockito.mock(ContractorAccount.class);
        when(contractor.getStatus()).thenReturn(AccountStatus.Requested);

        ContractorActionSupport contractorActionSupport = new ContractorActionSupport();

        contractorActionSupport.setContractor(contractor);
        Whitebox.setInternalState(contractorActionSupport, "permissions", permissions);

	    List<MenuComponent> result = contractorActionSupport.getAuditMenu();
	    assertTrue(result.isEmpty());
    }

	@Test
	public void testGetAuditMenu_ReviewDocuments() {
		Permissions permissions = Mockito.mock(Permissions.class);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		contractor = Mockito.mock(ContractorAccount.class);

		ContractorAudit conAudit = EntityFactory.makeContractorAudit(200, contractor);
		audit.setEffectiveDate(new Date());
		conAudit.getAuditType().setClassType(AuditTypeClass.Review);
		conAudit.getAuditType().setCanContractorView(true);
		conAudit.setContractorAccount(contractor);
		List<ContractorAudit> list = new ArrayList<ContractorAudit>();
		list.add(conAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		ContractorAuditOperator cao = EntityFactory.addCao(conAudit, operator);
		cao.setStatus(AuditStatus.Pending);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setOperator(operator);
		cao.getCaoPermissions().add(caop);
		ContractorOperator co = new ContractorOperator();
		co.setOperatorAccount(operator);
		List<ContractorOperator> ops = new ArrayList<ContractorOperator>();
		ops.add(co);

		when(contractor.getAudits()).thenReturn(list);
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		when(contractor.getOperators()).thenReturn(ops);
		when(contractor.getNonCorporateOperators()).thenReturn(ops);

		ContractorActionSupport contractorActionSupport = new ContractorActionSupport();

		contractorActionSupport.setContractor(contractor);
		Whitebox.setInternalState(contractorActionSupport, "permissions", permissions);
		Whitebox.setInternalState(contractorActionSupport, "featureToggle", featureToggle);

		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_SHOW_REVIEW_DOC_IN_AUDITGUARD)).thenReturn(true);
		List<MenuComponent> result = contractorActionSupport.getAuditMenu();
		assertTrue(result.size() == 3);
		assertTrue(result.get(1).getChildren().size() == 1);
		assertTrue(result.get(2).getChildren().size() == 1);

		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_SHOW_REVIEW_DOC_IN_AUDITGUARD)).thenReturn(false);
		result = contractorActionSupport.getAuditMenu();
		assertTrue(result.size() == 2);
		assertTrue(result.get(1).getChildren().size() == 1);
	}
}
