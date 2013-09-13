package com.picsauditing.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReflectUtil;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class IndexableDAO extends PicsDAO {
	public BaseTable find(int id) {
		return em.find(BaseTable.class, id);
	}

	public Indexable findIndexable(Class<? extends Indexable> clazz, int id) {
		return em.find(clazz, id);
	}

	public List<Indexable> find(Class<? extends Indexable> clazz, Collection<Integer> ids) {
		if (ids == null || ids.isEmpty())
			return Collections.emptyList();
		Query query = em.createQuery("FROM " + clazz.getName() + " i WHERE i.id IN (" + Strings.implode(ids) + ")");
		return query.getResultList();
	}

	/**
	 * Returns a set of ids for all Indexable objects of type <code>clazz</code>
	 * where <code>needsIndexing</code> is true.
	 * 
	 * @param clazz
	 *            Class of records to pull up
	 * @param id
	 *            Optional id of record to pull up, if less than or equal to 0
	 *            then it is ignored
	 * @param limit
	 *            Optional limit of number of records to pull up. if less than
	 *            or equal to 0 is is ignored
	 * @return Set of ids of indexable objects
	 */
	public Set<Integer> getIndexable(Class<? extends Indexable> clazz, int id, int limit) {
		String tableName = ReflectUtil.getTableName(clazz);
		SelectSQL sql = new SelectSQL(tableName);
		sql.addField("id");
		sql.addWhere("needsIndexing = 1");
		if (id < 0 || limit < 0)
			return Collections.emptySet();
		if (id > 0)
			sql.addWhere("id = " + id);
		if (limit > 0)
			sql.setLimit(limit);
		if (tableName.equals("users"))
			sql.addWhere("isGroup = 'No'");
		if (tableName.equals("accounts"))
			sql.addWhere("status NOT IN ('Deleted', 'Deactivated')");
		sql.addOrderBy("id");
		Query query = em.createNativeQuery(sql.toString());

		return new HashSet<Integer>(query.getResultList());
	}

}
