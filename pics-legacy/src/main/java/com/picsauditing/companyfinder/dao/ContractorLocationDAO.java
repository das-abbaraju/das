package com.picsauditing.companyfinder.dao;

import com.picsauditing.companyfinder.model.*;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Query;
import java.util.List;

public class ContractorLocationDAO extends PicsDAO {

    public static final String SELECT_CLAUSE = "SELECT distinct cl FROM ContractorLocation cl" +
            " JOIN cl.contractor ca";
    public static final String SELECT_CLAUSE_NATIVE =
            "SELECT distinct cl.* FROM contractor_location cl" +
                    " JOIN contractor_info ca" +
                    " ON cl.conId = ca.id" +
                    " JOIN accounts a" +
                    " ON cl.conId = a.id";

    public List<ContractorLocation> findContractorLocations(CompanyFinderFilter filter) {
        String sql = getSQL(filter);

        Query query = em.createQuery(sql);

        setQueryParameters(query, filter);

        return query.getResultList();
    }

    public List<ContractorLocationSummary> findContractorLocationsSummary(CompanyFinderFilter filter) {
        String sql = getSQLNativeSummary(filter);

        Query query = em.createNativeQuery(sql, ContractorLocationSummary.class);

        setQueryParametersSummaryNative(query, filter);

        return query.getResultList();
    }

    String getSQL(CompanyFinderFilter filter) {

        StringBuilder sql = new StringBuilder(SELECT_CLAUSE);
        List<Integer> tradeIds = filter.getTradeIds();
        if (!CollectionUtils.isEmpty(tradeIds)) {
            sql.append(" JOIN ca.trades ct");
        }

        StringBuilder whereClause = new StringBuilder(" WHERE");
        whereClause.append(getActivePendingWhere());
        whereClause.append(getViewPortWhere(filter.getViewPort()));
        whereClause.append(getTradeWhere(filter.getTradeIds()));
        whereClause.append(getSafetySensitiveWhere(filter.getSafetySensitive()));
        whereClause.append(getSoleProprietorWhere(filter.getSoleProprietor()));
        whereClause.append(getContractorIdsWhere(filter.getContractorIds()));

        sql.append(whereClause);
        return sql.toString();
    }

    String getSQLNativeSummary(CompanyFinderFilter filter) {

        StringBuilder sql = new StringBuilder(SELECT_CLAUSE_NATIVE);
        List<Integer> tradeIds = filter.getTradeIds();
        if (!CollectionUtils.isEmpty(tradeIds)) {
            sql.append(" JOIN contractor_trade ct ON a.id = ct.conId");
        }

        StringBuilder whereClause = new StringBuilder(" WHERE");
        whereClause.append(getActivePendingWhereSummaryNative());
        whereClause.append(getViewPortWhere(filter.getViewPort()));
        whereClause.append(getTradeWhereSummaryNative(filter.getTradeIds()));
        whereClause.append(getSafetySensitiveWhere(filter.getSafetySensitive()));
        whereClause.append(getSoleProprietorWhere(filter.getSoleProprietor()));
        whereClause.append(getContractorIdsWhereSummaryNative(filter.getContractorIds()));

        sql.append(whereClause);
        return sql.toString();
    }

    private String getViewPortWhere(ViewPort viewPort) {
        if (!isViewPortEmpty(viewPort)) {
            return  " AND cl.latitude > :swLat" +
                    " AND cl.longitude > :swLong" +
                    " AND cl.latitude < :neLat" +
                    " AND cl.longitude < :neLong";
        }
        return "";
    }

    private boolean isViewPortEmpty(ViewPort viewPort) {
        return viewPort == null || viewPort.isEmpty();
    }

    private String getTradeWhere(List<Integer> tradeIds) {
        if (!CollectionUtils.isEmpty(tradeIds)) {
            return " AND ct.trade.id IN :tradeList";
        }
        return "";
    }

