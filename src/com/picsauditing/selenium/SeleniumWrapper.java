package com.picsauditing.selenium;

import java.util.List;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.User;

public class SeleniumWrapper implements SeleniumDeletable {
	
	private static final String USER = "USER";
	private static final String EMPLOYEE = "EMPLOYEE";
	
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

}
