package com.picsauditing.jpa.entities;

public enum PaymentMethod implements Translatable {
	CreditCard, Check, Refund, EFT, BadDebtCreditMemo, ReturnCreditMemo;
	// EFT is for wiring the money
	public boolean isCreditCard() {
		return this.equals(CreditCard);
	}

	public boolean isCheck() {
		return this.equals(Check);
	}

	public boolean isRefund() {
		return this.equals(Refund);
	}

	public boolean isEFT() {
		return this.equals(EFT);
	}

    public boolean isBadDebt() {
        return this.equals(BadDebtCreditMemo);
    }

	public boolean isReturnCreditMemo() {
		return this.equals(ReturnCreditMemo);
	}

	public boolean isCreditMemo() {
		return this.equals(BadDebtCreditMemo) || this.equals(ReturnCreditMemo);
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}