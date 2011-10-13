package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.Refund;

@Transactional(readOnly = true)
public class RefundDAO extends PicsDAO {
	@Transactional
	public Refund save(Refund o) {
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

	@Transactional
	public void remove(Refund row) {
		if (row != null) {
			em.remove(row);
		}
		for (PaymentAppliedToRefund ip : row.getPayments())
			ip.getPayment().updateAmountApplied();
		row = null;
	}

	public Refund find(int id) {
		return em.find(Refund.class, id);
	}

}
