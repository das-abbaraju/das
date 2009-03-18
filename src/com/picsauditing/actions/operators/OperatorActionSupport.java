package com.picsauditing.actions.operators;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorActionSupport extends PicsActionSupport {
	private static final long serialVersionUID = 8967320010000259378L;
	protected int id;
	protected OperatorAccountDAO operatorDao;
	private OperatorAccount operator;

	public OperatorActionSupport(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperatorAccount getOperator() throws Exception {
		if (operator == null) {
			loadPermissions();
			
			if (id == 0) {
				// Check the parameter list just in case it hasn't been set yet 
				// ie this is being called from a Prepare method
				id = this.getParameter("id");
			}

			if (permissions.isOperator())
				id = permissions.getAccountId();
			else if (id == 0)
				throw new Exception("Missing operator id");
			else if (permissions.isCorporate()) {
				if (!permissions.getOperatorChildren().contains(id))
					throw new Exception("Corporate account doesn't have access to that operator");
			} else {
				permissions.tryPermission(OpPerms.AllOperators);
			}

			operator = operatorDao.find(id);
		}
		return operator;
	}

}
