package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.ContractorDocumentOperator;
import com.picsauditing.auditbuilder.entities.ContractorDocumentOperatorPermission;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
public class ContractorDocumentOperatorDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorDocumentOperator save(ContractorDocumentOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(ContractorDocumentOperatorPermission caop) {
		em.remove(caop);
	}
}