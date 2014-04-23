package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.FlagCriteria;
import com.picsauditing.flagcalculator.entities.Naics;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@SuppressWarnings("unchecked")
public class FlagCalculatorDAO extends PicsDAO {
    private static final String CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL1 = "SELECT fc1.id as year1_id, fc2.id as year2_id, fc3.id as year3_id " +
            "FROM flag_criteria fc1 " +
            "left outer join flag_criteria fc2 on fc1.oshaType = fc2.oshaType AND fc1.oshaRateType = fc2.oshaRateType and fc2.multiYearScope = 'TwoYearsAgo' " +
            "left outer join flag_criteria fc3 on fc1.oshaType = fc3.oshaType AND fc1.oshaRateType = fc3.oshaRateType and fc3.multiYearScope = 'ThreeYearsAgo' " +
            "WHERE (fc1.oshaType is not null and fc1.multiYearScope = 'LastYearOnly')";
    private static final String CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL2 = "SELECT fc1.id as year1_id, fc2.id as year2_id, fc3.id as year3_id " +
            "FROM flag_criteria fc1 " +
            "left outer join flag_criteria fc2 on fc1.questionID = fc2.questionID and fc2.multiYearScope = 'TwoYearsAgo' " +
            "left outer join flag_criteria fc3 on fc1.questionID = fc3.questionID and fc3.multiYearScope = 'ThreeYearsAgo' " +
            "WHERE (fc1.questionID is not null and fc1.multiYearScope = 'LastYearOnly')";

    public FlagCalculatorDAO() {}

    public FlagCalculatorDAO(EntityManager em) {
        this.em = em;
    }

//	public FlagCriteria find(int id) {
//		return em.find(FlagCriteria.class, id);
//	}
//
//	public List<FlagCriteria> findAll() {
//		Query q = em.createQuery("FROM FlagCriteria t ORDER BY t.displayOrder, t.category");
//		return q.getResultList();
//	}
//
	public List<FlagCriteria> findWhere(String where) {
		Query query = em.createQuery("From FlagCriteria WHERE " + where);
		return query.getResultList();
	}

//	/**
//	 * This method pulls back all {@link FlagCriteria} that is in use by {@link OperatorAccount}s.
//	 */
//	public HashSet<FlagCriteria> getDistinctOperatorFlagCriteria() {
//		Query query = em.createQuery("SELECT DISTINCT criteria from FlagCriteriaOperator");
//		return new HashSet(query.getResultList());
//	}

    public float getDartIndustryAverage(Naics naics) {
        naics = getBroaderNaicsForDart(naics);

        if (naics == null)
            return 0;
        return naics.getDart();
    }

    private Naics getBroaderNaicsForDart(Naics naics) {
        String code = naics.getCode();
        if (StringUtils.isEmpty(code))
            return null;
        if (naics.getDart() > 0)
            return naics;
        Naics naics2 = findParent(code);
        if (naics2 == null || naics2.getDart() > 0)
            return naics2;

        return getBroaderNaicsForDart(naics2);
    }

    private Naics findParent(String code) {
        Naics naics;
        while (code.length()>1) {
            code = code.substring(0, code.length() - 1);
            naics = em.find(Naics.class, code);
            if (naics != null) {
                return naics;
            }
        }
        return null;
    }

    public Map<Integer, List<Integer>> getCorrespondingMultiscopeCriteriaIds() {
        Query q = em.createNativeQuery(CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL1);
        List results = q.getResultList();
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        return resultMap;
    }

    /*
    private Map<Integer, List<Integer>> extractMultiyearCriteriaIdQueryResults() {
        try {
            List<BasicDynaBean> resultBDB = db.select(sql.toString(), false);
            for (BasicDynaBean row : resultBDB) {
                Integer year1 = (Integer) row.get("year1_id");
                Integer year2 = (Integer) row.get("year2_id");
                Integer year3 = (Integer) row.get("year3_id");

                ArrayList<Integer> list = new ArrayList<Integer>();
                if (year1 != null) {
                    list.add(year1);
                }

                if (year2 != null) {
                    list.add(year2);
                }

                if (year3 != null) {
                    list.add(year3);
                }

                if (year1 != null) {
                    resultMap.put(year1, list);
                }

                if (year2 != null) {
                    resultMap.put(year2, list);
                }

                if (year3 != null) {
                    resultMap.put(year3, list);
                }
            }
        } catch (Exception e) {
            logger.error("Error while extracting multi-year criteria.", e);
        }
    }
    */
}
