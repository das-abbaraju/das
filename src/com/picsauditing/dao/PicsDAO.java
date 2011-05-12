package com.picsauditing.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReflectUtil;

@Transactional
abstract public class PicsDAO {
	protected EntityManager em;
	protected QueryMetaData queryMetaData = null;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public QueryMetaData getQueryMetaData() {
		return queryMetaData;
	}

	public void setQueryMetaData(QueryMetaData queryMetaData) {
		this.queryMetaData = queryMetaData;
	}

	protected void applyQueryMetaData(Query query) {
		QueryMetaData qmd = getQueryMetaData();
		if (qmd != null) {
			if (qmd.getMaxRows() != -1) {
				query.setMaxResults(qmd.getMaxRows());
			}

			if (qmd.getStartRow() != -1) {
				query.setFirstResult(qmd.getStartRow());
			}
		}
	}

	protected static void setOptionalParameter(Query query, String name, Object value) {
		try {
			query.setParameter(name, value);
		} catch (IllegalArgumentException e) {
		}
	}

	public void clear() {
		em.clear();
	}

	public boolean isContained(Object o) {
		return em.contains(o);
	}

	public BaseTable save(BaseTable o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void refresh(BaseTable o) {
		if (em.contains(o)) {
			em.refresh(o);
		}
	}

	public void remove(BaseTable row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public BaseTable find(Class<? extends BaseTable> clazz, int id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	protected List<? extends BaseTable> findAll(Class<? extends BaseTable> clazz) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t ORDER BY t.id");
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<? extends BaseTable> findWhere(Class<? extends BaseTable> clazz, String where, int limit) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE " + where + " ORDER BY t.id");
		if (limit > 0)
			q.setMaxResults(limit);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<? extends BaseTable> findWhere(Class<? extends BaseTable> clazz, String where, int limit, String orderBy) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE " + where + " ORDER BY " + orderBy);
		if (limit > 0)
			q.setMaxResults(limit);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List findWhere(String className, String where, int limit) {
		Query q = em.createQuery("FROM " + className + " t WHERE " + where + " ORDER BY t.id");
		if (limit > 0)
			q.setMaxResults(limit);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String name, String value) {

		String tableName = ReflectUtil.getTableName(cls);

		SelectSQL sql = new SelectSQL(tableName + " t");
		sql.addJoin("JOIN app_translation tr ON CONCAT('" + cls.getSimpleName() + ".',t.id,'." + name
				+ "') = tr.msgKey");
		sql.addWhere("tr.msgValue LIKE :value");

		sql.addField("t.*");

		Query query = em.createNativeQuery(sql.toString(), cls);
		query.setParameter("value", value);

		return query.getResultList();
	}

	public int deleteData(Class<? extends BaseTable> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}
}
