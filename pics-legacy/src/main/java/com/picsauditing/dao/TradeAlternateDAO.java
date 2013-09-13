package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.TradeAlternate;

@SuppressWarnings("unchecked")
public class TradeAlternateDAO extends PicsDAO {

	public TradeAlternate find(int id) {
		TradeAlternate a = em.find(TradeAlternate.class, id);
		return a;
	}

	public List<TradeAlternate> findByTrade(int tradeID) {
		Query query = em.createQuery("SELECT p FROM TradeAlternate p WHERE p.trade.id =  ?");
		query.setParameter(1, tradeID);
		return query.getResultList();
	}
	
	@Transactional(propagation = Propagation.NESTED)
	public int updateAlternates(int oldTradeID, int newTradeID) {
		Query query = em.createQuery("UPDATE TradeAlternate ta SET ta.trade.id = :newTrade WHERE ta.trade.id = :oldTrade");
		query.setParameter("oldTrade", oldTradeID);
		query.setParameter("newTrade", newTradeID);
		return query.executeUpdate();
	}
}
