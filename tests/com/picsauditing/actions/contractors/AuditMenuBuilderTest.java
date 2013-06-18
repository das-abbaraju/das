package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.URLUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AuditMenuBuilderTest {
	private AuditMenuBuilder auditMenuBuilder;

	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private Permissions permissions;
	@Mock
	private URLUtils urlUtils;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		auditMenuBuilder = new AuditMenuBuilder(contractorAccount, permissions);

		Whitebox.setInternalState(auditMenuBuilder, "i18nCache", i18nCache);
		Whitebox.setInternalState(auditMenuBuilder, "locale", Locale.US);
		Whitebox.setInternalState(auditMenuBuilder, "urlUtils", urlUtils);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildAuditMenuFrom_MissingContractor() throws Exception {
		auditMenuBuilder = new AuditMenuBuilder(null, permissions);
		List<ContractorAudit> audits = new ArrayList<>();

		auditMenuBuilder.buildAuditMenuFrom(audits);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildAuditMenuFrom_MissingPermissions() throws Exception {
		auditMenuBuilder = new AuditMenuBuilder(contractorAccount, null);
		List<ContractorAudit> audits = new ArrayList<>();

		auditMenuBuilder.buildAuditMenuFrom(audits);
	}

	@Test
	public void testBuildAuditMenuFrom_OperatorPermissionsOnPendingAccount() throws Exception {
		ContractorAudit audit1 = mock(ContractorAudit.class);
		ContractorAudit audit2 = mock(ContractorAudit.class);

		List<ContractorAudit> audits = new ArrayList<>();
		audits.add(audit1);
		audits.add(audit2);

		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Requested);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		Set<ContractorAudit> sortedAudits = (Set<ContractorAudit>) Whitebox.getInternalState(auditMenuBuilder,
				"sortedAudits");

		assertTrue(auditMenuBuilder.buildAuditMenuFrom(audits).isEmpty());
		assertNull(sortedAudits);
	}

	@Test
	public void testBuildAuditMenuFrom_PQFAndTradeMenuIncludedForOperator() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeast(2)).getActionUrl(actionCaptor.capture(), anyString(), any());
		assertTrue(actionCaptor.getAllValues().contains("ContractorTrades"));
	}

	@Test
	public void testBuildAuditMenuFrom_PQFAndTradeMenuNotIncludedForNonSafetyContractorUser() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertTrue(menuComponents.isEmpty());

		verify(urlUtils, never()).getActionUrl(anyString(), anyString(), any());
	}

	@Test
	public void testBuildAuditMenuFrom_NoPQFAndTradeMenuForSafetyContractorUserWithNoOperators() throws
			Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits(false));
		assertNotNull(menuComponents);

		verify(urlUtils, atLeast(2)).getActionUrl(actionCaptor.capture(), anyString(), any());
		assertFalse(actionCaptor.getAllValues().contains("ContractorTrades"));
	}

	@Test
	public void testBuildAuditMenuFrom_PQFAndTradeMenuForSafetyContractorUserWithOperators() throws
			Exception {
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeast(2)).getActionUrl(actionCaptor.capture(), anyString(), any());
		assertTrue(actionCaptor.getAllValues().contains("ContractorTrades"));
	}

	@Test
	public void testBuildAuditMenuFrom_AnnualUpdateLink() throws
			Exception {
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(i18nCache).getText(eq("ContractorActionSupport.Update"), any(Locale.class), any());
	}

	@Test
	public void testBuildAuditMenuFrom_NoInsureGUARDForNonInsuranceContractorUser() throws
			Exception {
		when(permissions.isContractor()).thenReturn(true);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);

		verify(i18nCache, never()).getText(eq("global.InsureGUARD"), any(Locale.class));
	}

	@Test
	public void testBuildAuditMenuFrom_InsureGUARDForInsuranceContractorUser() throws
			Exception {
		when(permissions.hasPermission(OpPerms.ContractorInsurance)).thenReturn(true);

		ArgumentCaptor<String> translationKeyCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(i18nCache, atLeastOnce()).getText(translationKeyCaptor.capture(), any(Locale.class));

		assertTrue(translationKeyCaptor.getAllValues().contains("global.InsureGUARD"));
		assertTrue(translationKeyCaptor.getAllValues().contains("ContractorActionSupport.ManageCertificates"));
	}

	@Test
	public void testBuildAuditMenuFrom_PicsEmployeeWithVerificationHasVerificationLink() throws
			Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.AuditVerification)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeastOnce()).getActionUrl(actionCaptor.capture(), anyString(), any());

		assertTrue(actionCaptor.getAllValues().contains("InsureGuardVerification"));
	}

	@Test
	public void testBuildAuditMenuFrom_SafetyContractorUserDoesNotHaveEmployeeGUARDTag() throws
			Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeastOnce()).getActionUrl(actionCaptor.capture(), anyString(), any());

		assertFalse(actionCaptor.getAllValues().contains("EmployeeDashboard"));
	}

	@Test
	public void testBuildAuditMenuFrom_SafetyContractorUserHasEmployeeGUARDTag() throws
			Exception {
		when(contractorAccount.isHasEmployeeGUARDTag()).thenReturn(true);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeastOnce()).getActionUrl(actionCaptor.capture(), anyString(), any());

		assertTrue(actionCaptor.getAllValues().contains("EmployeeDashboard"));
	}

	@Test
	public void testBuildAuditMenuFrom_SafetyContractorUserHasOperatorWithCompetencyRequiringDocumentation()
			throws Exception {
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(true);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(urlUtils, atLeastOnce()).getActionUrl(actionCaptor.capture(), anyString(), any());

		assertTrue(actionCaptor.getAllValues().contains("EmployeeDashboard"));
	}

	@Test
	public void testBuildAuditMenuFrom_SafetyContractorUserHasInsureGUARD() throws
			Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> translationKeyCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(i18nCache, atLeastOnce()).getText(translationKeyCaptor.capture(), any(Locale.class));

		assertTrue(translationKeyCaptor.getAllValues().contains("global.AuditGUARD"));
	}


	@Test
	public void testBuildAuditMenuFrom_SafetyContractorUserHasClientReviews() throws
			Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		ArgumentCaptor<String> translationKeyCaptor = ArgumentCaptor.forClass(String.class);

		List<MenuComponent> menuComponents = auditMenuBuilder.buildAuditMenuFrom(audits());
		assertNotNull(menuComponents);
		assertFalse(menuComponents.isEmpty());

		verify(i18nCache, atLeastOnce()).getText(translationKeyCaptor.capture(), any(Locale.class));

		assertTrue(translationKeyCaptor.getAllValues().contains("global.ClientReviews"));
	}

	@Test
	public void testCompetencyRequiresDocumentation_NoTagsNoOperatorCompetencyRequiringDocumentation()
			throws Exception {
		when(contractorAccount.getOperatorTags()).thenReturn(Collections.<ContractorTag>emptyList());
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(false);

		boolean competencyRequiresDocumentation = Whitebox.invokeMethod(auditMenuBuilder, "competencyRequiresDocumentation");

		assertFalse("Contractor has no tags and no operators requiring competencies", competencyRequiresDocumentation);
	}

	@Test
	public void testCompetencyRequiresDocumentation_TagRemovingEGNoOperatorCompetencyRequiringDocumentation()
			throws Exception {
		List<ContractorTag> contractorTags = new ArrayList<>();
		ContractorTag contractorTag = mock(ContractorTag.class);
		contractorTags.add(contractorTag);
		OperatorTag operatorTag = mock(OperatorTag.class);

		when(contractorAccount.getOperatorTags()).thenReturn(contractorTags);
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(false);
		when(contractorTag.getTag()).thenReturn(operatorTag);
		when(operatorTag.getCategory()).thenReturn(OperatorTagCategory.RemoveEmployeeGUARD);

		boolean competencyRequiresDocumentation = Whitebox.invokeMethod(auditMenuBuilder, "competencyRequiresDocumentation");

		assertFalse("Contractor has no tags and no operators requiring competencies", competencyRequiresDocumentation);
	}

	@Test
	public void testCompetencyRequiresDocumentation_NoTagsHasOperatorCompetencyRequiringDocumentation()
			throws Exception {
		when(contractorAccount.getOperatorTags()).thenReturn(Collections.<ContractorTag>emptyList());
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(true);

		boolean competencyRequiresDocumentation = Whitebox.invokeMethod(auditMenuBuilder, "competencyRequiresDocumentation");

		assertTrue("Contractor has at least one operator requiring competencies and no tags",
				competencyRequiresDocumentation);
	}

	@Test
	public void testCompetencyRequiresDocumentation_TagRemovingEGAndOperatorCompetencyRequiringDocumentation()
			throws Exception {
		List<ContractorTag> contractorTags = new ArrayList<>();
		ContractorTag contractorTag = mock(ContractorTag.class);
		contractorTags.add(contractorTag);
		OperatorTag operatorTag = mock(OperatorTag.class);

		when(contractorAccount.getOperatorTags()).thenReturn(contractorTags);
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(true);
		when(contractorTag.getTag()).thenReturn(operatorTag);
		when(operatorTag.getCategory()).thenReturn(OperatorTagCategory.RemoveEmployeeGUARD);

		boolean competencyRequiresDocumentation = Whitebox.invokeMethod(auditMenuBuilder, "competencyRequiresDocumentation");

		assertFalse("Contractor has at least one operator requiring competencies and a tag removing EG",
				competencyRequiresDocumentation);
	}

	@Test
	public void testCompetencyRequiresDocumentation_NoTagRemovingEGAndOperatorCompetencyRequiringDocumentation()
			throws Exception {
		List<ContractorTag> contractorTags = new ArrayList<>();
		ContractorTag contractorTag = mock(ContractorTag.class);
		contractorTags.add(contractorTag);
		OperatorTag operatorTag = mock(OperatorTag.class);

		when(contractorAccount.getOperatorTags()).thenReturn(contractorTags);
		when(contractorAccount.hasOperatorWithCompetencyRequiringDocumentation()).thenReturn(true);
		when(contractorTag.getTag()).thenReturn(operatorTag);
		when(operatorTag.getCategory()).thenReturn(OperatorTagCategory.OtherEmployeeGUARD);

		boolean competencyRequiresDocumentation = Whitebox.invokeMethod(auditMenuBuilder, "competencyRequiresDocumentation");

		assertTrue("Contractor has at least one operator requiring competencies and a tag that does not remove EG",
				competencyRequiresDocumentation);
	}

	private List<ContractorAudit> audits() {
		return audits(true);
	}

	private List<ContractorAudit> audits(boolean hasOperators) {
		List<ContractorAudit> audits = new ArrayList<>();

		audits.add(pqf(hasOperators));
		audits.add(annualUpdate(hasOperators));
		audits.add(insureGUARD(hasOperators));
		audits.add(employeeGUARD(hasOperators));
		audits.add(auditGUARD(hasOperators));
		audits.add(clientReviews(hasOperators));

		return audits;
	}

	private ContractorAudit pqf(boolean hasOperators) {
		AuditType pqfAuditType = mock(AuditType.class);
		ContractorAudit pqf = mock(ContractorAudit.class);

		when(pqf.getAuditType()).thenReturn(pqfAuditType);
		when(pqf.getId()).thenReturn(1);
		when(pqfAuditType.getClassType()).thenReturn(AuditTypeClass.PQF);
		when(pqfAuditType.isPicsPqf()).thenReturn(true);

		if (hasOperators) {
			addOperators(pqf);
		}

		return pqf;
	}

	private ContractorAudit annualUpdate(boolean hasOperators) {
		AuditType annualUpdateType = mock(AuditType.class);
		ContractorAudit annualUpdate = mock(ContractorAudit.class);

		when(annualUpdate.getAuditType()).thenReturn(annualUpdateType);
		when(annualUpdate.getId()).thenReturn(11);
		when(annualUpdateType.isAnnualAddendum()).thenReturn(true);

		if (hasOperators) {
			addOperators(annualUpdate);
		}

		return annualUpdate;
	}

	private ContractorAudit insureGUARD(boolean hasOperators) {
		AuditType policyType = mock(AuditType.class);
		ContractorAudit policy = mock(ContractorAudit.class);

		when(policy.getAuditType()).thenReturn(policyType);
		when(policy.getId()).thenReturn(2);
		when(policyType.getClassType()).thenReturn(AuditTypeClass.Policy);

		if (hasOperators) {
			addOperators(policy);
		}

		return policy;
	}

	private ContractorAudit employeeGUARD(boolean hasOperators) {
		AuditType employeeType = mock(AuditType.class);
		ContractorAudit employeeGUARD = mock(ContractorAudit.class);

		when(employeeGUARD.getAuditType()).thenReturn(employeeType);
		when(employeeGUARD.getId()).thenReturn(AuditType.SHELL_COMPETENCY_REVIEW);
		when(employeeType.getClassType()).thenReturn(AuditTypeClass.Employee);

		if (hasOperators) {
			addOperators(employeeGUARD);
		}

		return employeeGUARD;
	}

	private ContractorAudit auditGUARD(boolean hasOperators) {
		AuditType auditType = mock(AuditType.class);
		ContractorAudit audit = mock(ContractorAudit.class);

		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.getId()).thenReturn(AuditType.DESKTOP);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);

		if (hasOperators) {
			addOperators(audit);
		}

		return audit;
	}

	private ContractorAudit clientReviews(boolean hasOperators) {
		AuditType clientReviewType = mock(AuditType.class);
		ContractorAudit clientReviews = mock(ContractorAudit.class);

		when(clientReviews.getAuditType()).thenReturn(clientReviewType);
		when(clientReviews.getId()).thenReturn(AuditType.BPIISNSPECIFIC);
		when(clientReviewType.getClassType()).thenReturn(AuditTypeClass.Review);

		if (hasOperators) {
			addOperators(clientReviews);
		}

		return clientReviews;
	}

	private void addOperators(ContractorAudit mockAudit) {
		ContractorAuditOperator contractorAuditOperator = mock(ContractorAuditOperator.class);
		List<ContractorAuditOperator> contractorAuditOperators = new ArrayList<>();
		contractorAuditOperators.add(contractorAuditOperator);
		when(mockAudit.getOperators()).thenReturn(contractorAuditOperators);
		when(mockAudit.getCurrentOperators()).thenReturn(contractorAuditOperators);
	}
}
