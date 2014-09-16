package com.picsauditing.companyfinder.dao;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.TriStateFlag;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AccountStatus;

import javax.persistence.Query;
import java.util.List;

public class ContractorLocationDAO extends PicsDAO {

    public static final String SELECT_CLAUSE = "SELECT distinct cl FROM ContractorLocation cl" +
            " JOIN cl.contractor ca";

    public List<ContractorLocation> findContractorLocations(CompanyFinderFilter filter) {
        String sql = getSQL(filter);

        Query query = em.createQuery(sql);

        setQueryParameters(query, filter);

        return query.getResultList();
    }

    String getSQL(CompanyFinderFilter filter) {

        StringBuilder sql = new StringBuilder(SELECT_CLAUSE);
        List<Integer> tradeIds = filter.getTradeIds();
        if (tradeIds != null) {
            sql.append(" JOIN ca.trades ct");
        }

        StringBuilder whereClause = new StringBuilder(" WHERE");
        whereClause.append(getViewPortWhere());
        whereClause.append(getTradeWhere(filter.getTradeIds()));
        whereClause.append(getSafetySensitiveWhere(filter.getSafetySensitive()));
        whereClause.append(getSoleProprietorWhere(filter.getSoleProprietor()));
        whereClause.append(getActivePendingWhere());
        whereClause.append(getContractorIdsWhere(filter.getContractorIds()));

        sql.append(whereClause);
        return sql.toString();
    }

    private String getViewPortWhere() {
        return " cl.latitude > :swLat" +
                " AND cl.longitude > :swLong" +
                " AND cl.latitude < :neLat" +
                " AND cl.longitude < :neLong";
    }

    private String getTradeWhere(List<Integer> tradeIds) {
        if (tradeIds != null) {
            return " AND ct.trade.id IN :tradeList";
        }
        return "";
    }

    private String getSafetySensitiveWhere(TriStateFlag safetySensitive) {
        if (hasSafetySensitive(safetySensitive)) {
            return " AND ca.safetySensitive = :safetySensitive";
        }
        return "";
    }

    private String getSoleProprietorWhere(TriStateFlag soleProprietor) {
        if (hasSoleProprietor(soleProprietor)) {
            return " AND ca.soleProprietor = :soleProprietor";
        }
        return "";
    }

    private String getActivePendingWhere() {
        return " AND (ca.status = :active OR ca.status = :pending )";
    }

    private String getContractorIdsWhere(List<Integer> contractorIds) {
        if (contractorIds != null) {
            return " AND ca.id IN (:contractorIds)";
        }
        return "";
    }

    private void setQueryParameters(Query query, CompanyFinderFilter filter) {
        setViewPortQueryParams(query, filter);
        setTradeQueryParams(query, filter);
        setSoleProprietorQueryParams(query, filter);
        setSafetySensitiveQueryParams(query, filter);
        setActivePendingQueryParams(query);
        setContractorIdsQueryParam(query, filter);
    }

    private void setViewPortQueryParams(Query query, CompanyFinderFilter filter) {
        ViewPort viewPort = filter.getViewPort();

        query.setParameter("swLat", viewPort.getSouthWest().getLatitude());
        query.setParameter("swLong", viewPort.getSouthWest().getLongitude());
        query.setParameter("neLat", viewPort.getNorthEast().getLatitude());
        query.setParameter("neLong", viewPort.getNorthEast().getLongitude());
    }

    private void setTradeQueryParams(Query query, CompanyFinderFilter filter) {
        List<Integer> tradeIdList = filter.getTradeIds();
        if (tradeIdList == null) return;
        query.setParameter("tradeList", tradeIdList);
    }

    private void setSoleProprietorQueryParams(Query query, CompanyFinderFilter filter) {
        if (hasSoleProprietor(filter.getSoleProprietor())) {
            query.setParameter("soleProprietor", filter.getSoleProprietor().toBoolean());
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

    private void setContractorIdsQueryParam(Query query, CompanyFinderFilter filter) {
        List<Integer> contractorIdList = filter.getContractorIds();
        if (contractorIdList == null) return;
        query.setParameter("contractorIds", contractorIdList);
    }

    private boolean hasSafetySensitive(TriStateFlag safetySensitive) {
        return safetySensitive != TriStateFlag.IGNORE;
    }

    private boolean hasSoleProprietor(TriStateFlag soleProprietor) {
        return soleProprietor != TriStateFlag.IGNORE;
    }

    public ContractorLocation findById(int conId) {
        String sql = "SELECT cl FROM ContractorLocation cl WHERE cl.contractor.id = :conId";
        Query query = em.createQuery(sql);
        query.setParameter("conId", conId);
        List<ContractorLocation> contractorLocation = query.getResultList();
        if (!contractorLocation.isEmpty()) {
            return contractorLocation.get(0);
        }
        return null;
    }
}
