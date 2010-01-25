package com.picsauditing.actions.report;

import javax.naming.NoPermissionException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportOperatorCorporate extends ReportAccount {
	protected String accountType = "Operator";
	boolean canEdit = false;
	boolean canDelete = false;
	protected int accountID;

	@Override
	public void checkPermissions() throws Exception {
		if (accountType.equals("Operator"))
			permissions.tryPermission(OpPerms.ManageOperators);
		else if (accountType.equals("Corporate"))
			permissions.tryPermission(OpPerms.ManageCorporate);
	}
	
	@Override
	public void buildQuery() {
		if (accountType.equals("Operator")) {
			canEdit = permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
			canDelete = permissions.hasPermission(OpPerms.ManageOperators, OpType.Delete);
			sql.addJoin("LEFT JOIN (SELECT genID, count(*) as subCount FROM generalContractors GROUP BY genID) sub ON sub.genID = a.id");
			sql.addField("subCount");
			// check to see for any requested contractors
			sql.addJoin("LEFT JOIN (SELECT requestedByID, count(*) as requestedBy FROM contractor_info GROUP BY requestedByID) requested ON requested.requestedByID = a.id");
			sql.addField("requestedBy");

			sql.addWhere("a.type = 'Operator'");
			sql.setType(Type.Operator);
		} else if (accountType.equals("Corporate")) {
			canEdit = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Edit);
			canDelete = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
			sql.addJoin("LEFT JOIN (SELECT corporateID, count(*) as subCount FROM facilities GROUP BY corporateID) sub ON sub.corporateID = a.id");
			sql.addField("subCount");
			sql.addWhere("a.type='Corporate'");
			sql.setType(null);
		}
		sql.addJoin("left JOIN users contact ON contact.id = a.contactID");
		sql.addField("contact.name AS contactname");
		sql.addField("a.industry");
		sql.addField("a.state");
		sql.addField("a.city");
		addFilterToSQL();
	}
	
	@Override
	public String execute() throws Exception {
		if ("Remove".equals(button)) {
			OperatorAccountDAO operatorAccountDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
			OperatorAccount operatorAccount = operatorAccountDAO.find(accountID);

			if (operatorAccount.getType().equals("Operator"))
				permissions.tryPermission(OpPerms.ManageOperators, OpType.Delete);
			else if (operatorAccount.getType().equals("Corporate"))
				permissions.tryPermission(OpPerms.ManageCorporate, OpType.Delete);
			else
				throw new NoPermissionException("Delete Account");

			boolean removed = operatorAccountDAO.removeAllByOpID(operatorAccount, getFtpDir());
			if (!removed)
				addActionError("Cannot Remove this account" + operatorAccount.getName());
		}
		return super.execute();
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

}
