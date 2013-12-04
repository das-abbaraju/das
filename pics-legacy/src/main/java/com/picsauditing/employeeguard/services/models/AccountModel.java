package com.picsauditing.employeeguard.services.models;

public final class AccountModel implements Comparable<AccountModel> {

	private final int id;
	private final String name;
	private final AccountType accountType;

	public AccountModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.accountType = builder.accountType;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public boolean isCorporate() {
		return accountType == AccountType.CORPORATE;
	}

	@Override
	public int compareTo(AccountModel that) {
		return this.name.compareToIgnoreCase(that.name);
	}

	public static class Builder {
		private int id;
		private String name;
		private AccountType accountType;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder accountType(AccountType accountType) {
			this.accountType = accountType;
			return this;
		}

		public AccountModel build() {
			return new AccountModel(this);
		}
	}
}
