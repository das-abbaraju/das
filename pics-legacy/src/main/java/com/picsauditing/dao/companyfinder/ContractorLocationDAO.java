package com.picsauditing.dao.companyfinder;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Trade;

import javax.persistence.Query;
import java.util.List;

public class ContractorLocationDAO extends PicsDAO {

    public ContractorLocationDAO() {
    }

    public List<ContractorLocation> findContractorLocations(double neLat, double neLong, double swLat, double swLong) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl " +
                " JOIN cl.contractor ca " +
                " WHERE " +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND " +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        viewPortParams(neLat, neLong, swLat, swLong, query);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);

        return query.getResultList();
    }

    public List<ContractorLocation> findContractorLocations(double neLat, double neLong, double swLat, double swLong, Trade trade) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl" +
                " JOIN cl.contractor ca " +
                " JOIN ca.trades ct " +
                " WHERE " +
                getTradeWhere(trade) +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND" +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        viewPortParams(neLat, neLong, swLat, swLong, query);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);
        includeTradeParam(trade, query);

        return query.getResultList();
    }


    public List<ContractorLocation> findContractorLocations(double neLat, double neLong, double swLat, double swLong, boolean safetySensitive) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl " +
                " JOIN cl.contractor ca " +
                " WHERE " +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND " +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " ca.safetySensitive = :safetySensitive AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        viewPortParams(neLat, neLong, swLat, swLong, query);
        query.setParameter("safetySensitive", safetySensitive);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);

        return query.getResultList();
    }

    public List<ContractorLocation> findContractorLocations(double neLat, double neLong, double swLat, double swLong, Trade trade, boolean safetySensitive) {
        Query query = em.createQuery("SELECT cl FROM ContractorLocation cl" +
                " JOIN cl.contractor ca " +
                " JOIN ca.trades ct " +
                " WHERE " +
                getTradeWhere(trade) +
                " cl.latitude > :swLat AND " +
                " cl.longitude > :swLong AND" +
                " cl.latitude < :neLat AND " +
                " cl.longitude < :neLong AND " +
                " ca.safetySensitive = :safetySensitive AND " +
                " (ca.status = :active OR ca.status = :pending ) ");
        viewPortParams(neLat, neLong, swLat, swLong, query);
        query.setParameter("safetySensitive", safetySensitive);
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);
        includeTradeParam(trade, query);

        return query.getResultList();
    }

    private String getTradeWhere(Trade trade) {
        if (trade == null) {
            return " ";
        }
        return " ct.trade.indexStart <= :tradeStart  AND :tradeEnd <= ct.trade.indexEnd AND ";
    }

    private void includeTradeParam(Trade trade, Query query) {
        if (trade != null) {
            query.setParameter("tradeStart", trade.getIndexStart());
            query.setParameter("tradeEnd", trade.getIndexEnd());
        }
    }

    private void viewPortParams(double neLat, double neLong, double swLat, double swLong, Query query) {
        query.setParameter("swLat", swLat);
        query.setParameter("swLong", swLong);
        query.setParameter("neLat", neLat);
        query.setParameter("neLong", neLong);
    }

}
