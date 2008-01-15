package com.picsauditing.dao;

import java.util.Map;

import com.picsauditing.jpa.entities.*;

public interface OshaLogDAO extends GenericDAO <OshaLog, Short>{
	@SuppressWarnings("unchecked")
	public OshaLog editLog(Map parameters, String oID) throws Exception;
}
