package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfoReport;

public class ContractorInfoReportDAO extends PicsDAO {
						 
	public ContractorInfoReport save(ContractorInfoReport o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		ContractorInfoReport row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public ContractorInfoReport find(int id) {
        return em.find(ContractorInfoReport.class, id);
    }
	
	
    public List<ContractorInfoReport> findActiveContractors() {
        Query query = em.createQuery("select cr from ContractorInfoReport cr where cr.account.active='Y' AND cr.pqfSubmittedDate <> '0000-00-00' order by cr.pqfSubmittedDate asc");
        return query.getResultList();
    }
}
