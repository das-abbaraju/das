package com.picsauditing.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.Transaction;

public class InvoiceCommissionDAO extends BaseTableDAO<InvoiceCommission> {
	
	private static final String QUERY_BY_INVOICE_ID = "SELECT ic FROM InvoiceCommission ic WHERE ic.invoice.id = :invoiceId";
	private static final String QUERY_MANY_BY_INVOICE_ID = "SELECT ic FROM InvoiceCommission ic WHERE ic.invoice.id in (:invoiceIds)";
	
	public InvoiceCommissionDAO() {
		super(InvoiceCommission.class);
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceCommission> findByInvoiceId(int invoiceId) {
		Query query = em.createQuery(QUERY_BY_INVOICE_ID);
		query.setParameter("invoiceId", invoiceId);
		return query.getResultList();
	}
	
	public InvoiceCommission findOneByInvoiceId(int invoiceId) {
		Query query = em.createQuery(QUERY_BY_INVOICE_ID);
		query.setParameter("invoiceId", invoiceId);
		return (InvoiceCommission) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<InvoiceCommission> findByTransactions(List<Transaction> transactions) {
		Query query = em.createQuery(QUERY_MANY_BY_INVOICE_ID);
		List<Integer> transactionIds = Utilities.getIdList(transactions);
		if (CollectionUtils.isEmpty(transactionIds)) {
			return Collections.emptyList();
		}
		
		query.setParameter("invoiceIds", transactionIds);
		return query.getResultList();
	}
	
}