package com.picsauditing.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.InvoiceCommission;

@SuppressWarnings("unchecked")
public class InvoiceCommissionDAO extends BaseTableDAO<InvoiceCommission> {
	
	private static final String FIND_BY_INVOICE_ID = "SELECT ic FROM InvoiceCommission ic WHERE ic.invoice.id = :invoiceId";
	
	private static final String FIND_INVOICE_COMMISSIONS_NOT_VOID = "SELECT ic FROM InvoiceCommission ic " +
			"WHERE ic.invoice.id = :invoiceId " +
			"AND ic.revenuePercent != 0 AND ic.points != 0";
	
	private static final String FIND_BY_INVOICE_IDS = "SELECT ic FROM InvoiceCommission ic WHERE ic.invoice.id in (:invoiceIds)";
	
	public InvoiceCommissionDAO() {
		super(InvoiceCommission.class);
	}

	public List<InvoiceCommission> findInvoiceCommissionNotVoid(int invoiceId) {
		Query query = em.createQuery(FIND_INVOICE_COMMISSIONS_NOT_VOID);
		query.setParameter("invoiceId", invoiceId);
		return query.getResultList();
	}
	
	public List<InvoiceCommission> findByInvoiceId(int invoiceId) {
		Query query = em.createQuery(FIND_BY_INVOICE_ID);
		query.setParameter("invoiceId", invoiceId);
		return query.getResultList();
	}
	
	public InvoiceCommission findOneByInvoiceId(int invoiceId) {
		Query query = em.createQuery(FIND_BY_INVOICE_ID);
		query.setParameter("invoiceId", invoiceId);
		return (InvoiceCommission) query.getSingleResult();
	}
	
	public List<InvoiceCommission> findByInvoiceIds(Collection<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		
		Query query = em.createQuery(FIND_BY_INVOICE_IDS);
		query.setParameter("invoiceIds", ids);
		return query.getResultList();
	}
	
}