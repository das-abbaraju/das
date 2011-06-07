package com.picsauditing.dao;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;

@Transactional
public class InvoiceFeeDAO extends PicsDAO {

	public InvoiceFee save(InvoiceFee o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		remove(find(id));
	}

	public void remove(InvoiceFee row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoiceFee find(int id) {
		return em.find(InvoiceFee.class, id);
	}

	public InvoiceFee findByNumberOfOperatorsAndClass(FeeClass feeClass, int numPayingFacilities) {
		Query query = em.createQuery("FROM InvoiceFee WHERE feeClass = :feeClass"
				+ " AND :numPayingFacilities >= minFacilities AND :numPayingFacilities <= maxFacilities");

		query.setParameter("feeClass", feeClass);
		query.setParameter("numPayingFacilities", numPayingFacilities);

		try {
			return (InvoiceFee) query.getSingleResult();
		} catch (EntityNotFoundException nre) {
			return null;
		}
	}
	
	public InvoiceFee findMembershipByLegacyAuditGUARDID(FeeClass classType, InvoiceFee legacyAuditGUARDfee) {
		int numPayingFacilities = 0;
		// assigning numFacilities based on legacy id
		switch(legacyAuditGUARDfee.getId()) {
			case 5: numPayingFacilities = 1; break;
			case 105: numPayingFacilities = 1; break;
			case 6: numPayingFacilities = 2; break;
			case 7: numPayingFacilities = 5; break;
			case 8: numPayingFacilities = 9; break;
			case 9: numPayingFacilities = 13; break;
			case 10: numPayingFacilities = 20; break;
			case 11: numPayingFacilities = 50; break;
		}
		
		return this.findByNumberOfOperatorsAndClass(classType, numPayingFacilities);
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceFee> findAll() {
		Query query = em.createQuery("FROM InvoiceFee ORDER BY fee");
		return query.getResultList();
	}

	public InvoiceFee findByName(String feeName) {
		Query query = em.createQuery("FROM InvoiceFee where fee = ?");

		query.setParameter(1, feeName);

		try {
			return (InvoiceFee) query.getSingleResult();
		} catch (Exception nre) {
			return null;
		}
	}

}
