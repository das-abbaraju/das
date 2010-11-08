package com.picsauditing.actions.operators;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
	private List<OperatorAccount> allParents = null;
	
	public OperatorConfiguration(OperatorAccountDAO operatorDao) {
		super(operatorDao);
	}
	
	public void prepare() throws Exception {
		id = getParameter("id");

		if (id > 0) {
			findOperator();
			subHeading = "Edit " + operator.getType();
		}
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		
		allParents = operatorDao.findWhere(true, "a.id IN (" + Strings.implode(operator.getOperatorHeirarchy()) + ")", permissions);
		
		return SUCCESS;
	}
	
	public List<OperatorAccount> getAllParents() {
		return allParents;
	}
}