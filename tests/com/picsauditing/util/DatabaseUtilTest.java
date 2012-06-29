package com.picsauditing.util;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DatabaseUtilTest {
	
	@Mock
	ResultSet resultSet;
	
	@Mock
	Statement statement;
	
	@Mock 
	Connection connection;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testCloseResultSet() throws SQLException {
		DatabaseUtil.closeResultSet(resultSet);
		verify(resultSet).close();
	}
	
	@Test
	public void testCloseConnection() throws SQLException {
		DatabaseUtil.closeConnection(connection);
		verify(connection).close();
	}
	
	@Test
	public void testCloseStatement() throws SQLException {
		DatabaseUtil.closeStatement(statement);
		verify(statement).close();
	}

}
