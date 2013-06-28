package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.List;

import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class AccountDeleter extends Deleter {
	public void execute() throws SQLException {
		if (null == IDs || IDs.isEmpty()) return;
		
		Database db = new Database();

		{
			Delete t = new Delete("contractor_audit_operator_permission");
			t.addJoin("JOIN contractor_audit_operator cao ON cao.id = t.caoID");
			t.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
			t.addJoin("WHERE ca.conID IN (" + IDs + ")");
			t.delete(db);
			t.table = "contractor_audit_operator_workflow";
			t.delete(db);
		}
		{
			Delete t = new Delete("audit_cat_data");
			t.addJoin("JOIN contractor_audit ca ON ca.id = t.auditID");
			t.addJoin("WHERE ca.conID IN (" + IDs + ")");
			t.delete(db);
			t.table = "contractor_audit_operator";
			t.delete(db);
			t.table = "pqfdata";
			t.delete(db);
		}
		{
			Delete t = new Delete("contractor_audit");
			t.addJoin("WHERE t.conID IN (" + IDs + ")");
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
			t.addJoin("WHERE t.genID IN (" + IDs + ") OR t.subID IN (" + IDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("facilities");
			t.addJoin("WHERE t.corporateID IN (" + IDs + ") OR t.opID IN (" + IDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice_item");
			t.addJoin("JOIN invoice i ON i.id = t.invoiceID");
			t.addJoin("WHERE i.accountID IN (" + IDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("invoice");
			t.addJoin("WHERE t.accountID IN (" + IDs + ")");
			t.delete(db);
			t.table = "users";
			t.delete(db);
		}
		{
			Delete t = new Delete("app_index");
			t.addJoin("WHERE t.foreignKey IN (" + IDs + ")");
			t.delete(db);
		}
		{
			Delete t = new Delete("accounts");
			t.addJoin("WHERE t.id IN (" + IDs + ")");
			t.delete(db);
			// Looks like these might cascade delete
			t.table = "operators";
			t.delete(db);
			t.table = "contractor_info";
			t.delete(db);
		}
	}
}