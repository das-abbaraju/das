package com.picsauditing.actions;

import java.sql.SQLException;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Anonymous
	public String execute() throws SQLException {
		
		return SUCCESS;
	}
}