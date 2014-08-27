package com.picsauditing.user.model;

public class UserInfo implements Nameable {

	private int userId;
    private int appUserId;
	private int accountId;
	private String name;
	private AccountType type;

    public UserInfo(int appUserId, int userId, int accountId, String name, AccountType type) {
        this.appUserId = appUserId;
        this.userId = userId;
        this.accountId = accountId;
        this.name = name;
        this.type = type;
    }

    public void setAppUserId(int appUserId) {
        this.appUserId = appUserId;
    }

    public int getAppUserId() {
		return appUserId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

    public int getUserId() {
        return userId;
    }

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

}
