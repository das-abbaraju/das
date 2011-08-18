package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Anonymous;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ResetSelenium extends PicsActionSupport {

	@Anonymous
	public String execute() throws Exception {
		Database db = new Database();

		SelectSQL accountSQL = new SelectSQL("accounts");
		accountSQL.addField("id");
		accountSQL.addField("type");
		accountSQL.addField("name");
		accountSQL.addWhere("name LIKE 'Selenium Test%'");
		List<BasicDynaBean> select = db.select(accountSQL.toString(), false);

		Set<Integer> accounts = new HashSet<Integer>();
		for (BasicDynaBean row : select) {
			accounts.add(Integer.parseInt(row.get("id").toString()));
			addActionMessage("Deleting " + row.get("type") + " - " + row.get("name"));
		}

		if (accounts.size() == 0) {
			addActionMessage("Found 0 Selenium test accounts");
			return SUCCESS;
		}
		String accountIDs = Strings.implodeForDB(accounts, ",");
		{
			Delete t = new Delete("contractor_audit_operator_permission");
			t.addJoin("JOIN contractor_audit_operator cao ON cao.id = t.caoID");
			t.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
			t.addJoin("WHERE ca.conID IN (" + accountIDs + ")");
			t.delete(db);
			t.table = "contractor_audit_operator_workflow";
			t.delete(db);
		}
		{
			Delete t = new Delete("audit_cat_data");
			t.addJoin("JOIN contractor_audit ca ON ca.id = t.auditID");
			t.addJoin("WHERE ca.conID IN (" + accountIDs + ")");
			t.delete(db);
			t.table = "contractor_audit_operator";
			t.delete(db);
			t.table = "pqfdata";
			t.delete(db);
		}
		{
			Delete t = new Delete("contractor_audit");
			t.addJoin("WHERE t.conID IN (" + accountIDs + ")");
			t.delete(db);
			t.table = "contractor_fee";
			t.delete(db);
			t.table = "contractor_trade";
			t.delete(db);
		}
		{
			Delete t = new Delete("generalcontractors");
			t.addJoin("WHERE t.genID IN (" + accountIDs + ") OR t.subID IN (" + accountIDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("facilities");
			t.addJoin("WHERE t.corporateID IN (" + accountIDs + ") OR t.opID IN (" + accountIDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice_item");
			t.addJoin("JOIN invoice i ON i.id = t.invoiceID");
			t.addJoin("WHERE i.accountID IN (" + accountIDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice");
			t.addJoin("WHERE t.accountID IN (" + accountIDs + ")");
			t.delete(db);
			t.table = "users";
			t.delete(db);
		}
		{
			Delete t = new Delete("accounts");
			t.addJoin("WHERE t.id IN (" + accountIDs + ")");
			t.delete(db);
			// Looks like these might cascade delete
			t.table = "operators";
			t.delete(db);
			t.table = "contractor_info";
			t.delete(db);
		}

		return SUCCESS;
	}

	private class Delete {
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
			int changes = db.executeUpdate(toString());
			addActionMessage(changes + " from " + table);
		}
	}
}
