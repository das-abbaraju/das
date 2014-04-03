package com.picsauditing.actions;

import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.Database;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.ReflectUtil;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractIndexerEngine implements IndexerEngine {

    @Autowired
	protected IndexableDAO indexableDao;

	protected Database db = new Database();

	private static Set<Class<? extends Indexable>> entries = new HashSet<Class<? extends Indexable>>();

	static {
		entries.add(Account.class);
		entries.add(User.class);
		entries.add(Employee.class);
		entries.add(Trade.class);
	}

	@Override
	public void updateIndex(int id, Class<? extends Indexable> clazz) {
		Set<Integer> saved = new HashSet<Integer>();
		saved.add(id);
		updateIndex(saved, clazz);
	}

	@Override
	public void updateIndex(Set<Integer> saved, Class<? extends Indexable> clazz) {
		if (saved.isEmpty())
			return;
		String updateIndexing = "UPDATE " + ReflectUtil.getTableName(clazz) + " SET needsIndexing=0 WHERE id IN ("
				+ Strings.implode(saved) + ")";
		try {
			db.executeUpdate(updateIndexing);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}

	public void setIndexableDao(IndexableDAO indexableDao) {
		this.indexableDao = indexableDao;
	}

	public Set<Class<? extends Indexable>> getEntries() {
		return entries;
	}

}
