package com.picsauditing.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.actions.AbstractIndexerEngine;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.Database;

public final class SQLIndexerEngine extends AbstractIndexerEngine {

	// Number of records to run at a time
	private static final int BATCH_NUM = 75;
	private final Database db = new Database();

	private static final List<String> STATS_QUERY_BUILDER = Collections.unmodifiableList(Arrays.asList(
			"TRUNCATE TABLE app_index_stats;",
			"REPLACE INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;",
			"REPLACE INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;",
			"REPLACE INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;",
			"ANALYZE TABLE app_index, app_index_stats;" 
	));

	private static final String QUERY_INDEX = "INSERT IGNORE INTO app_index VALUES(?,?,?,?)";
	private static final String QUERY_STATS = "INSERT IGNORE INTO app_index_stats VALUES(?,?,?)";
	private static final String QUERY_DELETE = "DELETE FROM app_index WHERE foreignKey = ? AND indexType = ?";
	
	private static final Logger logger = LoggerFactory.getLogger(SQLIndexerEngine.class);

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
			logger.error("Error during run for class {}", clazz.getName(), e);
		} finally {
			DatabaseUtil.closeConnection(connection);
			DatabaseUtil.closeStatement(indexBatch);
			DatabaseUtil.closeStatement(statsBatch);
		}

	}

	@Override
	public void runSingle(Indexable toIndex) {
		if (toIndex == null) {
			return;
		} else {
			Connection connection = null;
			PreparedStatement indexBatch = null;
			PreparedStatement statsBatch = null;
			
			try {
				connection = DBBean.getDBConnection();
				connection.setAutoCommit(false);

				if (!toIndex.isRemoved()) {
					indexBatch = connection.prepareStatement(QUERY_INDEX);
					statsBatch = connection.prepareStatement(QUERY_STATS);
					indexSingle(toIndex, indexBatch);
					insertStats(toIndex, statsBatch);
				}

				deleteSingle(toIndex);

				if (!toIndex.isRemoved()) {
					indexBatch.executeBatch();
					statsBatch.executeBatch();
				}

				connection.commit();
			} catch (SQLException e) {
				logger.error("Error during query for Index Class {}", toIndex.getClass().getName(), e);
			} finally {
				DatabaseUtil.closeConnection(connection);
				DatabaseUtil.closeStatement(indexBatch);
				DatabaseUtil.closeStatement(statsBatch);
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
			logger.error("Error during delete", e);
		} finally {
			DatabaseUtil.closeConnection(connection);
		}
	}

	@Override
	public void deleteSingle(Indexable toDelete) {
		List<Indexable> listToDelete = Arrays.asList(toDelete);
		delete(listToDelete);
	}

	private final void updateStats() {
		for (String stats : STATS_QUERY_BUILDER) {
			try {
				db.executeInsert(stats);
			} catch (SQLException e) {
				logger.error("Error while executing an insert in the SQLIndexerEngine.", e);
			}
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

}
