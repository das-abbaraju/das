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
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.SelectSQL;

public class AccountContractorModelTest {

	private AccountContractorModel model = new AccountContractorModel();
	private Permissions permissions;
	private Definition definition;

	@Before
	public void setup() {
		permissions = EntityFactory.makePermission();
	}

	@Test
	public void testAvailableFields() throws Exception {
		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		assertFalse("contractorRequestedByOperatorCity is Low importance",
				availableFields.containsKey("contractorRequestedByOperatorCity".toUpperCase()));
		assertTrue("contractorPQFExpiresDate is Required",
				availableFields.containsKey("contractorPQFExpiresDate".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 66, availableFields.size());
	}

	@Test
	public void testAvailableFieldsForAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		assertTrue("contractorBalance is Required", availableFields.containsKey("contractorBalance".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 74, availableFields.size());
	}

	@Test
	public void testSqlForOperator() throws Exception {
		definition = new Definition("");
		definition.getColumns().add(new Column("accountCountry"));
		definition.getColumns().add(new Column("contractorOperatorFlagColor"));

		permissions = EntityFactory.makePermission(EntityFactory.makeUser(OperatorAccount.class));

		AbstractModel contractorModel = ModelFactory.build(ModelType.Contractors);
		SelectSQL sql = new SqlBuilder().initializeSql(contractorModel, definition, permissions);
		String sqlResult = sql.toString();
		assertContains("generalcontractors AS myFlag ON myFlag.subID = c.id", sqlResult);
	}
}
