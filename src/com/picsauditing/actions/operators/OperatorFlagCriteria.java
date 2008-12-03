package com.picsauditing.actions.operators;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.OperatorAccountDAO;

public class OperatorFlagCriteria extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;
	private FlagCalculator2 flagCalculator;
	
	public OperatorFlagCriteria(OperatorAccountDAO operatorDao, FlagCalculator2 flagCalculator) {
		super(operatorDao);
		this.flagCalculator = flagCalculator;
	}

	public String execute() throws Exception {
		getOperator();
		
		return SUCCESS;
	}
	
	
}
