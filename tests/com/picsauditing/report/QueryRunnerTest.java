package com.picsauditing.report;

import java.sql.SQLException;
import java.util.Map;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;

public class QueryRunnerTest extends TestCase {

	private Permissions permissions;
	private QueryRunner runner;
	private QueryCommand command;
	private String sql;

	protected void setUp() throws Exception {
		super.setUp();
		permissions = EntityFactory.makePermission();
		// By default create a Contractor QueryRunner
		buildRunner(QueryBase.Contractors);
		command = new QueryCommand();
	}

	private void buildRunner(QueryBase base) {
		runner = new QueryRunner(base, permissions, null);
	}

	private void runBuildQueryWithCommand() throws SQLException {
		sql = runner.buildQuery(command, false).toString();
	}

	public void testAvailableFieldSize() {
		Map<String, QueryField> availableFields = runner.getAvailableFields();
		assertEquals(46, availableFields.size());
	}

	public void testSimpleContractorQuery() throws SQLException {
		runBuildQueryWithCommand();
		assertEquals("SELECT SQL_CALC_FOUND_ROWS customerService.id AS customerServiceUserID, " +
				"customerService.phone AS customerServicePhone, " +
				"accountContact.accountID AS accountContactUserAccountID, a.zip AS accountZip, " +
				"a.onsiteServices AS accountOnsite, c.main_trade AS contractorMainTrade, " +
				"accountContact.phone AS accountContactPhone, a.offsiteServices AS accountOffsite, " +
				"c.tradesSelf AS contractorTradesSelfPerformed, a.state AS accountState, " +
				"c.renew AS contractorRenew, customerService.accountID AS customerServiceUserAccountID, " +
				"c.accountLevel AS contractorAccountLevel, customerService.name AS customerServiceUserName, " +
				"a.status AS accountStatus, accountContact.email AS accountContactEmail, " +
				"c.paymentMethod AS contractorPaymentMethod, a.creationDate AS accountCreationDate, " +
				"a.address AS accountAddress, a.dbaName AS accountDBAName, a.country AS accountCountry, " +
				"a.fax AS accountFax, a.transportationServices AS accountTransportation, a.id AS accountID, " +
				"c.riskLevel AS contractorRiskLevel, c.paymentExpires AS contractorPaymentExpires, " +
				"a.web_url AS accountWebsite, a.nameIndex AS accountNameIndex, c.balance AS contractorBalance, " +
				"c.productRisk AS contractorProductRisk, a.materialSupplier AS accountMaterialSupplier, " +
				"c.tradesSub AS contractorTradesSubContracted, a.reason AS accountReason, " +
				"c.safetyRisk AS contractorSafetyRisk, customerService.email AS customerServiceEmail, " +
				"c.mustPay AS contractorMustPay, a.phone AS accountPhone, a.name AS accountName, " +
				"c.payingFacilities AS contractorPayingFacilities, c.score AS contractorScore, " +
				"a.city AS accountCity, c.ccOnFile AS contractorCreditCardOnFile, " +
				"accountContact.id AS accountContactUserID, c.ccExpiration AS contractorCreditCardExpiration, " +
				"accountContact.name AS accountContactUserName, c.membershipDate AS contractorMembershipDate " +
				"FROM accounts a JOIN contractor_info c ON a.id = c.id " +
				"LEFT JOIN users customerService ON customerService.id = c.welcomeAuditor_id " +
				"LEFT JOIN users accountContact ON accountContact.id = a.contactID " +
				"WHERE (a.type='Contractor')  AND (1 AND 1<>1) ORDER BY a.nameIndex LIMIT 100",
				runner.getSQL());
	}

	public void testLimit() throws SQLException {
		command.setRowsPerPage(10);
		runBuildQueryWithCommand();
		assertTrue(runner.getSQL().endsWith("LIMIT 10"));
	}

	public void testPages() throws SQLException {
		command.setRowsPerPage(10);
		command.setPage(2);
		runBuildQueryWithCommand();
		assertTrue(sql.endsWith("LIMIT 10, 10"));
	}
}
