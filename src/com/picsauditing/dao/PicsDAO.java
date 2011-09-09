package com.picsauditing.dao;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReflectUtil;
import com.picsauditing.util.Strings;

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

	public <T extends BaseTable> T find(Class<T> clazz, int id) {
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

	public List findWhere(String className, String where, int limit) {
		Query q = em.createQuery("FROM " + className + " t WHERE " + where + " ORDER BY t.id");
		if (limit > 0)
			q.setMaxResults(limit);
		return q.getResultList();
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String name, String value) {
		return findByTranslatableField(cls, "", name, value, null);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value) {
		return findByTranslatableField(cls, where, name, value, null);
	}

	@SuppressWarnings("unchecked")
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value, Locale locale) {
		String tableName = ReflectUtil.getTableName(cls);

		SelectSQL sql = new SelectSQL(tableName + " t");

		String identifier = "";
		if (name != null && name.length() > 0) {
			name = ",'." + name + "'";
			identifier = "id";
		} else {
			identifier = "isoCode";
		}

		try {
			if (cls.getDeclaredField("uniqueCode") != null)
				sql.addJoin("JOIN app_translation tr ON CONCAT('" + cls.getSimpleName()
						+ ".',IF(t.uniqueCode <> '',t.uniqueCode,t." + identifier + ")" + name + ") = tr.msgKey");
		} catch (NoSuchFieldException theFieldDoesNotExist) {
			sql.addJoin("JOIN app_translation tr ON CONCAT('" + cls.getSimpleName() + ".',t." + identifier + name
					+ ") = tr.msgKey");
		} catch (SecurityException justIgnoreIt) {
		}

		if (!Strings.isEmpty(where)) {
			sql.addWhere(where);
		}

		sql.addWhere("tr.msgValue LIKE :value");

		sql.addField("t.*");

		if (locale != null)
			sql.addWhere("(tr.locale = :locale OR (tr.locale != :locale AND tr.locale = :lang) OR ( tr.locale != :locale AND tr.locale != :lang AND tr.locale = :default))");

		Query query = em.createNativeQuery(sql.toString(), cls);
		query.setParameter("value", value);

		if (locale != null) {
			query.setParameter("locale", locale);
			query.setParameter("lang", locale.getLanguage());
			query.setParameter("default", I18nCache.DEFAULT_LANGUAGE);
		}

		return query.getResultList();
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String value) {
		return findByTranslatableField(cls, "", value, Locale.ENGLISH);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String value, Locale locale) {
		return findByTranslatableField(cls, "", value, locale);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String value,
			Locale locale) {
		return findByTranslatableField(cls, where, "", value, locale);
	}

	public int deleteData(Class<? extends BaseTable> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}
}