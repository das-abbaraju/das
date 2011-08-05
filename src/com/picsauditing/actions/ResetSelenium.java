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

		SelectSQL accounts = new SelectSQL("accounts");
		accounts.addField("id");
		accounts.addWhere("name LIKE 'Selenium Test%'");
		List<BasicDynaBean> select = db.select(accounts.toString(), false);

		Set<Integer> contractors = new HashSet<Integer>();
		for (BasicDynaBean row : select) {
			contractors.add(Integer.parseInt(row.get("id").toString()));
		}

		if (contractors.size() > 0) {
			{
				Delete t = new Delete("contractor_audit_operator_permission");
				t.addJoin("JOIN contractor_audit_operator cao ON cao.id = t.caoID");
				t.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
				t.addJoin("WHERE ca.conID IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
				t.table = "contractor_audit_operator_workflow";
				t.delete(db);
			}
			{
				Delete t = new Delete("audit_cat_data");
				t.addJoin("JOIN contractor_audit ca ON ca.id = t.auditID");
				t.addJoin("WHERE ca.conID IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
				t.table = "contractor_audit_operator";
				t.delete(db);
				t.table = "pqfdata";
				t.delete(db);
			}
			{
				Delete t = new Delete("contractor_audit");
				t.addJoin("WHERE t.conID IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
				t.table = "contractor_fee";
				t.delete(db);
				t.table = "contractor_trade";
				t.delete(db);
			}
			{
				Delete t = new Delete("invoice_item");
				t.addJoin("JOIN invoice i ON i.id = t.invoiceID");
				t.addJoin("WHERE i.accountID IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
			}
			{
				Delete t = new Delete("invoice");
				t.addJoin("WHERE t.accountID IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
				t.table = "users";
				t.delete(db);
			}
			{
				Delete t = new Delete("contractor_info");
				t.addJoin("WHERE t.id IN (" + Strings.implodeForDB(contractors, ",") + ")");
				t.delete(db);
				t.table = "accounts";
				t.delete(db);
			}
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

		public int delete(Database db) throws SQLException {
			System.out.println(toString() + ";");
			// return 1;
			return db.executeUpdate(toString());
		}
	}
}
