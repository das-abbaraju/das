package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
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

	public List<Trade> findByParent(int parentID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.parent.id = ?");
		query.setParameter(1, parentID);
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

		StringBuilder joins = new StringBuilder().append(buildSearchJoins(terms, true));
		StringBuilder wrapper = new StringBuilder().append("SELECT rt2.*, IF(rt2.id = t").append(terms.size())
				.append(".id, 'true', 'false') matching FROM (").append(joins.toString()).append(") t")
				.append(terms.size()).append(" JOIN ref_trade rt2 ON t").append(terms.size())
				.append(".indexStart >= rt2.indexStart AND t").append(terms.size()).append(".indexEnd <= rt2.indexEnd")
				.append(" GROUP BY rt2.id").append(" ORDER BY rt2.indexStart");

		// core query
		SelectSQL sql = new SelectSQL("app_index i0");
		sql.addField("t0.*");
		sql.addJoin("JOIN ref_trade t0 ON t0.id = i0.foreignKey AND i0.indexType = 'T' AND i0.value LIKE :0");

		// wrap core query with joins
		String sqlString = wrapper.toString().replace("***APPEND***", sql.toString());
		Query query = em.createNativeQuery(sqlString, "matchingTradeResults");

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

	public List<Trade> findByIndexValue(String q, Integer limit) {
		List<String> terms = new SearchEngine(null).buildTerm(q, true, false);
		if (terms.isEmpty())
			return Collections.<Trade> emptyList();

		StringBuilder wrapper = new StringBuilder().append(buildSearchJoins(terms, false));

		// core query
		SelectSQL sql = new SelectSQL("ref_trade t0");
		sql.addField("i0.weight as i0weight, t0.*");
		sql.addJoin("JOIN app_index i0 ON i0.indexType = 'T' and t0.id = i0.foreignKey AND i0.value LIKE :0");

		String orderBy = "i0weight";
		String groupBy = "";
		for (int i = 1; i < terms.size(); i++) {
			groupBy = "t" + i + ".id";
			orderBy += " + i" + i + "weight";
		}
		if (groupBy.isEmpty())
			groupBy = "t0.id";

		wrapper.append(" GROUP BY ").append(groupBy);
		wrapper.append(" ORDER BY ").append(orderBy).append(" DESC").append(", contractorCount DESC");

		// Merge core query into wrapper
		String sqlString = wrapper.toString().replace("***APPEND***", sql.toString());

		Query query = em.createNativeQuery(sqlString, Trade.class);

		for (int i = 0; i < terms.size(); i++) {
			query.setParameter("" + i, terms.get(i) + "%");
		}

		if (limit != null && limit > 0)
			query.setMaxResults(limit);

		return query.getResultList();
	}

	public List<ContractorTrade> findContractorTradeByTrade(int tradeID) {
		Query query = em.createQuery("SELECT ct FROM ContractorTrade ct WHERE ct.trade.id = ?");
		query.setParameter(1, tradeID);
		return query.getResultList();
	}
	
	public List<ContractorAccount> findContractorsByTrade(int tradeID) {
		Query query = em.createQuery("SELECT ");
		query.setParameter(1, tradeID);
		return query.getResultList();
		
	}

	@Transactional(propagation = Propagation.NESTED)
	public int updateContractorTrades(int oldTradeID, int newTradeID) {
		Query query = em
				.createQuery("UPDATE ContractorTrade ct SET ct.trade.id = :newTrade WHERE ct.trade.id = :oldTrade");
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
	private String buildSearchJoins(List<String> terms, boolean hierarchical) {
		StringBuilder sbSelects = new StringBuilder();
		StringBuilder sbJoins = new StringBuilder();
		for (int i = 1; i < terms.size(); i++) {

			String tableAlias = "t" + i;
			String indexAlias = "i" + i;
			sbJoins.append(") t").append(i).append(" JOIN app_index ").append(indexAlias).append(" ON ")
					.append(indexAlias).append(".indexType = 'T' AND ").append(tableAlias).append(".id = ")
					.append(indexAlias).append(".foreignKey AND ").append(indexAlias).append(".value LIKE :").append(i);
			if (i != terms.size() || !hierarchical) {
				String startString = sbSelects.toString();
				sbSelects = new StringBuilder().append("SELECT ").append(tableAlias).append(".*");
				if (!hierarchical) {
					sbSelects.append(", ").append(indexAlias).append(".weight AS ").append(indexAlias)
							.append("weight ");
				}
				sbSelects.append(" FROM (").append(startString);
			}
		}

		return sbSelects.toString() + " ***APPEND*** " + sbJoins.toString();
	}
}