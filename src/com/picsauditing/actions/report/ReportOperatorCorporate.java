package com.picsauditing.actions.report;

import javax.naming.NoPermissionException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.SpringUtils;

public class ReportOperatorCorporate extends ReportAccount {
	protected String accountType;
	boolean canEdit = false;
	boolean canDelete = false;
	protected int accountID;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		loadPermissions();
		if (accountType == null)
			accountType = "Operator";

		if (accountType.equals("Operator")) {
			permissions.tryPermission(OpPerms.ManageOperators);
			canEdit = permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
			canDelete = permissions.hasPermission(OpPerms.ManageOperators, OpType.Delete);
			sql
					.addJoin("LEFT JOIN (SELECT genID, count(*) as subCount FROM generalContractors GROUP BY genID) sub ON sub.genID = a.id");
			sql.addField("subCount");
			sql.addWhere("a.type = 'Operator'");
			sql.setType(Type.Operator);
		} else if (accountType.equals("Corporate")) {
			permissions.tryPermission(OpPerms.ManageCorporate);
			canEdit = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Edit);
			canDelete = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
			sql
					.addJoin("LEFT JOIN (SELECT corporateID, count(*) as subCount FROM facilities GROUP BY corporateID) sub ON sub.corporateID = a.id");
			sql.addField("subCount");
			sql.addWhere("a.type='Corporate'");
			sql.setType(null);
		}
		sql.addField("a.contact");
		sql.addField("a.industry");
		sql.addField("a.state");
		sql.addField("a.city");
		if (this.orderBy == null)
			this.orderBy = "a.name";
		this.run(sql);

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
		return SUCCESS;
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
