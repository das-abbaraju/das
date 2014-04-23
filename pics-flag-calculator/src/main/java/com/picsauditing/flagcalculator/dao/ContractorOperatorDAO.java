package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.ContractorOperator;

import javax.persistence.EntityManager;

public class ContractorOperatorDAO extends PicsDAO {

    public ContractorOperatorDAO() {}

    public ContractorOperatorDAO(EntityManager em) {
        this.em = em;
    }

    public ContractorOperator find(int id) {
        return em.find(ContractorOperator.class, id);
    }

}
