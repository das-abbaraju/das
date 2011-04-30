package com.picsauditing.dao;

import java.util.List;
import javax.persistence.Query;
import com.picsauditing.jpa.entities.TradeAlternate;

@SuppressWarnings("unchecked")
public class TradeAlternateDAO extends PicsDAO {

	public TradeAlternate find(int id) {
		TradeAlternate a = em.find(TradeAlternate.class, id);
		return a;
	}

	public List<TradeAlternate> findByTrade(int tradeID) {
		Query query = em.createQuery("SELECT p FROM Trade p WHERE p.trade.id =  ?");
		query.setParameter(1, tradeID);
		return query.getResultList();
	}
}
