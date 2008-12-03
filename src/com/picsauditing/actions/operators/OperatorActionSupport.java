package com.picsauditing.actions.operators;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorActionSupport extends PicsActionSupport {
	private static final long serialVersionUID = 8967320010000259378L;
	protected int id;
	protected OperatorAccountDAO operatorDao;
	protected OperatorAccount operator;

	public OperatorActionSupport(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperatorAccount getOperator() {
		loadPermissions();
		if (operator == null) {
			if (!permissions.hasPermission(OpPerms.AllOperators))
				if (permissions.isOperator())
					id = permissions.getAccountId();
				else
					return null;
			operator = operatorDao.find(id);
		}
		return operator;
	}

}
