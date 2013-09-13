package com.picsauditing.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.PaymentCommission;

@SuppressWarnings("unchecked")
public class PaymentCommissionDAO extends BaseTableDAO<PaymentCommission> {

	private static final String QUERY_BY_PAYMENT_ID = "SELECT pc FROM PaymentCommission pc " +
			"WHERE pc.payment.id = :paymentId";
	
	private static final String QUERY_BY_INVOICE_COMMISSION_ID = "SELECT pc FROM PaymentCommission pc " +
			"WHERE pc.invoiceCommission.id IN ( :invoiceCommissionIds )";
	
	private static final String FIND_PAYMENTS_WITH_ZERO_AMOUNT = "SELECT pc from PaymentCommission pc " +
			"WHERE pc.activationPoints != 0 " +
			"AND pc.paymentAmount != 0 " +
			"AND pc.invoiceCommission.id IN ( :invoiceCommissionIds )";
	
	public PaymentCommissionDAO() {
		super(PaymentCommission.class);
	}
		
	public List<PaymentCommission> findByPaymentId(int paymentId) {
		Query query = em.createQuery(QUERY_BY_PAYMENT_ID);
		query.setParameter("paymentId", paymentId);
		return query.getResultList();
	}
	
	public List<PaymentCommission> findByInvoiceCommissionId(int invoiceCommissionId) {
		return findByInvoiceCommissionIds(Arrays.asList(invoiceCommissionId));
	}
	
	public List<PaymentCommission> findByInvoiceCommissionIds(Collection<Integer> ids) {
		Query query = em.createQuery(QUERY_BY_INVOICE_COMMISSION_ID, PaymentCommission.class);
		query.setParameter("invoiceCommissionId", ids);
		return query.getResultList();
	}
	
	public List<PaymentCommission> findNonRefundedPaymentsByIds(Collection<Integer> ids) {
		Query query = em.createQuery(FIND_PAYMENTS_WITH_ZERO_AMOUNT);
		query.setParameter("invoiceCommissionIds", ids);
		return query.getResultList();
	}
	
//	@SuppressWarnings("unchecked")
//	public PaymentCommission findByPaymentId(int paymentId) {
//		Query query = em.createQuery(QUERY_BY_PAYMENT_ID);
//		query.setParameter("paymentId", paymentId);
//		return query.getResultList();
//	}

}