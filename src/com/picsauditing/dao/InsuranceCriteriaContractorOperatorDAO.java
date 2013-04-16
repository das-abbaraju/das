package com.picsauditing.dao;

import com.picsauditing.jpa.entities.InsuranceCriteriaContractorOperator;

import javax.persistence.Query;
import java.util.List;

public class InsuranceCriteriaContractorOperatorDAO extends PicsDAO {
    public InsuranceCriteriaContractorOperator findBy(int flagCriteriaID, int contractorID, int operatorID) {
        Query query = em.createQuery("FROM InsuranceCriteriaContractorOperator i " +
                "WHERE i.flagCriteria.id = :flagCriteriaID " +
                "AND i.contractorAccount.id = :contractorID " +
                "AND i.operatorAccount.id = :operatorID");

        query.setParameter("flagCriteriaID", flagCriteriaID);
        query.setParameter("contractorID", contractorID);
        query.setParameter("operatorID", operatorID);

        return (InsuranceCriteriaContractorOperator) query.getSingleResult();
    }

    public List<InsuranceCriteriaContractorOperator> findByContractorId(int contractorID) {
        Query query = em.createQuery("FROM InsuranceCriteriaContractorOperator i " +
                "WHERE i.contractorAccount.id = :contractorID");

        query.setParameter("contractorID", contractorID);

        return query.getResultList();
     }
}
