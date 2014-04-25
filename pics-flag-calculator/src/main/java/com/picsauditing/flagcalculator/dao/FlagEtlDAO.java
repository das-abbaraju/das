package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.AmBest;
import com.picsauditing.flagcalculator.entities.AuditData;
import com.picsauditing.flagcalculator.entities.FlagCriteria;
import com.picsauditing.flagcalculator.entities.FlagCriteriaContractor;
import com.picsauditing.flagcalculator.service.FlagService;
import com.picsauditing.flagcalculator.util.Strings;

import javax.persistence.Query;
import java.util.*;

@SuppressWarnings("unchecked")
public class FlagEtlDAO extends PicsDAO {
	/**
	 * This method pulls back all {@link FlagCriteria} that is in use by {@link OperatorAccount}s.
	 */
	public HashSet<FlagCriteria> getDistinctOperatorFlagCriteria() {
		Query query = em.createQuery("SELECT DISTINCT criteria from FlagCriteriaOperator");
		return new HashSet(query.getResultList());
	}

    /**
     * Find all answers for given questions for this contractor Questions can
     * come from any audit type that is Pending, Submitted, Resubmitted or
     * Active but must only have one possible answer
     *
     * @param conID
     * @param questionIds
     * @return Map containing QuestionID => AuditData
     */
    public Map<Integer, AuditData> findAnswersByContractor(int conID, Collection<Integer> questionIds) {
        Map<Integer, AuditData> indexedResult = new HashMap<Integer, AuditData>();
        if (questionIds.size() == 0) {
            return indexedResult;
        }

        Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE d.audit.contractorAccount.id = :conID "
                + "AND (d.audit.expiresDate IS NULL OR d.audit.expiresDate > :today) " + "AND d.question.id IN ("
                + Strings.implode(questionIds) + ")");
        query.setParameter("conID", conID);
        query.setParameter("today", new Date());

        List<AuditData> result = query.getResultList();

        for (AuditData row : result) {
            int questionId = row.getQuestion().getId();
            if (indexedResult.containsKey(questionId)) {
                if (row.getCreationDate() != null && indexedResult.get(questionId).getCreationDate() != null) {
                    if (row.getCreationDate().after(indexedResult.get(questionId).getCreationDate())) {
                        indexedResult.put(questionId, row);
                    }
                }
            } else {
                indexedResult.put(questionId, row);
            }
        }

        return indexedResult;
    }

    public AmBest findByNaic(String naic) {
        Query q = em.createQuery("SELECT ab FROM AmBest ab WHERE ab.naic = ?");
        q.setParameter(1, naic);

        List<AmBest> list = q.getResultList();
        if (list != null && list.size() > 0) {
            if(list.size() == 1) {
                return list.get(0);
            } else {
                for(AmBest amBest : list) {
                    if(amBest.getRatingCode() < 70) {
                        return amBest;
                    }
                }
            }
        }
        return null;
    }

    public Collection<FlagCriteriaContractor> insertUpdateDeleteManaged(Collection<FlagCriteriaContractor> dbLinkedList,
                                                                                        Collection<FlagCriteriaContractor> changes) {
        // update/delete
        Iterator<FlagCriteriaContractor> dbIterator = dbLinkedList.iterator();
        Collection<FlagCriteriaContractor> removalList = new ArrayList<>();

        while (dbIterator.hasNext()) {
            FlagCriteriaContractor fromDB = dbIterator.next();
            FlagCriteriaContractor found = null;

            for (FlagCriteriaContractor change : changes) {
                if (fromDB.equals(change)) {
                    FlagService.updateFlagCriteriaContractor(fromDB, change);
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