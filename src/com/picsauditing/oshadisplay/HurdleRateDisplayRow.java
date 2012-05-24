package com.picsauditing.oshadisplay;

import com.picsauditing.jpa.entities.OperatorAccount;

public class HurdleRateDisplayRow extends OshaDisplayRow{
	private OperatorAccount operator;
	
	@Override
	public String getTitle() {
		return operator.getName();
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Override
	public boolean isHurdleRate() {
		return true;
	}
}
