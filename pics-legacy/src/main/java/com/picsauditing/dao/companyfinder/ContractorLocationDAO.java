package com.picsauditing.dao.companyfinder;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Trade;

import javax.persistence.Query;
import java.util.List;

public class ContractorLocationDAO extends PicsDAO{

    public List<ContractorLocation> findByViewPort(double neLat, double neLong, double swLat, double swLong ) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl " +
                " JOIN cl.contractor ca " +
                " WHERE " +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND " +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        query.setParameter("swLat", swLat);
        query.setParameter("swLong", swLong);
        query.setParameter("neLat", neLat);
        query.setParameter("neLong", neLong);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);

        return query.getResultList();
    }

    public List<ContractorLocation> findByViewPortAndTrade(Trade trade, double neLat, double neLong, double swLat, double swLong) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl" +
                " JOIN cl.contractor ca "+
                " JOIN ca.trades ct " +
                " WHERE " +
                " ct.trade.indexStart <= :tradeStart  AND " +
                " :tradeEnd <= ct.trade.indexEnd AND " +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND" +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        query.setParameter("swLat", swLat);
        query.setParameter("swLong", swLong);
        query.setParameter("neLat", neLat);
        query.setParameter("neLong", neLong);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);
        query.setParameter("tradeStart", trade.getIndexStart());
        query.setParameter("tradeEnd", trade.getIndexEnd());

        return query.getResultList();
    }

}
