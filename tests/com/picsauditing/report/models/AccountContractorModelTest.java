package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.SelectSQL;

public class AccountContractorModelTest extends ModelTest {
	private AccountContractorModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountContractorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		Map<String, Field> availableFields = model.getAvailableFields();

		assertFalse("contractorRequestedByOperatorCity is Low importance",
				availableFields.containsKey("contractorRequestedByOperatorCity".toUpperCase()));
		assertTrue("contractorPQFExpiresDate is Required",
				availableFields.containsKey("contractorPQFExpiresDate".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 56, availableFields.size());
	}

	@Test
	public void testAvailableFieldsForAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		Map<String, Field> availableFields = model.getAvailableFields();

		assertTrue("contractorBalance is Required", availableFields.containsKey("contractorBalance".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 64, availableFields.size());
	}

	@Test
	public void testSqlForOperator() throws Exception {
		definition.getColumns().add(new Column("accountCountry"));
		definition.getColumns().add(new Column("contractorOperatorFlagColor"));

		permissions = EntityFactory.makePermission(EntityFactory.makeUser(OperatorAccount.class));
		model = new AccountContractorModel(permissions);

		SelectSQL sql = new SqlBuilder().initializeSql(model, definition, permissions);
		String sqlResult = sql.toString();
		String expected = "JOIN generalcontractors AS ContractorFlag ON Contractor.id = ContractorFlag.subID AND ContractorFlag.genID = "
				+ permissions.getAccountId();
		assertContains(expected, sqlResult);
	}
}
