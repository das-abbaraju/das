package com.picsauditing.actions.audits;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class SubmittedImportPQFAudits extends PicsActionSupport {
	private Database db;
	private SelectSQL sql;
	private int allRows;

	public List<BasicDynaBean> getData() throws Exception {
		db = new Database();
		sql = new SelectSQL("contractor_audit ca");

		sql.addField("a.id accountID");
		sql.addField("a.name");
		sql.addField("i.id invoiceID");
		sql.addField("i.status invoiceStatus");
		sql.addField("ca.id auditID");
		sql.addField("GROUP_CONCAT(DISTINCT cao.status SEPARATOR ', ') auditStatus");

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1"
				+ " AND cao.status = 'Submitted'");
		sql.addJoin("JOIN accounts a ON a.id = ca.conID AND a.status = 'Active'");
		sql.addJoin("JOIN contractor_info c ON c.id = a.id");
		sql.addJoin("JOIN invoice i ON i.accountID = a.id AND i.status = 'Paid'");
		sql.addJoin("JOIN invoice_item ii ON ii.invoiceID = i.id");
		sql.addJoin("JOIN invoice_fee fee ON fee.id = ii.feeID AND fee.feeClass = 'ImportFee'");

		sql.addWhere("ca.auditTypeID = 232 AND ca.auditorID = " + permissions.getUserId());
		sql.addOrderBy("ca.creationDate");
		sql.addGroupBy("a.id, ca.id");

		sql.setLimit(10);

		List<BasicDynaBean> data = db.select(sql.toString(), true);
		allRows = db.getAllRows();

		return data;
	}

	public int getAllRows() {
		return allRows;
	}
}
