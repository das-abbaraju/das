package com.picsauditing.dao;

import com.picsauditing.jpa.entities.ContractorAuditOperator;

public class ContractorAuditOperatorDAO extends PicsDAO {
	public ContractorAuditOperator save(ContractorAuditOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorAuditOperator row = find(id);
		remove(row);
	}

	public void remove(ContractorAuditOperator row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorAuditOperator find(int id) {
		return em.find(ContractorAuditOperator.class, id);
	}

}
