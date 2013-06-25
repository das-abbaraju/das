package com.picsauditing.dao.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.picsauditing.search.QueryMapper;

public class GenericQueryMapper<E> implements QueryMapper<E> {

	@Override
	public void mapObjectToPreparedStatement(E object, PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setObject(1, object);
	}

}
