package com.picsauditing.actions.operators;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorActionSupport extends PicsActionSupport {
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
		if (operator == null)
			operatorDao.find(id);
		return operator;
	}

}
