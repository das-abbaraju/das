package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@Transactional
public class IndexableDAO extends PicsDAO {
	public BaseTable find(int id) {
		return em.find(BaseTable.class, id);
	}

	public Indexable find(Class<? extends Indexable> clazz, int id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public List<Indexable> find(Class<? extends Indexable> clazz, Collection<Integer> ids) {
		if (ids == null || ids.isEmpty())
			return Collections.emptyList();
		Query query = em.createQuery("FROM " + clazz.getName() + " I WHERE I IN (" + Strings.implode(ids) + ")");
		return query.getResultList();
	}

	/**
	 * @param indexable
	 *            Table we are indexing
	 * @param startId
	 *            Id to start at, inclusive. If this is set and endId is not
	 *            then pull up just the id matching startId. Set to 0 and endId
	 *            to 0 to pull up all indexables
	 * @param endId
	 *            Id to end at, inclusive. If this is set less than startId then
	 *            pull up nothing
	 * @return Set of ids of a type that need to be indexed
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Set<Integer> getIndexable(Class<? extends Indexable> clazz, int startId, int endId) {
		String tableName = clazz.getAnnotation(Table.class).name();
		SelectSQL sql = new SelectSQL(tableName);
		sql.addField("id");
		sql.addWhere("needsIndexing = 1");
		if (startId > 0 && endId == 0)
			sql.addWhere("id = " + startId);
		else if (startId > 0 && endId > 0 && (endId > startId)) {
			sql.addWhere("id BETWEEN " + startId + " AND " + endId);
		} else if (!(startId >= endId)) {
			return Collections.emptySet();
		}
		if (tableName.equals("users"))
			sql.addWhere("isGroup = 'No'");
		if (tableName.equals("accounts"))
			sql.addWhere("status NOT IN ('Deleted', 'Deactivated')");
		sql.addOrderBy("id");
		Query query = em.createNativeQuery(sql.toString());
		
		return new HashSet<Integer>(query.getResultList());
	}

}
