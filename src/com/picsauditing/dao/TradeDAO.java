package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("unchecked")
public class TradeDAO extends PicsDAO {

	public Trade find(int id) {
		Trade a = em.find(Trade.class, id);
		return a;
	}

	public List<Trade> findByParent(int productID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.parent.id =  ?");
		query.setParameter(1, productID);
		return query.getResultList();
	}

	public List<Trade> findByBestMatch(int productID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.bestMatch.id =  ?");
		query.setParameter(1, productID);
		return query.getResultList();
	}

	public List<Trade> findRoot(ClassificationType type) {
		Query query = em.createQuery("SELECT p FROM Trade p "
				+ "WHERE p.parent IS NULL AND p.classificationType = ?");
		query.setParameter(1, type);
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
		
		String sql = "SELECT distinct t0.* " +
					"FROM ref_trade t0 " +
					"JOIN ref_trade t1 " +
					"ON t1.classificationType = t0.classificationType AND t0.indexStart <= t1.indexStart AND t0.indexEnd >= t1.indexEnd " +
					"JOIN ref_trade t2 " +
					"ON t1.id = t2.bestMatchID AND t2.classificationType = 'Suncor' " +
					"WHERE t0.classificationType = 'Master'  " +
					"and t0.parentID " + parentString +
					" ORDER BY t0.classificationCode, t1.classificationCode;";
		
		Query query = em.createNativeQuery(sql, com.picsauditing.jpa.entities.Trade.class);
		return query.getResultList();
	}
}
