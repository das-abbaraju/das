package com.picsauditing.selenium;

import java.util.List;

public interface SeleniumDeletable {

	public int getID();
	public String getName();
	public String getType();
	public boolean IDisIn(List<Integer> IDList);
	public boolean isAnAccount();
	public boolean isAnEmployee();
	public boolean isUser();
}
