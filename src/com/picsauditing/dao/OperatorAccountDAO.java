package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.PermissionQueryBuilder;

@Transactional
@SuppressWarnings("unchecked")
public class OperatorAccountDAO extends PicsDAO {
	public OperatorAccount save(OperatorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OperatorAccount row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorAccount find(int id) {
		return em.find(OperatorAccount.class, id);
	}

	public List<OperatorAccount> findAll(Permissions permissions) {
		String where = "";
		if (permissions.isCorporate())
			// Show corporate users operators in their facility
			where = "operatorAccount IN (SELECT operator FROM Facility " +
			"WHERE corporate = "+permissions.getAccountId()+")";
		if (permissions.isOperator())
			// Show operator users operators that share the same corporate facility
			where = "(operatorAccount.id = "+permissions.getAccountId()+
			" OR operatorAccount IN (SELECT operator FROM Facility " +
			"WHERE corporate IN (SELECT corporate FROM Facility " +
			"WHERE operator.id = "+permissions.getAccountId()+")))";
		return findWhere(where);
	}
	
	public List<OperatorAccount> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from OperatorAccount a " + where
				+ " order by a.type, a.name");
		return query.getResultList();
	}
	
	public int getContractorCount(int id) {
		Account operator = find(id);
		
		String where;
		
		if (operator.getType().equals("Corporate")) {
			where = "operatorAccount IN (SELECT operator FROM Facility WHERE corporate = ?)";
		} else {
			where = "operatorAccount = ?";
		}

		Query query = em.createQuery("SELECT count(c) FROM ContractorAccount c " +
				"WHERE c.active = 'Y' " +
				"AND c IN (SELECT contractorAccount FROM ContractorOperator WHERE "+where+")");
		query.setParameter(1, operator);
		return Integer.parseInt(query.getSingleResult().toString());
	}
}
