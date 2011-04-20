package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Node;
import com.picsauditing.util.Tree;

@SuppressWarnings("unchecked")
public class TradeDAO extends IndexableDAO {

	public Trade find(int id) {
		Trade a = em.find(Trade.class, id);
		return a;
	}

	public List<Trade> findByParent(int productID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.parent.id =  ?");
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

	public List<Trade> findByNode(Trade trade) {
		String parentString = "";
		if (trade == null)
			parentString = "is null";
		else
			parentString = "= " + trade.getId();

		String sql = "SELECT distinct t0.* "
				+ "FROM ref_trade t0 "
				+ "JOIN ref_trade t1 "
				+ "ON t1.classificationType = t0.classificationType AND t0.indexStart <= t1.indexStart AND t0.indexEnd >= t1.indexEnd "
				+ "JOIN ref_trade t2 " + "ON t1.id = t2.bestMatchID AND t2.classificationType = 'Suncor' "
				+ "WHERE t0.classificationType = 'Master'  " + "and t0.parentID " + parentString
				+ " ORDER BY t0.classificationCode, t1.classificationCode;";

		Query query = em.createNativeQuery(sql, com.picsauditing.jpa.entities.Trade.class);
		return query.getResultList();
	}

	public Tree<Trade> findByIndexValue(String q) {
		Map<Trade, Node<Trade>> nodes = new HashMap<Trade, Node<Trade>>();
		Tree<Trade> tree = new Tree<Trade>();
		Node<Trade> root = new Node<Trade>();
		tree.setRoot(root);
		nodes.put(null, root);
		
		String sql = "SELECT t2.* " + 
					"FROM app_index i " + 
					"JOIN ref_trade t1 ON t1.id = i.foreignKey " + 
					"JOIN ref_trade t2 ON t1.indexStart >= t2.indexStart AND t1.indexEnd <= t2.indexEnd " + 
					"WHERE i.indexType = 'T' AND i.value LIKE :q " + 
					"GROUP BY t2.id " +
					"ORDER by t2.indexStart";
		
		Query query = em.createNativeQuery(sql, Trade.class);
		query.setParameter("q", q + "%");

		List<Trade> trades = query.getResultList();

		for (Trade trade : trades) {
			Node<Trade> node = new Node<Trade>(trade);

			if (nodes.get(trade.getParent()) != null && !nodes.get(trade.getParent()).getChildren().contains(node)) {
				nodes.get(trade.getParent()).addChild(node);
				nodes.put(trade, node);
			}
		}

		return tree;
	}
}
