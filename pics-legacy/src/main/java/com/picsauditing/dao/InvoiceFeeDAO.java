package com.picsauditing.dao;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;

@SuppressWarnings("unchecked")
public class InvoiceFeeDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public InvoiceFee save(InvoiceFee o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		remove(find(id));
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(InvoiceFee row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoiceFee find(int id) {
		return em.find(InvoiceFee.class, id);
	}

	public InvoiceFee findByNumberOfOperatorsAndClass(FeeClass feeClass, int numPayingFacilities) {
        return findDiscountByNumberOfOperatorsAndClass(feeClass, numPayingFacilities, 0);
	}

    public InvoiceFee findDiscountByNumberOfOperatorsAndClass(FeeClass feeClass, int numPayingFacilities, int discountOperatorId) {
        Query query = em.createQuery("FROM InvoiceFee WHERE feeClass = :feeClass"
                + " AND :numPayingFacilities >= minFacilities AND :numPayingFacilities <= maxFacilities" +
                " AND discountOperatorID = :discountOperatorId"
        );

        query.setParameter("feeClass", feeClass);
        query.setParameter("numPayingFacilities", numPayingFacilities);
        query.setParameter("discountOperatorId", discountOperatorId);

        try {
            return (InvoiceFee) query.getSingleResult();
        } catch (EntityNotFoundException | NoResultException nre) {
            return null;
        }
    }


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
