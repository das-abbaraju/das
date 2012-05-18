package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

public class RegistrationCommissionTier {
	private int minContractors;
	private int maxContractors;
	private BigDecimal amountPerContractor = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);

	public RegistrationCommissionTier(int minContractors, int maxContractors, BigDecimal amountPerContractor) {
		this.minContractors = minContractors;
		this.maxContractors = maxContractors;
		this.amountPerContractor = amountPerContractor;
	}

	public int getMinContractors() {
		return minContractors;
	}

	public int getMaxContractors() {
		return maxContractors;
	}

	public BigDecimal getAmountPerContractor() {
		return amountPerContractor;
	}
}
