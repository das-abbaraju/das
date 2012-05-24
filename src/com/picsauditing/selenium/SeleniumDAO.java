package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SeleniumDAO {
	
	public static void delete (List<Account> deletables) throws Exception{
		if (CollectionUtils.isEmpty(deletables)) return;
		
		String accounts = Strings.implodeForDB(getAccountIDsFor(deletables), ",");
		Database db = new Database();
		
		{
			Delete t = new Delete("contractor_audit_operator_permission");
			t.addJoin("JOIN contractor_audit_operator cao ON cao.id = t.caoID");
			t.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
			t.addJoin("WHERE ca.conID IN (" + accounts + ")");
			t.delete(db);
			t.table = "contractor_audit_operator_workflow";
			t.delete(db);
		}
		{
			Delete t = new Delete("audit_cat_data");
			t.addJoin("JOIN contractor_audit ca ON ca.id = t.auditID");
			t.addJoin("WHERE ca.conID IN (" + accounts + ")");
			t.delete(db);
			t.table = "contractor_audit_operator";
			t.delete(db);
			t.table = "pqfdata";
			t.delete(db);
		}
		{
			Delete t = new Delete("contractor_audit");
			t.addJoin("WHERE t.conID IN (" + accounts + ")");
			t.delete(db);
			t.table = "contractor_fee";
			t.delete(db);
			t.table = "email_queue";
			t.delete(db);
			t.table = "contractor_trade";
			t.delete(db);
		}
		{
			Delete t = new Delete("generalcontractors");
			t.addJoin("WHERE t.genID IN (" + accounts + ") OR t.subID IN (" + accounts + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("facilities");
			t.addJoin("WHERE t.corporateID IN (" + accounts + ") OR t.opID IN (" + accounts + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice_item");
			t.addJoin("JOIN invoice i ON i.id = t.invoiceID");
			t.addJoin("WHERE i.accountID IN (" + accounts + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice");
			t.addJoin("WHERE t.accountID IN (" + accounts + ")");
			t.delete(db);
			t.table = "users";
			t.delete(db);
		}
		{
			Delete t = new Delete("app_index");
			t.addJoin("WHERE t.foreignKey IN (" + accounts + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("accounts");
			t.addJoin("WHERE t.id IN (" + accounts + ")");
			t.delete(db);
			// Looks like these might cascade delete
			t.table = "operators";
			t.delete(db);
			t.table = "contractor_info";
			t.delete(db);
		}
		
	}
	
	public static List<Integer> getAccountIDsFor(List<Account> deletables) {
		List<Integer> IDs = new ArrayList<Integer>();
		for (Account account : deletables)
			IDs.add(account.getId());
		return IDs;
	}
	

	public static List<Account> AvailableTestingAccounts() {
		try {
			ArrayList<Account> accounts = new ArrayList<Account>();
			Database db = new Database();

			SelectSQL accountSQL = new SelectSQL("accounts");
			accountSQL.addField("id");
			accountSQL.addField("type");
			accountSQL.addField("name");
			accountSQL.addWhere("name LIKE 'Selenium Test%'");
			List<BasicDynaBean> select = db.select(accountSQL.toString(), false);

			for (BasicDynaBean row : select) {
				Account account = new Account();
				account.setId((Integer) row.get("id"));
				account.setName(row.get("name").toString());
				account.setType(row.get("type").toString());
				accounts.add(account);
			}
			
			return accounts;
		} catch (SQLException e) {
			//TODO : Throw and account for the SQLException.
			throw new RuntimeException("SQL Error in SeleniumDAO", e);
		}
	}
	
	private static class Delete {
		String table = "";
		List<String> joins = new ArrayList<String>();

		public Delete(String table) {
			this.table = table;
		}

		public Delete addJoin(String join) {
			joins.add(join);
			return this;
		}

		public String toString() {
			String sql = "DELETE t FROM " + table + " t ";

			for (String join : joins) {
				sql += "\n" + join;
			}

			return sql;
		}

		public void delete(Database db) throws SQLException {
			db.executeUpdate(toString());
		}
	}
}
