package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorAccountDAO extends PicsDAO {
	public ContractorAccount save(ContractorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorAccount row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorAccount find(int id) {
		return em.find(ContractorAccount.class, id);
	}

	public List<Integer> findAll() {
		Query query = em.createQuery("select a.id from ContractorAccount a");
		return query.getResultList();
	}
	
	public List<ContractorAccount> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from ContractorAccount a " + where
				+ " order by a.name");
		return query.getResultList();
	}
	
	public List<ContractorOperator> findOperators(ContractorAccount contractor, Permissions permissions) {
		String where = "";
		if (permissions.isCorporate())
			// Show corporate users operators in their facility
			where = "AND operatorAccount IN (SELECT operator FROM Facility " +
			"WHERE corporate = "+permissions.getAccountId()+")";
		if (permissions.isOperator())
			// Show operator users operators that share the same corporate facility
			where = "AND (operatorAccount.id = "+permissions.getAccountId()+
			" OR operatorAccount IN (SELECT operator FROM Facility " +
			"WHERE corporate IN (SELECT corporate FROM Facility " +
			"WHERE operator.id = "+permissions.getAccountId()+")))";
		
		Query query = em.createQuery("FROM ContractorOperator WHERE contractorAccount = ? "+where+" ORDER BY operatorAccount.name");
		query.setParameter(1, contractor);
		return query.getResultList();
	}

	public List<ContractorAccount> findNewContractors(Permissions permissions, int limit) {
		if (permissions == null)
			return new ArrayList<ContractorAccount>();
		String where = "active='Y'";
		if (permissions.isOperator())
			where += " AND a IN (SELECT contractorAccount FROM ContractorOperator " +
					"WHERE operatorAccount.id = "+permissions.getAccountId()+")";
		
		Query query = em.createQuery("FROM ContractorAccount a WHERE "+where+" ORDER BY dateCreated DESC");
		query.setMaxResults(limit);
		return query.getResultList();
	}
}
