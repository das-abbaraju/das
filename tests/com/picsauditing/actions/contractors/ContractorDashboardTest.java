package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;

public class ContractorDashboardTest extends PicsTranslationTest {
	// NOTE: when you need Struts/Spring stuff do not use PowerMockRunner.
	// Instead, extend PicsActionTest and take a look at any of its other
	// subclasses (see me for help - Galen)

	private ContractorDashboard dashboard;
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private OperatorAccount corporate;
	private ContractorOperator conOp;
	private ContractorOperator conCorp;
	private Permissions permissions;

	@Mock
	private UserDAO userDAO;
	@Mock
	private ContractorOperatorDAO contractorOperatorDAO;
	@Mock
	private Database databaseForTesting;
	@Mock
	private PermissionBuilder permissionBuilder;
	@Mock
	private Permissions corporatePermissions;
	@Mock
	private Permissions operatorPermissions1;
	@Mock
	private Permissions operatorPermissions2;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		dashboard = new ContractorDashboard();

		contractor = EntityFactory.makeContractor();

		corporate = EntityFactory.makeOperator();
		conCorp = EntityFactory.addContractorOperator(contractor, corporate);

		operator = EntityFactory.makeOperator();
		conOp = EntityFactory.addContractorOperator(contractor, operator);
		ArrayList<Facility> corporateFacilities = new ArrayList<Facility>();
		corporateFacilities.add(EntityFactory.makeFacility(operator, corporate));
		operator.setCorporateFacilities(corporateFacilities);

		permissions = EntityFactory.makePermission();
	}

	@Test
	public void testFindCorporateOverrides() throws Exception {
		Calendar date = Calendar.getInstance();

		corporate.setType(Account.CORPORATE_ACCOUNT_TYPE);
		operator.setType(Account.OPERATOR_ACCOUNT_TYPE);
		Whitebox.setInternalState(dashboard, "co", conCorp);
		Whitebox.setInternalState(dashboard, "contractor", contractor);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertTrue(dashboard.getCorporateFlagOverride() == null);

		conCorp.setForceFlag(FlagColor.Red);
		date.add(Calendar.MONTH, -1);
		conCorp.setForceBegin(date.getTime());
		date.add(Calendar.MONTH, 3);
		conCorp.setForceEnd(date.getTime());
		Whitebox.setInternalState(dashboard, "co", conCorp);
		Whitebox.setInternalState(dashboard, "contractor", contractor);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertTrue(dashboard.getCorporateFlagOverride() == null);
	}

	@Test
	public void testGetUsersWithPermission() throws Exception {
		PicsTestUtil.autowireDAOsFromDeclaredMocks(dashboard, this);
		OperatorAccount site1 = EntityFactory.makeOperator();
		OperatorAccount site2 = EntityFactory.makeOperator();
		OperatorAccount corporate = EntityFactory.makeOperator();

		site1.setType(Account.OPERATOR_ACCOUNT_TYPE);
		site1.setAutoApproveRelationships(false);
		site1.setParent(corporate);

		site2.setType(Account.OPERATOR_ACCOUNT_TYPE);
		site2.setAutoApproveRelationships(false);
		site2.setParent(corporate);

		corporate.setType(Account.CORPORATE_ACCOUNT_TYPE);
		corporate.setAutoApproveRelationships(false);
		corporate.getChildOperators().add(site1);
		corporate.getChildOperators().add(site2);

		ContractorOperator conOp1 = EntityFactory.addContractorOperator(contractor, site1);
		ContractorOperator conOp2 = EntityFactory.addContractorOperator(contractor, site2);
		ContractorOperator conOpCorporate = EntityFactory.addContractorOperator(contractor, corporate);

		conOp1.setWorkStatus(ApprovalStatus.P);
		conOp2.setWorkStatus(ApprovalStatus.P);
		conOpCorporate.setWorkStatus(ApprovalStatus.P);

		User operatorUser1 = EntityFactory.makeUser();
		User operatorUser2 = EntityFactory.makeUser();
		User corporateUser = EntityFactory.makeUser();

		site1.getUsers().add(operatorUser1);
		site1.getUsers().add(operatorUser2);
		site2.getUsers().add(operatorUser2);
		corporate.getUsers().add(corporateUser);

		when(permissionBuilder.login(operatorUser1)).thenReturn(operatorPermissions1);
		when(permissionBuilder.login(operatorUser2)).thenReturn(operatorPermissions2);
		when(permissionBuilder.login(corporateUser)).thenReturn(corporatePermissions);
		when(operatorPermissions1.hasPermission(OpPerms.AddContractors)).thenReturn(true);
		when(operatorPermissions2.hasPermission(OpPerms.AddContractors)).thenReturn(false);
		when(corporatePermissions.hasPermission(OpPerms.AddContractors)).thenReturn(false);
		when(userDAO.findByOperatorAccount(eq(corporate), anyInt())).thenReturn(corporate.getUsers());
		when(userDAO.findByOperatorAccount(eq(site1), anyInt())).thenReturn(site1.getUsers());
		when(userDAO.findByOperatorAccount(eq(site2), anyInt())).thenReturn(site2.getUsers());

		Whitebox.setInternalState(dashboard, "permissionBuilder", permissionBuilder);

		List<User> users;

		// 2 users, 1 with permission
		Whitebox.setInternalState(dashboard, "co", conOp1);
		users = dashboard.getOperatorUsersWithPermission(OpPerms.AddContractors);
		assertEquals(1, users.size());

		// 1 user, no permissions
		Whitebox.setInternalState(dashboard, "co", conOp2);
		users = dashboard.getOperatorUsersWithPermission(OpPerms.AddContractors);
		assertEquals(0, users.size());

		// Corporate, 1 with permission
		Whitebox.setInternalState(dashboard, "co", conOpCorporate);
		users = dashboard.getOperatorUsersWithPermission(OpPerms.AddContractors);
		assertEquals(1, users.size());
	}

	@Test
	public void testGetIncompleteAnnualUpdates() throws Exception {
		Calendar date = Calendar.getInstance();

		String year1 = "" + date.get(Calendar.YEAR);
		date.add(Calendar.YEAR, -1);
		String year2 = "" + date.get(Calendar.YEAR);
		contractor.getAudits().add(createAU(year1, AuditStatus.Complete));
		contractor.getAudits().add(createAU(year2, AuditStatus.Pending));

		String results = Whitebox.invokeMethod(dashboard, "getIncompleteAnnualUpdates", contractor, operator.getId());
		assertTrue(results.contains(year2));
		assertTrue(!results.contains(year1));
	}

	private ContractorAudit createAU(String year, AuditStatus status) {
		ContractorAudit audit;
		audit = EntityFactory.makeAnnualUpdate(AuditType.ANNUALADDENDUM, contractor, year);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, 3);
		audit.setExpiresDate(date.getTime());

		ContractorAuditOperator cao;
		cao = EntityFactory.addCao(audit, operator);
		cao.setStatus(status);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setOperator(operator);
		cao.getCaoPermissions().add(caop);
		return audit;
	}
}
