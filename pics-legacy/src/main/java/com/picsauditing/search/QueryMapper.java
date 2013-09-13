package com.picsauditing.search;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Generic mapper for mapping any object type to the prepared statement, for any type of
 * query operation.
 * 
 * FIXME: Refactor and move this to a better place.
 */
public interface QueryMapper<T> {
	
	void mapObjectToPreparedStatement(T object, PreparedStatement preparedStatement) throws SQLException;

}