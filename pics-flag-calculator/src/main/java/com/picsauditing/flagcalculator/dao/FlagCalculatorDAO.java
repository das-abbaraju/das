package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.FlagCriteria;
import com.picsauditing.flagcalculator.entities.FlagData;
import com.picsauditing.flagcalculator.entities.Naics;
import com.picsauditing.flagcalculator.service.FlagService;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@SuppressWarnings("unchecked")
public class FlagCalculatorDAO extends PicsDAO {
    public static final String CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL1 = "SELECT fc1.id as year1_id, fc2.id as year2_id, fc3.id as year3_id " +
            "FROM flag_criteria fc1 " +
            "left outer join flag_criteria fc2 on fc1.oshaType = fc2.oshaType AND fc1.oshaRateType = fc2.oshaRateType and fc2.multiYearScope = 'TwoYearsAgo' " +
            "left outer join flag_criteria fc3 on fc1.oshaType = fc3.oshaType AND fc1.oshaRateType = fc3.oshaRateType and fc3.multiYearScope = 'ThreeYearsAgo' " +
            "WHERE (fc1.oshaType is not null and fc1.multiYearScope = 'LastYearOnly')";
    public static final String CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL2 = "SELECT fc1.id as year1_id, fc2.id as year2_id, fc3.id as year3_id " +
            "FROM flag_criteria fc1 " +
            "left outer join flag_criteria fc2 on fc1.questionID = fc2.questionID and fc2.multiYearScope = 'TwoYearsAgo' " +
            "left outer join flag_criteria fc3 on fc1.questionID = fc3.questionID and fc3.multiYearScope = 'ThreeYearsAgo' " +
            "WHERE (fc1.questionID is not null and fc1.multiYearScope = 'LastYearOnly')";

    public FlagCalculatorDAO() {}

//    public FlagCalculatorDAO(EntityManager em) {
//        this.em = em;
//    }

	public List<FlagCriteria> findWhere(String where) {
		Query query = em.createQuery("From FlagCriteria WHERE " + where);
		return query.getResultList();
	}

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
        Map<Integer, List<Integer>> resultMap = extractMultiyearCriteriaIdQueryResults(q.getResultList());

        q = em.createNativeQuery(CORRESPONDING_MULTISCOPE_CRITERIA_IDS_SQL2);
        resultMap.putAll(extractMultiyearCriteriaIdQueryResults(q.getResultList()));
        return resultMap;
    }

    private Map<Integer, List<Integer>> extractMultiyearCriteriaIdQueryResults(List<Object[]> results) {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        try {
            for (Object[] row : results) {
                Integer year1 = (Integer) row[0];
                Integer year2 = (Integer) row[1];
                Integer year3 = (Integer) row[2];

                ArrayList<Integer> list = new ArrayList<>();
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
        }

        return resultMap;
    }

    public Collection<FlagData> insertUpdateDeleteManaged(Collection<FlagData> dbLinkedList,
                                                                        Collection<FlagData> changes) {
        // update/delete
        Iterator<FlagData> dbIterator = dbLinkedList.iterator();
        Collection<FlagData> removalList = new ArrayList<>();

        while (dbIterator.hasNext()) {
            FlagData fromDB = dbIterator.next();
            FlagData found = null;

            for (FlagData change : changes) {
                if (fromDB.equals(change)) {
                    FlagService.updateFlagData(fromDB, change);
                    found = change;
                }
            }

            if (found != null) {
                changes.remove(found); // update was performed
            } else {
                removalList.add(fromDB);
            }
        }

        // merging remaining changes (updates/inserts)
        dbLinkedList.addAll(changes);

        return removalList;
    }

}
