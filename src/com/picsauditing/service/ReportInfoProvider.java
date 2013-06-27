package com.picsauditing.service;

import java.util.Collections;
import java.util.List;

import com.picsauditing.dao.ReportDAO;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.dao.mapper.ReportInfoMapper;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Paginatable;
import com.picsauditing.util.pagination.PaginationParameters;

public class ReportInfoProvider implements Paginatable<ReportInfo> {

    @Autowired
    private ReportUserDAO reportUserDAO;
    @Autowired
    private ReportDAO reportDAO;

    private static final Logger logger = LoggerFactory.getLogger(ReportInfoProvider.class);

    public List<ReportInfo> findTenMostFavoritedReports(Permissions permissions, int size) {
        return reportUserDAO.findTenMostFavoritedReports(permissions, size);
    }

    public List<ReportInfo> findReportSuggestions(Permissions permissions) {
        return reportDAO.findReportSuggestions(permissions);
    }

    @Override
    public List<ReportInfo> getPaginationResults(PaginationParameters parameters) {
        try {
            SelectSQL sql = buildQueryForSearch(parameters);
            sql.setPageNumber(parameters.getPageSize(), parameters.getPage());
            return new Database().select(sql.toString(), new ReportInfoMapper());
        } catch (Exception e) {
            logger.error("Unexpected exception in getPaginationResults()", e);
        }

        return Collections.emptyList();
    }

    private SelectSQL buildQueryForSearch(PaginationParameters parameters) {
        ReportPaginationParameters reportParams = (ReportPaginationParameters) parameters;

        // TODO escape properly
        String query = "\'%" + Strings.escapeQuotes(reportParams.getQuery()) + "%\'";

        SelectSQL selectSQL = ReportUserDAO.setupSqlForSearchFilterQuery(reportParams.getPermissions());
        selectSQL.addWhere("r.name LIKE " + query + " OR r.description LIKE " + query + " OR r.modelType LIKE " + query + " OR u.name LIKE " + query);

        return selectSQL;
    }

    @Override
    public int getPaginationOverallCount(PaginationParameters parameters) {
        try {
            SelectSQL sql = buildQueryForSearch(parameters);
            sql.addField("count(r.id) AS count");

            Database db = new Database();
            List<BasicDynaBean> results = db.select(sql.toString(), false);

            return Database.toInt(results.get(0), "count");
        } catch (Exception e) {
            logger.error("Unexpected exception in getPaginationOverallCount()", e);
        }

        return -1;
    }

}
