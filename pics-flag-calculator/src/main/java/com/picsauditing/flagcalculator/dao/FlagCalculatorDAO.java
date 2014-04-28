package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.FlagCriteria;
import com.picsauditing.flagcalculator.entities.Naics;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class FlagCalculatorDAO extends PicsDAO {
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


}
