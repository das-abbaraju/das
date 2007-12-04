package com.picsauditing.domain;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

public interface IPicsDO {
	
	public void setFromResultSet(ResultSet SQLResult) throws Exception;
	public void setFromRequest(HttpServletRequest request);
}
