package com.picsauditing.employeeguard.models;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountModel that = (AccountModel) o;

        if (id != that.id) return false;
        if (accountType != that.accountType) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
        return result;
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
