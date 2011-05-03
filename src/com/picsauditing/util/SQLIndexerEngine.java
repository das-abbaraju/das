package com.picsauditing.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.actions.AbstractIndexerEngine;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.Database;

public final class SQLIndexerEngine extends AbstractIndexerEngine {

	// Number of records to run at a time
	private static final int BATCH_NUM = 75;
	private final Database db = new Database();

	private static final String[] STATS_QUERY_BUILDER = {
			"TRUNCATE TABLE app_index_stats;",
			"INSERT INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;",
			"INSERT INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;",
			"INSERT INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;",
			"ANALYZE TABLE app_index, app_index_stats;" };

	private static final String QUERY_INDEX = "INSERT IGNORE INTO app_index VALUES(?,?,?,?)";
	private static final String QUERY_STATS = "INSERT IGNORE INTO app_index_stats VALUES(?,?,?)";
	private static final String QUERY_DELETE = "DELETE FROM app_index WHERE foreignKey = ? AND indexType = ?";

	@Override
	public void runAll(Set<Class<? extends Indexable>> toIndex) {
		for (Class<? extends Indexable> index : toIndex) {
			run(index);
		}
		updateStats();
	}

	@Override
	public void run(Class<? extends Indexable> clazz) {
		Set<Integer> needsIndexingIds = indexableDao.getIndexable(clazz, 0, 0);
		if (needsIndexingIds.isEmpty())
			return;

		Connection connection = null;
		PreparedStatement indexBatch = null;
		PreparedStatement statsBatch = null;
		try {
			connection = DBBean.getDBConnection();
			connection.setAutoCommit(false);
			indexBatch = connection.prepareStatement(QUERY_INDEX);
			statsBatch = connection.prepareStatement(QUERY_STATS);
			Set<Integer> savedIds = new HashSet<Integer>();

			for (IndexingIterator<Integer> it = IndexingIterator.getIterator(needsIndexingIds); it.hasNext();) {
				Set<Integer> pullIds = it.next(BATCH_NUM);
				List<Indexable> indexableList = indexableDao.find(clazz, pullIds);
				for (Indexable toIndex : indexableList) {
					indexSingle(toIndex, indexBatch);
					insertStats(toIndex, statsBatch);
				}
				delete(indexableList);
				savedIds.addAll(pullIds);
				indexBatch.executeBatch();
				statsBatch.executeBatch();
				indexBatch.clearBatch();
				statsBatch.clearBatch();
				connection.commit();
				updateIndex(savedIds, clazz);
				savedIds.clear();
			}
		} catch (Exception e) {
			// TODO: handle exception
			// Query didn't work, or db failed. Should we catch?
			e.printStackTrace();
		} finally {
			closeConnection(connection);
			closeStatement(indexBatch);
			closeStatement(statsBatch);
		}

	}

	@Override
	public void runSingle(Class<? extends Indexable> clazz, int id) {
		Indexable toIndex = indexableDao.findIndexable(clazz, id);
		if (toIndex == null) {
			return;
		} else {
			Connection connection = null;
			PreparedStatement indexBatch = null;
			PreparedStatement statsBatch = null;
			try {
				connection = DBBean.getDBConnection();
				connection.setAutoCommit(false);
				indexBatch = connection.prepareStatement(QUERY_INDEX);
				statsBatch = connection.prepareStatement(QUERY_STATS);
				indexSingle(toIndex, indexBatch);
				insertStats(toIndex, statsBatch);
				deleteSingle(toIndex);
				indexBatch.executeBatch();
				statsBatch.executeBatch();
				connection.commit();
				updateIndex(toIndex.getId(), toIndex.getClass());
			} catch (SQLException e) {
				// TODO: handle exception
				// Query didn't work, or db failed. Should we catch?
			} finally {
				closeConnection(connection);
				closeStatement(indexBatch);
				closeStatement(statsBatch);
			}
		}

	}

	@Override
	public void delete(List<Indexable> listToDelete) {
		Connection connection = null;
		try {
			connection = DBBean.getDBConnection();
			connection.setAutoCommit(false);
			PreparedStatement deleteBatch = connection.prepareStatement(QUERY_DELETE);
			for (Indexable record : listToDelete) {
				deleteBatch.setInt(1, record.getId());
				deleteBatch.setString(2, record.getIndexType());
				deleteBatch.addBatch();
				deleteBatch.clearParameters();
			}
			deleteBatch.executeBatch();
			connection.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public void deleteSingle(Indexable toDelete) {
		List<Indexable> listToDelete = Arrays.asList(toDelete);
		delete(listToDelete);
	}

	private final void updateStats() {
		try {
			for (String stats : STATS_QUERY_BUILDER) {
				db.executeInsert(stats);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final void insertStats(Indexable indexable, PreparedStatement statsBatch) throws SQLException {
		String indexType = indexable.getIndexType();
		for (IndexObject stat : indexable.getIndexValues()) {
			int i = 1;
			statsBatch.setString(i++, indexType);
			statsBatch.setString(i++, stat.getValue());
			statsBatch.setInt(i++, 1);
			statsBatch.addBatch();
			statsBatch.clearParameters();
		}

	}

	private final void indexSingle(Indexable indexable, PreparedStatement indexBatch) throws SQLException {
		for (IndexObject insertValue : indexable.getIndexValues()) {
			int i = 1;
			indexBatch.setString(i++, indexable.getIndexType());
			indexBatch.setInt(i++, indexable.getId());
			indexBatch.setString(i++, insertValue.getValue());
			indexBatch.setInt(i++, insertValue.getWeight());
			indexBatch.addBatch();
			indexBatch.clearParameters();
		}
	}

	private final void closeConnection(Connection connection) {
		if (connection == null)
			return;
		try {
			connection.close();
		} catch (SQLException e) {
			// Error while calling close
			e.printStackTrace();
		}
	}

	private final void closeStatement(Statement statement) {
		if (statement == null)
			return;
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
