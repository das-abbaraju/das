package com.picsauditing.actions.contractors;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class PQFVerificationWidget extends PicsActionSupport {
	public List<BasicDynaBean> getPqfVerifications() {
		SelectAccount sql = new SelectAccount();

		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.setType(SelectAccount.Type.Contractor);

        sql.addJoin("JOIN account_user au on au.accountID = a.id and au.role='PICSCustomerServiceRep' and au.startDate < now() and au.endDate > now()");

		sql.addJoin("JOIN users csr ON csr.id = au.userID");
		sql.addField("csr.name csr_name");
		sql.addWhere("csr.id = " + permissions.getShadowedUserID());

		sql.addJoin("JOIN contractor_audit ca ON ca.conid = a.id");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addWhere("cao.status IN ('Submitted','Resubmitted')");
		sql.addWhere("ca.auditTypeID IN (1,11) AND (ca.expiresDate IS NULL OR ca.expiresDate > NOW())");
		sql.addField("MIN(cao.statusChangedDate) as completedDate");
		sql.addWhere("c.accountLevel = 'Full'");
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("c.id not in (" + "select c.id from contractor_info c " + "join invoice i on i.accountID = c.id "
				+ "join invoice_item ii on i.id = ii.invoiceID join invoice_fee invf on ii.feeID = invf.id "
				+ "where invf.feeClass = 'Membership' and invf.id != 100 and invf.id != 4 and i.status = 'Unpaid'"
				+ " and (ii.amount = invf.defaultAmount or i.totalAmount >= 450))");
		sql.addGroupBy("ca.conid");
		sql.addOrderBy("cao.statusChangedDate");
		sql.setLimit(10);

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), false);
			return pageData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
