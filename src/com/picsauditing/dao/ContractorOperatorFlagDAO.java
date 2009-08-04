package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorOperatorFlag;

@Transactional
public class ContractorOperatorFlagDAO extends PicsDAO {
	public ContractorOperatorFlag save(ContractorOperatorFlag o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorOperatorFlag row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorOperatorFlag find(int id) {
		return em.find(ContractorOperatorFlag.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ContractorOperatorFlag> findFlagChangedByOperatorAndRange(int opID, Date start, Date end) {
		String query = "FROM ContractorOperatorFlag WHERE operatorAccount.id = :opID AND lastUpdate BETWEEN :start and :end";

		Query q = em.createQuery(query);
		q.setParameter("opID", opID);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorOperatorFlag> findWhere(String where) {
		String query = "FROM ContractorOperatorFlag WHERE " + where + " ORDER BY contractorAccount.name";

		Query q = em.createQuery(query);
		return q.getResultList();
	}

}
