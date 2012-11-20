package com.picsauditing.util;

import static com.picsauditing.util.Assert.assertContains;
import static com.picsauditing.util.Assert.assertNotContains;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;

public class PermissionQueryBuilderTest extends PicsTest {

	private static final int ACCOUNT_ID = 15;
	private static final Set<Integer> CLIENT_SITES = new HashSet<Integer>();

	private PermissionQueryBuilder builder;

	@Mock
	private Permissions permissions;
	@Mock
	private Set<AccountStatus> visibleStatuses;

	@Before
	public void setup() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		builder = new PermissionQueryBuilder(permissions);
		autowireEMInjectedDAOs(builder);

		CLIENT_SITES.add(new Integer(16));
		CLIENT_SITES.add(new Integer(17));
		CLIENT_SITES.add(new Integer(18));

		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(visibleStatuses.isEmpty()).thenReturn(true);
		PicsTestUtil.forceSetPrivateField(builder, "permissions", permissions);
	}

	@Test
	public void testNull() throws Exception {
		builder = new PermissionQueryBuilder(null);
		assertEquals("1=0", builder.buildWhereClause());
	}

	@Test
	public void testNotLoggedIn() throws Exception {
		when(permissions.isLoggedIn()).thenReturn(false);

		assertEquals("1=0", builder.buildWhereClause());
	}

	@Test
	public void testBlank() throws Exception {
		assertEquals("1=0", builder.buildWhereClause());
		assertEquals(PermissionQueryBuilder.SQL, builder.getQueryLanguage());
	}

	@Test
	public void testAdmin() throws Exception {
		when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

		assertEquals("", builder.buildWhereClause());
	}

	@Test
	public void testContractorWithHQL() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		assertEquals("contractorAccount.id = " + permissions.getAccountId(), builder.buildWhereClause());
	}

	@Test
	public void testAssessment() throws Exception {
		when(permissions.isAssessment()).thenReturn(true);

		String expected = ".status IN ('Active', 'Pending', 'Deactivated')";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testAuditor() throws Exception {
		when(permissions.isOnlyAuditor()).thenReturn(true);

		String expected = "SELECT conID FROM contractor_audit WHERE auditorID =";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testAuditorWithHQL() throws Exception {
		when(permissions.isOnlyAuditor()).thenReturn(true);

		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		String expected = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.auditor.id =";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testOperatorAutoApprovesContractors() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		setPermissionAccount();

		String whereClause = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ ACCOUNT_ID + ") AND gc.workStatus = 'Y')";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testOperatorNoWorkingFacility() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		setPermissionAccount();

		builder.setWorkingFacilities(false);
		String whereClause = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ ACCOUNT_ID + "))";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testOperatorNoWorkingFacilityWithPermissions() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ViewUnApproved)).thenReturn(true);
		setPermissionAccount();

		builder.setWorkingFacilities(false);
		String whereClause = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ ACCOUNT_ID + "))";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testOperatorWithHQL() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		setPermissionAccount();

		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		String whereClause = "contractorAccount.status IN ('Active') AND contractorAccount IN ("
				+ "SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount.id IN ("
				+ ACCOUNT_ID + ") AND co.workStatus = 'Y')";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testOperatorWithStatuses() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		setPermissionAccount();

		builder.addVisibleStatus(AccountStatus.Pending);
		builder.addVisibleStatus(AccountStatus.Deactivated);
		String whereClause = builder.buildWhereClause();
		assertContains("a.status IN (", whereClause);
		assertContains("'Active'", whereClause);
		assertContains("'Pending'", whereClause);
		assertContains("'Deactivated'", whereClause);
		assertNotContains("'Demo'", whereClause);
	}

	@Test
	public void testOperatorWithGcJoin() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		setPermissionAccount();

		builder.setContractorOperatorAlias("flag");
		String whereClause = "a.status IN ('Active') AND flag.genID IN (" + ACCOUNT_ID + ") AND flag.workStatus = 'Y'";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testDemoOperator() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Demo);
		setPermissionAccount();

		String whereClause = builder.buildWhereClause();
		assertContains("'Active'", whereClause);
		assertContains("'Demo'", whereClause);
	}

	@Test
	public void testCorporateWithGcJoin() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		setPermissionAccount();
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);

		builder.setContractorOperatorAlias("flag");
		String whereClause = "a.status IN ('Active') " + "AND a.id IN (SELECT gc.subID "
				+ "FROM generalcontractors gc " + "WHERE gc.genID IN (" + Strings.implode(CLIENT_SITES, ",")
				+ ") AND gc.workStatus = 'Y')";
		assertEquals(whereClause, builder.buildWhereClause());
	}

	@Test
	public void testCorporate() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);
		setPermissionAccount();

		String expected = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ Strings.implode(CLIENT_SITES, ",") + ") AND gc.workStatus = 'Y')";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testCorporateWithHQL() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);
		setPermissionAccount();

		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		String expected = "SELECT co.contractorAccount FROM ContractorOperator co "
				+ "WHERE co.operatorAccount.id IN (" + Strings.implode(CLIENT_SITES, ",") + ") AND co.workStatus = 'Y'";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testOnlyApproved__CanViewUnapproved() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);
		setPermissionAccount();
		when(permissions.isGeneralContractor()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ViewUnApproved)).thenReturn(true);

		String expected = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ Strings.implode(CLIENT_SITES, ",") + "))";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testOnlyApproved__cannotViewUnapproved() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);
		setPermissionAccount();
		when(permissions.isGeneralContractor()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ViewUnApproved)).thenReturn(false);

		String expected = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ Strings.implode(CLIENT_SITES, ",") + ") AND gc.workStatus = 'Y')";
		assertContains(expected, builder.buildWhereClause());
	}

	@Test
	public void testOnlyApproved__isGeneralContractor() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(CLIENT_SITES);
		setPermissionAccount();
		when(permissions.isGeneralContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ViewUnApproved)).thenReturn(false);

		String expected = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ Strings.implode(CLIENT_SITES, ",") + ") AND gc.workStatus = 'Y')";
		assertContains(expected, builder.buildWhereClause());
	}

	private void setPermissionAccount() {
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(permissions.getAccountIdString()).thenReturn(new Integer(ACCOUNT_ID).toString());
		when(permissions.isOperatorCorporate()).thenReturn(true);
	}
}