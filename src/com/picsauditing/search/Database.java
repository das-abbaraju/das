package com.picsauditing.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.util.DatabaseUtil;
import com.picsauditing.util.Strings;

/**
 * TODO: Refactor this to use anonymous classes within a static method to
 * execute the queries so we don't need to constantly create a new connection
 * for every single method.
 */
public class Database {

	private static final int BATCH_SIZE = 500;

	private int allRows = 0;

	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> select(String sql, boolean countRows) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            return select(connection, sql, countRows);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> selectReadOnly(String sql, boolean countRows) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getReadOnlyConnection();
            return select(connection, sql, countRows);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    public List<BasicDynaBean> select(Connection connection, String sql, boolean countRows) throws SQLException {
		Statement stmt = null;
		ResultSet tempRS = null;
		ResultSet rs = null;
		RowSetDynaClass rsdc;

		try {
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			rsdc = new RowSetDynaClass(rs, false, true);
			if (countRows) {
				tempRS = stmt.executeQuery("SELECT FOUND_ROWS()");
				tempRS.next();
				allRows = tempRS.getInt(1);
			}

			return rsdc.getRows();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeResultSet(tempRS);
			DatabaseUtil.closeStatement(stmt);
		}
	}

	public long executeInsert(String sql) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            return executeInsert(connection, sql);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

	public long executeInsert(Connection connection, String sql) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();

			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();

			long id = -1;
			if (rs.next()) {
				id = rs.getLong(1);
			}

			return id;
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(stmt);
		}
	}

	public int executeUpdate(String sql) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            return executeUpdate(connection, sql);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

	public int executeUpdate(Connection connection, String sql) throws SQLException {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			return stmt.executeUpdate(sql);
		} finally {
			DatabaseUtil.closeStatement(stmt);
		}
	}

	public boolean execute(String sql) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            return execute(connection, sql);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }

    }

    public boolean execute(Connection connection, String sql) throws SQLException {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			return stmt.execute(sql);
		} finally {
			DatabaseUtil.closeStatement(stmt);
		}
	}

	public boolean executeReadOnly(String sql) throws SQLException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = DBBean.getReadOnlyConnection();
			stmt = connection.createStatement();

			return stmt.execute(sql);
		} finally {
			DatabaseUtil.closeStatement(stmt);
			DatabaseUtil.closeConnection(connection);
		}
	}

	public int getAllRows() {
		return allRows;
	}

	static public int toInt(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);
		return Integer.parseInt(value.toString());
	}

	public static boolean toBoolean(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);

		if (value.toString().equals("1")) {
			return true;
		}

		return Boolean.parseBoolean(value.toString());
	}

	public static float toFloat(BasicDynaBean row, String columnName) {
		Object value = row.get(columnName);
		return Float.parseFloat(value.toString());
	}

	public static String getDatabaseName() throws SQLException {
		Connection connection = null;
		String databaseName = "";

		try {
			connection = DBBean.getDBConnection();

			databaseName = connection.getCatalog();
		} finally {
			DatabaseUtil.closeConnection(connection);
		}

		return databaseName;
	}

	public <T> List<T> select(String sql, RowMapper<T> rowMapper) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            return select(connection, sql, rowMapper);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

	public <T> List<T> select(Connection connection, String sql, RowMapper<T> rowMapper) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		List<T> results = new ArrayList<T>();
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSet = statement.executeQuery(sql);

			int row = 0;
			while (resultSet.next()) {
				results.add(rowMapper.mapRow(resultSet, row));
				row++;
			}
		} finally {
			DatabaseUtil.closeResultSet(resultSet);
			DatabaseUtil.closeStatement(preparedStatement);
		}

		return results;
	}

	public static <T, E> List<T> select(String sql, E queryObject, QueryMapper<E> queryMapper, RowMapper<T> rowMapper)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		List<T> results = new ArrayList<T>();
		try {
			connection = DBBean.getDBConnection();
			preparedStatement = connection.prepareStatement(sql);
			queryMapper.mapObjectToPreparedStatement(queryObject, preparedStatement);

			resultSet = preparedStatement.executeQuery();

			int row = 0;
			while (resultSet.next()) {
				results.add(rowMapper.mapRow(resultSet, row));
				row++;
			}
		} finally {
			DatabaseUtil.closeResultSet(resultSet);
			DatabaseUtil.closeStatement(preparedStatement);
			DatabaseUtil.closeConnection(connection);
		}

		return results;
	}

	public static <T> void executeBatch(String sql, List<T> items, QueryMapper<T> queryMapper) throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getDBConnection();
            executeBatch(connection, sql, items, queryMapper);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }

    }
	public static <T> void executeBatch(Connection connection, String sql, List<T> items, QueryMapper<T> queryMapper) throws SQLException {
		if (Strings.isEmpty(sql) || CollectionUtils.isEmpty(items) || queryMapper == null) {
			return;
		}
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(sql);

			int count = 0;
			for (T item : items) {
				queryMapper.mapObjectToPreparedStatement(item, preparedStatement);
				preparedStatement.addBatch();

				// once we hit the batch size maximum, run the batch
				if (++count % BATCH_SIZE == 0) {
					preparedStatement.executeBatch();
				}
			}

			preparedStatement.executeBatch();
		} finally {
			DatabaseUtil.closeStatement(preparedStatement);
		}
	}

	public void executeBatch(List<String> queries) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = DBBean.getDBConnection();
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			int count = 0;
			for (String query : queries) {
				statement.addBatch(query);

				if (++count % BATCH_SIZE == 0) {
					statement.executeBatch();
				}
			}
            // execute those statements that didn't make batch size
            statement.executeBatch();
		} finally {
			DatabaseUtil.closeResultSet(resultSet);
			DatabaseUtil.closeStatement(preparedStatement);
			DatabaseUtil.closeConnection(connection);
		}
	}

}
