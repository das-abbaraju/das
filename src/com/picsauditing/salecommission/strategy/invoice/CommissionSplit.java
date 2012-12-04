package com.picsauditing.salecommission.strategy.invoice;

import java.util.Map;

import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.FeeClass;

public class CommissionSplit {
	
	private AccountUser accountUser;
	private Map<FeeClass, Double> revenueBreakDown;
	
	public AccountUser getAccountUser() {
		return accountUser;
	}
	
	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}
	
	public Map<FeeClass, Double> getRevenueBreakDown() {
		return revenueBreakDown;
	}
	
	public void setRevenueBreakDown(Map<FeeClass, Double> revenueBreakDown) {
		this.revenueBreakDown = revenueBreakDown;
	}
	
	public double sumOfServiceLevelRevenues() {
		double sum = 0;
		for (Double revenueForService : revenueBreakDown.values()) {
			if (revenueForService != null) {
				sum += revenueForService.doubleValue();
			}
		}
		
		return sum;
	}
	
	public double getRevenueForServiceLevel(FeeClass feeClass) {
		if (revenueBreakDown.containsKey(feeClass)) {
			Double value = revenueBreakDown.get(feeClass);
			return value == null ? 0 : value.doubleValue();
		}
		
		return 0;
	}
	
}
