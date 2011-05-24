package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.search.Database;
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
		return findHierarchyByTrade(tradeID, Integer.MAX_VALUE);
	}

	public Tree<Trade> findHierarchyByTrade(int tradeID, int depth) {
		return Tree.createTreeFromOrderedList(findListByTrade(tradeID, depth));
	}

	public List<Trade> findListByTrade(int tradeID, int depth) {
		SelectSQL sql = new SelectSQL("ref_trade t1");

		sql.addJoin("JOIN ref_trade t2 ON (t1.indexStart >= t2.indexStart AND t1.indexEnd <= t2.indexEnd) "
				+ "OR (t1.indexStart <= t2.indexStart AND t1.indexEnd >= t2.indexEnd)");
		sql.addWhere("t1.id = :tradeID");
		sql.addWhere("t2.indexLevel - t1.indexLevel <= :depth");

		sql.addGroupBy("t2.id");
		sql.addOrderBy("t2.indexStart");
		sql.addField("t2.*");

		Query query = em.createNativeQuery(sql.toString(), Trade.class);
		query.setParameter("tradeID", tradeID);
		query.setParameter("depth", depth);

		return query.getResultList();
	}

	public Tree<Trade> findHierarchyByIndexValue(String q) {
		List<String> terms = new SearchEngine(null).buildTerm(q, true, false);
		if (terms.isEmpty())
			return Tree.createTreeFromOrderedList(Collections.<Trade> emptyList());

		String searchJoins = buildSearchJoins(terms);

		SelectSQL sql = new SelectSQL("app_index i0");
		sql.addField("t2.*");
		sql.addField("IF(t2.id = t1.id, 'true', 'false') matching");
		sql.addJoin("JOIN ref_trade t1 ON t1.id = i0.foreignKey");
		sql.addJoin("JOIN ref_trade t2 ON t1.indexStart >= t2.indexStart AND t1.indexEnd <= t2.indexEnd");
		if (!searchJoins.isEmpty())
			sql.addJoin(searchJoins);
		sql.addWhere("i0.indexType = 'T' AND i0.value LIKE :0");
		sql.addGroupBy("t2.id");
		sql.addOrderBy("t2.indexStart");

		Query query = em.createNativeQuery(sql.toString(), "matchingTradeResults");

		for (int i = 0; i < terms.size(); i++) {
			query.setParameter("" + i, terms.get(i) + "%");
		}

		List<Trade> trades = new ArrayList<Trade>();
		Set<Trade> matches = new HashSet<Trade>();
		for (Object result : query.getResultList()) {
			Object[] resultArray = (Object[]) result;
			trades.add((Trade) resultArray[0]);
			if (Boolean.valueOf(resultArray[1].toString()))
				matches.add((Trade) resultArray[0]);
		}

		return Tree.createDecoratedTreeFromOrderedList(trades, matches);
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
		sql.addWhere("i0.indexType = 'T' AND i0.value LIKE :0");
		sql.addOrderBy("t1.indexLevel DESC");

		Query query = em.createNativeQuery(sql.toString(), Trade.class);

		for (int i = 0; i < terms.size(); i++) {
			query.setParameter("" + i, terms.get(i) + "%");
		}

		return query.getResultList();
	}
	
	public List<ContractorTrade> findContractorTradeByTrade(int tradeID) {
		Query query = em.createQuery("SELECT ct FROM ContractorTrade ct WHERE ct.trade.id = ?");
		query.setParameter(1, tradeID);
		return query.getResultList();
	}
	
	public int updateContractorTrades(int oldTradeID, int newTradeID) {
		Query query = em.createQuery("UPDATE ContractorTrade ct SET ct.trade.id = :newTrade WHERE ct.trade.id = :oldTrade");
		query.setParameter("oldTrade", oldTradeID);
		query.setParameter("newTrade", newTradeID);
		return query.executeUpdate();
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
			sb.append("JOIN app_index ").append(alias).append(" ON i1.indexType = 'T' AND i0.foreignKey = ").append(
					alias).append(".foreignKey AND ").append(alias).append(".value LIKE :").append(i).append(" ");
		}
		return sb.toString();
	}
}
