package com.picsauditing.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Tree;

@SuppressWarnings("unchecked")
public class TradeDAO extends PicsDAO {

	public Trade find(int id) {
		Trade a = em.find(Trade.class, id);
		return a;
	}

	public List<Trade> findByParent(int productID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.parent.id = ?");
		query.setParameter(1, productID);
		return query.getResultList();
	}

	public List<Trade> findWhere(String where) {
		if (where == null)
			where = "";
		else
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT p FROM Trade p" + where);
		query.setMaxResults(100);
		return query.getResultList();
	}

	public Tree<Trade> findHierarchyByTrade(int tradeID) {
		SelectSQL sql = new SelectSQL("ref_trade t1");

		sql.addJoin("JOIN ref_trade t2 ON (t1.indexStart >= t2.indexStart AND t1.indexEnd <= t2.indexEnd) "
				+ "OR (t1.indexStart <= t2.indexStart AND t1.indexEnd >= t2.indexEnd)");
		sql.addWhere("t1.id = :tradeID");
		sql.addGroupBy("t2.id");
		sql.addOrderBy("t2.indexStart");
		sql.addField("t2.*");

		Query query = em.createNativeQuery(sql.toString(), Trade.class);
		query.setParameter("tradeID", tradeID);

		return Tree.createTreeFromOrderedList(query.getResultList());
	}

	public Tree<Trade> findHierarchyByIndexValue(String q) {
		List<String> terms = new SearchEngine(null).buildTerm(q, true, false);
		if (terms.isEmpty())
			return Tree.createTreeFromOrderedList(Collections.<Trade> emptyList());

		String searchJoins = buildSearchJoins(terms);

		SelectSQL sql = new SelectSQL("app_index i1");
		sql.addField("t2.*");
		sql.addJoin("JOIN ref_trade t1 ON t1.id = i1.foreignKey");
		sql.addJoin("JOIN ref_trade t2 ON t1.indexStart >= t2.indexStart AND t1.indexEnd <= t2.indexEnd");
		if (!searchJoins.isEmpty())
			sql.addJoin(searchJoins);
		sql.addWhere("i1.indexType = 'T' AND i1.value LIKE :0");
		sql.addGroupBy("t2.id");
		sql.addOrderBy("t2.indexStart");

		Query query = em.createNativeQuery(sql.toString(), Trade.class);

		for (int i = 0; i < terms.size(); i++) {
			query.setParameter("" + i, terms.get(i) + "%");
		}

		return Tree.createTreeFromOrderedList(query.getResultList());
	}

	public List<Trade> findByIndexValue(String q) {
		List<String> terms = new SearchEngine(null).buildTerm(q, true, false);
		if (terms.isEmpty())
			return Collections.<Trade> emptyList();

		String searchJoins = buildSearchJoins(terms);

		SelectSQL sql = new SelectSQL("ref_trade t1");
		sql.addField("t1.*");
		sql.addJoin("JOIN app_index i0 ON t1.id = i0.foreignKey");
		if (!searchJoins.isEmpty())
			sql.addJoin(searchJoins);
		sql.addWhere("i1.indexType = 'T' AND i0.value LIKE :0");
		sql.addOrderBy("t1.indexLevel DESC");

		Query query = em.createNativeQuery(sql.toString(), Trade.class);

		for (int i = 0; i < terms.size(); i++) {
			query.setParameter("" + i, terms.get(i));
		}

		return query.getResultList();
	}

	/**
	 * Builds a String containing all the Joins for the app_index search
	 * 
	 * @param terms
	 * @return JOIN String to use in a query
	 */
	private String buildSearchJoins(List<String> terms) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < terms.size(); i++) {
			String alias = "i" + i;
			sb.append("JOIN app_index ").append(alias).append(" ON i1.indexType = 'T' AND i1.foreignKey = ")
					.append(alias).append(".foreignKey AND ").append(alias).append(".value LIKE :").append(i);
		}
		return sb.toString();
	}
}
