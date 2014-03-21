package com.picsauditing.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.InvoiceOperatorCommission;
import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.InvoiceCommission;

@SuppressWarnings("unchecked")
public class InvoiceCommissionDAO extends PicsDAO {
	
	private static final String FIND_INVOICE_COMMISSIONS_BY_INVOICE_ID = "SELECT ic FROM InvoiceCommission ic WHERE ic.invoice.id = :invoiceId";
	
	private static final String FIND_INVOICE_COMMISSIONS_NOT_VOID = "SELECT ic FROM InvoiceCommission ic " +
			"WHERE ic.invoice.id = :invoiceId " +
			"AND ic.revenuePercent != 0 OR ic.points != 0";

    private static final String FIND_INVOICE_OPERATOR_COMMISSIONS_BY_INVOICE_ID = "SELECT ic FROM InvoiceOperatorCommission ic WHERE ic.invoice.id = :invoiceId";

	public List<InvoiceCommission> findInvoiceCommissionNotVoid(int invoiceId) {
		Query query = em.createQuery(FIND_INVOICE_COMMISSIONS_NOT_VOID);
		query.setParameter("invoiceId", invoiceId);
		return query.getResultList();
	}
	
	public List<InvoiceCommission> findInvoiceCommissionsByInvoiceId(int invoiceId) {
		Query query = em.createQuery(FIND_INVOICE_COMMISSIONS_BY_INVOICE_ID);
		query.setParameter("invoiceId", invoiceId);
		return query.getResultList();
	}

    public List<InvoiceOperatorCommission> findInvoiceOperatorCommissionsByInvoiceId(int invoiceId) {
        Query query = em.createQuery(FIND_INVOICE_OPERATOR_COMMISSIONS_BY_INVOICE_ID);
        query.setParameter("invoiceId", invoiceId);
        return query.getResultList();
    }

}