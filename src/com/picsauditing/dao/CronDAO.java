package com.picsauditing.dao;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAccount;

public class CronDAO extends PicsDAO {
    public Integer runCountGivenTimeMinutes(int minutes) {
        String sql = "SELECT COUNT(*) \n" +
                "FROM contractor_cron_log ccl\n" +
                "WHERE ccl.startDate > DATE_SUB(NOW(), INTERVAL " +
                minutes + " MINUTE);";
        Query q = em.createNativeQuery(sql);

        return Integer.parseInt(q.getSingleResult().toString());
    }

    public Float averageTimePerContractor() {
        String sql = "select AVG(runTime) from contractor_cron_log";
        Query q = em.createNativeQuery(sql);

        return Float.parseFloat(q.getSingleResult().toString());
    }

    public List<ContractorAccount> recentlyRunContractors(int limit) {
        String sql = "SELECT a.*, c.* \n" +
                "FROM contractor_cron_log ccl\n" +
                "JOIN contractor_info c ON c.id = ccl.conID\n" +
                "JOIN accounts a on a.id = c.id\n" +
                "WHERE ccl.success = 1\n" +
                "ORDER BY ccl.id DESC\n" +
                "LIMIT " + limit;
        Query q = em.createNativeQuery(sql, ContractorAccount.class);

        return q.getResultList();
    }

    public Map<String, Integer> contractorsPerServer() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String sql = "SELECT MID(l.server, 8, 11), COUNT(*) FROM contractor_cron_log l \n" +
                "WHERE l.startDate > DATE_SUB(NOW(), INTERVAL 1 DAY) \n" +
                "GROUP BY MID(l.server, 8, 11) \n" +
                "LIMIT 10";
        Query q = em.createNativeQuery(sql);

        for (Object result : q.getResultList()) {
            Object[] row = (Object[]) result;
            map.put((String) row[0], Integer.parseInt(row[1].toString()));
        }
        return map;
    }

    public int timeToRunAllContractors() {
        String sql = "";
        Query q = em.createNativeQuery(sql);
        return (int) q.getSingleResult();
    }
}
