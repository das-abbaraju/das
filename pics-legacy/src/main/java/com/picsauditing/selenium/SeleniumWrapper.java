package com.picsauditing.selenium;

import java.util.List;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.User;

public class SeleniumWrapper implements SeleniumDeletable {
	
	private static final String USER = "USER";
	private static final String EMPLOYEE = "EMPLOYEE";
    private static final String CONTRACTOR = "CONTRACTOR";
    private static final String OPERATOR = "OPERATOR";
    private static final String ADMIN = "ADMIN";
    private static final String CORPORATE = "CORPORATE";
    private static final String ASSESSMENT = "ASSESSMENT";
	
	private int ID;
	private String NAME;
	private String TYPE;
	
	
	public SeleniumWrapper (Account account) {
		ID = account.getId();
		NAME = account.getName();
		TYPE = account.getType().toUpperCase();
	}
	
	public SeleniumWrapper (User user) {
		ID = user.getId();
		NAME = user.getName();
		TYPE = USER;
	}
	
	public SeleniumWrapper (Employee emp) {
		ID = emp.getId();
		NAME = emp.getName();
		TYPE = EMPLOYEE;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return NAME;
	}

	public String getType() {
		return TYPE;
	}

	public boolean IDisIn(List<Integer> IDList) {
		return (null == IDList)? false : IDList.contains(ID);
	}

	public boolean isAnAccount() {
		return !(TYPE.equals(USER) || TYPE.equals(EMPLOYEE));
	}

	public boolean isAnEmployee() {
		return TYPE.equals(EMPLOYEE);
	}

	public boolean isUser() {
		return TYPE.equals(USER);
	}

    public boolean isClientSite() {
        return TYPE.equals(OPERATOR) || TYPE.equals(CORPORATE);
    }

    public boolean isContractor() {
        return TYPE.equals(CONTRACTOR);
    }
}
