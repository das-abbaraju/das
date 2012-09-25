package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.Column;
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
		availableFields = model.getAvailableFields();

		fieldsThatShouldNotBeIncluded.add("ContractorRequestedByOperatorCity");
		fieldsThatShouldBeIncluded.add("contractorPQFExpiresDate");
		fieldsThatShouldBeIncluded.add("ContractorProductRisk");
		fieldsThatShouldBeIncluded.add("AccountFax");
		fieldsThatShouldBeIncluded.add("AccountContactEmail");
		fieldsThatShouldNotBeIncluded.add("AccountContactLastLogin");
		fieldsThatShouldBeIncluded.add("AccountNaicsTrir");
		fieldsThatShouldBeIncluded.add("ContractorCustomerServiceFax");
		fieldsThatShouldNotBeIncluded.add("ContractorCustomerServiceLastLogin");
		checkFields();
	}

	@Test
	public void testAvailableFieldsForAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		availableFields = model.getAvailableFields();

		fieldsThatShouldBeIncluded.add("contractorBalance");
		checkFields();
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
