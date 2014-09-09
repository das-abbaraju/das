package com.picsauditing.companyfinder.dao;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.SafetySensitive;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Trade;

import javax.persistence.Query;
import java.util.List;

public class ContractorLocationDAO extends PicsDAO {

    public static String SELECT_CLAUSE = "SELECT distinct cl FROM ContractorLocation cl" +
            " JOIN cl.contractor ca";

    public List<ContractorLocation> findContractorLocations(CompanyFinderFilter filter) {
        String sql = getSQL(filter);

        Query query = em.createQuery(sql);

        setQueryParameters(query, filter);

        return query.getResultList();
    }

     String getSQL(CompanyFinderFilter filter) {

        StringBuilder sql = new StringBuilder();
        Trade trade = filter.getTrade();
        if(trade != null){
            SELECT_CLAUSE +=  " JOIN ca.trades ct";
        }
        sql.append(SELECT_CLAUSE);

        StringBuilder whereClause = new StringBuilder(" WHERE");
        whereClause.append(getViewPortWhere());
        whereClause.append(getTradeWhere(filter.getTrade()));
        whereClause.append(getSafetySensitiveWhere(filter.getSafetySensitive()));
        whereClause.append(getActivePendingWhere());

        sql.append(whereClause);
        return sql.toString();
    }

    private String getViewPortWhere() {
        return " cl.latitude > :swLat AND" +
                " cl.longitude > :swLong AND" +
                " cl.latitude < :neLat AND" +
                " cl.longitude < :neLong AND";
    }

    private String getTradeWhere(Trade trade) {
        if (trade == null) {
            return "";
        }
        return " ct.trade.indexStart <= :tradeStart AND :tradeEnd <= ct.trade.indexEnd AND";
    }

    private String getSafetySensitiveWhere(SafetySensitive safetySensitive) {
        if (hasSafetySensitive(safetySensitive)) {
            return " ca.safetySensitive = :safetySensitive AND";
        }
        return "";
    }

    private String getActivePendingWhere() {
        return " (ca.status = :active OR ca.status = :pending )";
    }

    private void setQueryParameters(Query query, CompanyFinderFilter filter) {
        setViewPortQueryParams(query, filter);
        setTradeQueryParams(query, filter);
        setSafetySensitiveQueryParams(query, filter);
        setActivePendingQueryParams(query);
    }

    private void setViewPortQueryParams(Query query, CompanyFinderFilter filter) {
        ViewPort viewPort = filter.getViewPort();

        query.setParameter("swLat", viewPort.getSouthWest().getLatitude());
        query.setParameter("swLong", viewPort.getSouthWest().getLongitude());
        query.setParameter("neLat", viewPort.getNorthEast().getLatitude());
        query.setParameter("neLong", viewPort.getNorthEast().getLongitude());
    }

    private void setTradeQueryParams(Query query, CompanyFinderFilter filter) {
        if (filter.getTrade() != null) {
            query.setParameter("tradeStart", filter.getTrade().getIndexStart());
            query.setParameter("tradeEnd", filter.getTrade().getIndexEnd());
        }
    }

    private void setSafetySensitiveQueryParams(Query query, CompanyFinderFilter filter) {
        if (hasSafetySensitive(filter.getSafetySensitive())) {
            query.setParameter("safetySensitive", filter.getSafetySensitive().toBoolean());
        }
    }

    private void setActivePendingQueryParams(Query query) {
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);
    }

    private boolean hasSafetySensitive(SafetySensitive safetySensitive) {
        return safetySensitive != SafetySensitive.IGNORE;
    }

    public ContractorLocation findById(int conId) {
        String sql = "SELECT cl FROM ContractorLocation cl WHERE cl.contractor.id = :conId";
        Query query = em.createQuery(sql);
        query.setParameter("conId", conId);
        List<ContractorLocation> contractorLocation = query.getResultList();
        if (!contractorLocation.isEmpty()){
            return contractorLocation.get(0);
        }
        return null;
    }
}
