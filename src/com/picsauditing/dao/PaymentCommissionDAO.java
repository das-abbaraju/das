package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.picsauditing.jpa.entities.PaymentCommission;

public class PaymentCommissionDAO extends BaseTableDAO<PaymentCommission> {

	private static final String QUERY_BY_PAYMENT_ID = "SELECT pc FROM PaymentCommission pc WHERE pc.payment.id = :paymentId";
	private static final String QUERY_BY_INVOICE_COMMISSION_ID = "SELECT pc FROM PaymentCommission pc " +
			"WHERE pc.invoiceCommission.id = :invoiceCommissionId";
	
	public PaymentCommissionDAO() {
		super(PaymentCommission.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<PaymentCommission> findByPaymentId(int paymentId) {
		Query query = em.createQuery(QUERY_BY_PAYMENT_ID);
		query.setParameter("paymentId", paymentId);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PaymentCommission> findByInvoiceCommissionId(int invoiceCommissionId) {
		Query query = em.createQuery(QUERY_BY_INVOICE_COMMISSION_ID, PaymentCommission.class);
		query.setParameter("invoiceCommissionId", invoiceCommissionId);
		return query.getResultList();
	}
	
//	@SuppressWarnings("unchecked")
//	public PaymentCommission findByPaymentId(int paymentId) {
//		Query query = em.createQuery(QUERY_BY_PAYMENT_ID);
//		query.setParameter("paymentId", paymentId);
//		return query.getResultList();
//	}

}