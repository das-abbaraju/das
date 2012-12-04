package com.picsauditing.billing.strategy;

public interface FeeAggregationStrategy {

	boolean applicable();
	
	void execute();
	
}
