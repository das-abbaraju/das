package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.FlagData;

@Transactional
@SuppressWarnings("unchecked")
public class FlagDataDAO extends PicsDAO {

	public FlagData find(int id) {
		return em.find(FlagData.class, id);
	}

	public List<FlagData> findByContractorAndOperator(int conID, int opID) {
		String q = "FROM FlagData d WHERE contractor.id = ? ";
		if (opID > 0)
			q += "AND operator.id = ? ";
		q += "ORDER BY d.criteria.displayOrder";
		Query query = em.createQuery(q);
		query.setParameter(1, conID);
		if (opID > 0)
			query.setParameter(2, opID);
		return query.getResultList();
	}

	public List<FlagData> findByOperator(int opID) {
		Query query = em.createQuery("FROM FlagData d WHERE operator.id = :opID ORDER BY d.contractor.id");
		query.setParameter("opID", opID);
		return query.getResultList();
	}

	public List<FlagData> findProblems(int conID, int opID) {
		// Once/If we fix the issue with flagData not being deleted, then we can
		// revert this back to a simpler DAO call
		String q = "SELECT flagData FROM ContractorOperator co JOIN co.flagDatas as flagData WHERE flagData.criteria.insurance = 0 AND co.contractorAccount.id = ? ";
		if (opID > 0)
			q += "AND co.operatorAccount.id = ? ";
		q += "AND flagData.flag IN ('Red', 'Amber') ORDER BY flagData.criteria.displayOrder";
		Query query = em.createQuery(q);
		query.setParameter(1, conID);
		if (opID > 0)
			query.setParameter(2, opID);
		return query.getResultList();
	}
	
	@Override
	public void remove(BaseTable row) {
		if(row!=null)
			em.remove(row);
	}

}
