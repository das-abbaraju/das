package com.picsauditing.dao;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReflectUtil;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
abstract public class PicsDAO {
	
	protected static final int NO_LIMIT = 0; 
	
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

	@Transactional(propagation = Propagation.NESTED)
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

	@Transactional(propagation = Propagation.NESTED)
	public void remove(BaseTable row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public <T extends BaseTable> T find(Class<T> clazz, int id) {
		return em.find(clazz, id);
	}

	public <T extends BaseTable> List<T> findAll(Class<T> clazz) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t ORDER BY t.id");
		return q.getResultList();
	}

	public <T extends BaseTable> T findOne(Class<T> c, String where) {
		Query q = em.createQuery("FROM " + c.getName() + " t WHERE " + where);
		return (T) q.getSingleResult();
	}

	public <T extends BaseTable> List<T> findWhere(Class<T> clazz, String where) {
		return findWhere(clazz, where, 0);
	}

	public <T extends BaseTable> List<T> findWhere(Class<T> clazz, String where, int limit) {
		return findWhere(clazz, where, limit, " t.id");
	}

	public <T extends BaseTable> List<T> findWhere(Class<T> clazz, String where, int limit, String orderBy) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE " + where + " ORDER BY " + orderBy);
		if (limit > 0)
			q.setMaxResults(limit);
		return q.getResultList();
	}

	public <T extends BaseTable> int getCount(Class<T> clazz, String where) {
		Query q = em.createQuery("SELECT COUNT(*) FROM " + clazz.getName() + " WHERE " + where);
		int result = 0;
		try {
			result = (Integer) q.getSingleResult();
		} catch (Exception e) {
			result = 0;
		}

		return result;
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String name, String value) {
		return findByTranslatableField(cls, "", name, value, null);
	}
	
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String name, String value, int limit) {
		return findByTranslatableField(cls, "", name, value, null, limit);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value) {
		return findByTranslatableField(cls, where, name, value, null);
	}
	
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value, int limit) {
		return findByTranslatableField(cls, where, name, value, null, limit);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value, Locale locale) {
		return findByTranslatableField(cls, where, name, value, locale, NO_LIMIT);
	}
	
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String name,
			String value, Locale locale, int limit) {
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
			sql
					.addWhere("(tr.locale = :locale OR (tr.locale != :locale AND tr.locale = :lang) OR ( tr.locale != :locale AND tr.locale != :lang AND tr.locale = :default))");

		Query query = em.createNativeQuery(sql.toString(), cls);
		query.setParameter("value", value);

		if (locale != null) {
			query.setParameter("locale", locale);
			query.setParameter("lang", locale.getLanguage());
			query.setParameter("default", I18nCache.DEFAULT_LANGUAGE);
		}
		
		if (limit > NO_LIMIT) {
			query.setMaxResults(limit);
		}

		return query.getResultList();
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String value) {
		return findByTranslatableField(cls, "", value, Locale.ENGLISH);
	}
	
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String value, int limit) {
		return findByTranslatableField(cls, "", value, Locale.ENGLISH, limit);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String value, Locale locale) {
		return findByTranslatableField(cls, "", value, locale);
	}

	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String value,
			Locale locale) {
		return findByTranslatableField(cls, where, "", value, locale);
	}
	
	public <T extends Translatable> List<T> findByTranslatableField(Class<T> cls, String where, String value,
			Locale locale, int limit) {
		return findByTranslatableField(cls, where, "", value, locale, limit);
	}

	@Transactional(propagation = Propagation.NESTED)
	public int deleteData(Class<? extends BaseTable> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}
	
	protected static Query setLimit(Query query, int limit) {
		if (limit > NO_LIMIT) {
			return query.setMaxResults(limit);
		}
		
		return query;
	}
}