package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.ContractorAuditOperator;
import com.picsauditing.auditbuilder.entities.ContractorAuditOperatorPermission;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
public class ContractorAuditOperatorDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorAuditOperator save(ContractorAuditOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(ContractorAuditOperatorPermission caop) {
		em.remove(caop);
	}
}