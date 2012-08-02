package com.picsauditing.salecommission.service.strategy;

public interface ContractorCommissionStrategy<T> {

	public ContractorCommissionResults calculateCommission(T data);
	
}