    private String getTradeWhereSummaryNative(List<Integer> tradeIds) {
        if (!CollectionUtils.isEmpty(tradeIds)) {
            return " AND ct.id IN (:tradeList)";
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
        return " (ca.status = :active OR ca.status = :pending )";
    }


    private String getActivePendingWhereSummaryNative() {
        return " (a.status = :active OR a.status = :pending )";
    }

    private String getContractorIdsWhere(List<Integer> contractorIds) {
        if (!CollectionUtils.isEmpty(contractorIds)) {
            return " AND ca.id IN :contractorIds";
        }
        return "";
    }

    private String getContractorIdsWhereSummaryNative(List<Integer> contractorIds) {
        if (!CollectionUtils.isEmpty(contractorIds)) {
            return " AND ca.id IN (:contractorIds)";
        }
        return "";
    }

    private void setQueryParameters(Query query, CompanyFinderFilter filter) {
        setViewPortQueryParams(query, filter);
        setTradeQueryParam(query, filter);
        setSoleProprietorQueryParams(query, filter);
        setSafetySensitiveQueryParam(query, filter);
        setActivePendingQueryParams(query);
        setContractorIdsQueryParam(query, filter);
    }

    private void setQueryParametersSummaryNative(Query query, CompanyFinderFilter filter) {
        setViewPortQueryParams(query, filter);
        setTradeQueryParamSummaryNative(query, filter);
        setSoleProprietorQueryParamsSummaryNative(query, filter);
        setSafetySensitiveQueryParamSummaryNative(query, filter);
        setActivePendingQueryParamsSummaryNative(query);
        setContractorIdsQueryParamSummaryNative(query, filter);
    }

    private void setViewPortQueryParams(Query query, CompanyFinderFilter filter) {
        ViewPort viewPort = filter.getViewPort();

        if(isViewPortEmpty(viewPort)){
            return;
        }

        query.setParameter("swLat", viewPort.getSouthWest().getLatitude());
        query.setParameter("swLong", viewPort.getSouthWest().getLongitude());
        query.setParameter("neLat", viewPort.getNorthEast().getLatitude());
        query.setParameter("neLong", viewPort.getNorthEast().getLongitude());
    }

    private void setTradeQueryParam(Query query, CompanyFinderFilter filter) {
        List<Integer> tradeIdList = filter.getTradeIds();

        if (CollectionUtils.isEmpty(tradeIdList)) return;

        query.setParameter("tradeList", tradeIdList);
    }

    private void setTradeQueryParamSummaryNative(Query query, CompanyFinderFilter filter) {
        List<Integer> tradeIdList = filter.getTradeIds();
        if (CollectionUtils.isEmpty(tradeIdList)) return;
        String inClauseIds = Strings.implode(tradeIdList);

        query.setParameter("tradeList", inClauseIds);
    }

    private void setSoleProprietorQueryParams(Query query, CompanyFinderFilter filter) {
        if (hasSoleProprietor(filter.getSoleProprietor())) {
            query.setParameter("soleProprietor", filter.getSoleProprietor().toBoolean());
        }
    }

    private void setSoleProprietorQueryParamsSummaryNative(Query query, CompanyFinderFilter filter) {
        if (hasSoleProprietor(filter.getSoleProprietor())) {
            query.setParameter("soleProprietor", filter.getSoleProprietor().getValue());
        }
    }

    private void setSafetySensitiveQueryParam(Query query, CompanyFinderFilter filter) {
        if (hasSafetySensitive(filter.getSafetySensitive())) {
            query.setParameter("safetySensitive", filter.getSafetySensitive().toBoolean());
        }
    }

    private void setSafetySensitiveQueryParamSummaryNative(Query query, CompanyFinderFilter filter) {
        if (hasSafetySensitive(filter.getSafetySensitive())) {
            query.setParameter("safetySensitive", filter.getSafetySensitive().getValue());
        }
    }

    private void setActivePendingQueryParams(Query query) {
        query.setParameter("active", AccountStatus.Active);
        query.setParameter("pending", AccountStatus.Pending);
    }

    private void setActivePendingQueryParamsSummaryNative(Query query) {
        query.setParameter("active", AccountStatus.Active.name());
        query.setParameter("pending", AccountStatus.Pending.name());
    }

    private void setContractorIdsQueryParam(Query query, CompanyFinderFilter filter) {
        List<Integer> contractorIdList = filter.getContractorIds();
        if (CollectionUtils.isEmpty(contractorIdList)) return;
        query.setParameter("contractorIds", contractorIdList);
    }

    private void setContractorIdsQueryParamSummaryNative(Query query, CompanyFinderFilter filter) {
        List<Integer> contractorIdList = filter.getContractorIds();
        if (CollectionUtils.isEmpty(contractorIdList)) return;
        String inClauseIds = Strings.implode(contractorIdList);

        query.setParameter("contractorIds", inClauseIds);
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
