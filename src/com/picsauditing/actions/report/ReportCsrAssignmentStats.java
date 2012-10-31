package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.search.Database;

public class ReportCsrAssignmentStats extends ReportAccount {
	private static final long serialVersionUID = 1317657856130650808L;

	private List<BasicDynaBean> data;

	public String execute() throws SQLException {
		String sql = "select \r\n"
				+ "u.id as csrId, \r\n"
				+ "u.name as csrName,\r\n"
				+ "sum(if(a.status='Active', 1, 0)) as numActive,\r\n"
				+ "sum(if(a.status='Requested', 1, 0)) as numRequested,\r\n"
				+ "\r\n"
				+ "(\r\n"
				+ "  select count(distinct c.id) \r\n"
				+ "  from contractor_info c\r\n"
				+ "  join accounts a on c.id = a.id\r\n"
				+ "  where a.status = 'Active'\r\n"
				+ "  and a.type = 'Contractor'\r\n"
				+ "  and c.welcomeAuditor_id = u.id\r\n"
				+ "  and exists(\r\n"
				+ "    select * from invoice i\r\n"
				+ "    left join invoice_item item on i.id = item.invoiceID\r\n"
				+ "    left join invoice_fee fee on fee.id = item.feeID\r\n"
				+ "    where i.accountID = c.id\r\n"
				+ "    and i.status <> 'Void'\r\n"
				+ "    and fee.feeClass = 'AuditGUARD'\r\n"
				+ "    )\r\n"
				+ ") as numWithAuditGuard,\r\n"
				+ "\r\n"
				+ "(\r\n"
				+ "  select count(distinct c.id) \r\n"
				+ "  from contractor_info c\r\n"
				+ "  join accounts a on c.id = a.id\r\n"
				+ "  where a.status = 'Active'\r\n"
				+ "  and a.type = 'Contractor'\r\n"
				+ "  and c.welcomeAuditor_id = u.id\r\n"
				+ "  and exists(\r\n"
				+ "    select * from invoice i\r\n"
				+ "    left join invoice_item item on i.id = item.invoiceID\r\n"
				+ "    left join invoice_fee fee on fee.id = item.feeID\r\n"
				+ "    where i.accountID = c.id\r\n"
				+ "    and i.status <> 'Void'\r\n"
				+ "    and fee.feeClass = 'InsureGUARD'\r\n"
				+ "    )\r\n"
				+ ") as numWithInsureGuard\r\n"
				+ "\r\n"
				+ "from users u\r\n"
				+ "left join contractor_info c on u.id = c.welcomeAuditor_id\r\n"
				+ "left join accounts a on a.id = c.id and a.status in ('Active', 'Requested') and a.type = 'Contractor'\r\n"
				+ "\r\n" + "where u.assignmentCapacity > 0\r\n" + "and u.isActive = 'Yes'\r\n"
				+ "and u.accountID = 1100\r\n" + "\r\n" + "group by u.id\r\n" + "order by u.name";

		Database db = new Database();
		data = db.select(sql, true);

		return SUCCESS;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}
}
