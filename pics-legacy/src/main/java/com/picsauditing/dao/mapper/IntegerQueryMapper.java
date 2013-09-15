package com.picsauditing.dao.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.picsauditing.search.QueryMapper;

public class IntegerQueryMapper implements QueryMapper<Integer> {

	@Override
	public void mapObjectToPreparedStatement(Integer number, PreparedStatement preparedStatement) throws SQLException {
		if (number == null || preparedStatement == null) {
			throw new IllegalArgumentException("PreparedStatement and Integer number cannot be null.");
		}
		
		preparedStatement.setInt(1, number);
	}

}