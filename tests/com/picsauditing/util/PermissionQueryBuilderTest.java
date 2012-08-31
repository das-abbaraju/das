package com.picsauditing.util;

import static com.picsauditing.util.Assert.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;

public class PermissionQueryBuilderTest {

	private User user;
	private Permissions permissions;
	private PermissionQueryBuilder builder;

	@Before
	public void setup() {
		user = EntityFactory.makeUser();
		permissions = EntityFactory.makePermission(user);
	}

	@Test
	public void testNull() throws Exception {
		builder = new PermissionQueryBuilder(null);
		assertEquals("AND 1=0", builder.toString());
	}

	@Test
	public void testNotLoggedIn() throws Exception {
		permissions.clear();
		builder = new PermissionQueryBuilder(permissions);
		assertEquals("1=0", builder.buildQuery());
	}

	@Test
	public void testBlank() throws Exception {
		builder = new PermissionQueryBuilder(permissions);
		assertEquals("1=0", builder.buildQuery());
		assertEquals(PermissionQueryBuilder.SQL, builder.getQueryLanguage());
	}

	@Test
	public void testAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);

		builder = new PermissionQueryBuilder(permissions);
		assertEquals("", builder.toString());
	}

	@Test
	public void testContractorWithHQL() throws Exception {
		user.getAccount().setType("Contractor");
		permissions.setAccountPerms(user);

		builder = new PermissionQueryBuilder(permissions);
		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		assertEquals("contractorAccount.id = " + permissions.getAccountId(), builder.buildQuery());
	}

	@Test
	public void testOperator() throws Exception {
		permissions.setAccountPerms(EntityFactory.makeUser(OperatorAccount.class));

		builder = new PermissionQueryBuilder(permissions);
		String whereClause = "a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
				+ permissions.getAccountId() + "))";
		assertEquals(whereClause, builder.buildQuery());
	}

	@Test
	public void testOperatorWithHQL() throws Exception {
		permissions.setAccountPerms(EntityFactory.makeUser(OperatorAccount.class));

		builder = new PermissionQueryBuilder(permissions);
		builder.setQueryLanguage(PermissionQueryBuilder.HQL);
		String whereClause = "contractorAccount.status IN ('Active') AND contractorAccount IN ("
				+ "SELECT t.contractorAccount FROM ContractorOperator t WHERE t.operatorAccount.id IN ("
				+ permissions.getAccountId() + "))";
		assertEquals(whereClause, builder.buildQuery());
	}

	@Test
	public void testOperatorWithStatuses() throws Exception {
		permissions.setAccountPerms(EntityFactory.makeUser(OperatorAccount.class));

		builder = new PermissionQueryBuilder(permissions);
		builder.addVisibleStatus(AccountStatus.Pending);
		builder.addVisibleStatus(AccountStatus.Deactivated);
		String whereClause = builder.buildQuery();
		assertContains("a.status IN (", whereClause);
		assertContains("'Active'", whereClause);
		assertContains("'Pending'", whereClause);
		assertContains("'Deactivated'", whereClause);
		assertNotContains("'Demo'", whereClause);
	}

	@Test
	public void testDemoOperator() throws Exception {
		User operator = EntityFactory.makeUser(OperatorAccount.class);
		operator.getAccount().setStatus(AccountStatus.Demo);
		permissions.setAccountPerms(operator);

		builder = new PermissionQueryBuilder(permissions);
		String whereClause = builder.buildQuery();
		assertContains("'Active'", whereClause);
		assertContains("'Demo'", whereClause);
	}

	@Test
	public void testCorporate() throws Exception {
		user = EntityFactory.makeUser(OperatorAccount.class);
		user.getAccount().setType("Corporate");
		permissions.setAccountPerms(user);

		builder = new PermissionQueryBuilder(permissions);

		String expected = "FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID =";
		assertContains(expected, builder.buildQuery());
	}

	@Test
	public void testCorporateWithHQL() throws Exception {
		user = EntityFactory.makeUser(OperatorAccount.class);
		user.getAccount().setType("Corporate");
		permissions.setAccountPerms(user);

		builder = new PermissionQueryBuilder(permissions);
		builder.setQueryLanguage(PermissionQueryBuilder.HQL);

		String expected = "SELECT co.contractorAccount FROM ContractorOperator co "
				+ "WHERE co.operatorAccount IN (SELECT f.operator FROM Facility f WHERE f.corporate.id =";
		assertContains(expected, builder.buildQuery());
	}
	
	@Ignore
	@Test
	public void testAuditor() throws Exception {
		user = EntityFactory.makeUser(OperatorAccount.class);
		addUserGroup(User.GROUP_AUDITOR);
		permissions.setAccountPerms(user);

		builder = new PermissionQueryBuilder(permissions);

		String expected = "FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID =";
		assertContains(expected, builder.buildQuery());
	}

	private void addUserGroup(int groupID) {
		UserGroup auditorUserGroup = new UserGroup();
		auditorUserGroup.setGroup(new User(groupID));
		user.getGroups().add(auditorUserGroup);
	}

}
