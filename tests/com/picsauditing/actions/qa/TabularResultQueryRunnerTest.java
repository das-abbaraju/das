package com.picsauditing.actions.qa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import com.picsauditing.search.SelectSQL;

public class TabularResultQueryRunnerTest {
	private Date now = new Date();
	private Object[][] testData = { { 10, 100, "Green", "Green", now }, { 20, 200, "Red", "Red", now } };

	private TabularResultQueryRunner analysis;

	@Mock
	private Connection connection;
	@Mock
	private Statement statement;
	@Mock
	private ResultSet resultSet;
	@Mock
	private ResultSetMetaData resultSetMetaData;
	@Mock
	private TabularModel returnedData;
	@Mock
	private SelectSQL query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		analysis = (TabularResultQueryRunner) QueryRunnerFactory.instance(query);
		analysis.setDbConnection(connection);
	}

	@SuppressWarnings(value = { "rawtypes" })
	@Test
	public void testRun_InitialQueryInitialData() throws Exception {
		when(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(statement);
		when(statement.executeQuery(anyString())).thenReturn(resultSet);
		when(resultSet.next()).thenAnswer(new Answer() {
			private int countCalls = 0;

			public Object answer(InvocationOnMock invocation) {
				if (countCalls++ < testData.length) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			}
		});
		when(resultSet.getObject(anyInt())).thenAnswer(new Answer() {
			private int countCalls = 1;

			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				int row = countCalls / (testData[0].length + 1);
				int column = (Integer) args[0] - 1;
				countCalls++;
				return testData[row][column];
			}
		});

		TabularModel data = analysis.run();

		for (int i = 1; i <= data.getRowCount(); i++) {
			for (int j = 1; j <= data.getColumnCount(); j++) {
				assertEquals(testData[i - 1][j - 1], data.getValueAt(i, j));
			}
		}
	}

	@Test
	public void testSetColumnNamesOnData() throws Exception {
		List<String> columnNames = new ArrayList<String>() {
			{
				add("foo");
				add("bar");
				add("baz");
			}
		};
		when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
		when(resultSetMetaData.getColumnCount()).thenReturn(columnNames.size());
		for (int i = 1; i <= columnNames.size(); i++) {
			when(resultSetMetaData.getColumnLabel(i)).thenReturn(columnNames.get(i - 1));
		}

		TabularResultQueryRunner analysis = (TabularResultQueryRunner) QueryRunnerFactory.instance(query, returnedData);
		analysis.setDbConnection(connection);

		Whitebox.invokeMethod(analysis, "setColumnNamesOnData", resultSet);

		verify(returnedData).setColumnNames(columnNames);
	}
}
