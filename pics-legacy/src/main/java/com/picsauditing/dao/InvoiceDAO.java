package com.picsauditing.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;

@SuppressWarnings("unchecked")
public class InvoiceDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public Invoice save(Invoice o) {
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
	public void remove(Invoice row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Invoice find(int id) {
		return em.find(Invoice.class, id);
	}

	public List<Invoice> findDelinquentContractors(Permissions permissions, int limit) {
		if (permissions == null)
			return new ArrayList<Invoice>();

		String hql = "SELECT i FROM Invoice i " + "WHERE i.dueDate < NOW() AND i.status = 'Unpaid' "
				+ "AND i.account.status = 'Active' ";
		if (permissions.isOperator()) {
			hql += "AND i.account.id IN (SELECT t.contractorAccount.id FROM ContractorOperator t WHERE t.operatorAccount.id = "
					+ permissions.getAccountId() + ") ";
		}
		hql += "ORDER BY i.dueDate";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Invoice> findWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT i FROM Invoice i " + where + " ORDER BY i.id");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Invoice> findDelinquentInvoicesMissingLateFees() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -30);

		String hql = "SELECT i FROM Invoice i JOIN i.account AS account "
				+ "LEFT JOIN i.items AS item WITH item.invoiceFee.id = :oldfee OR item.invoiceFee.id = :fee "
				+ "WHERE i.dueDate < :dueDate AND i.status = :status AND item IS NULL "
				+ "AND i.account.status = :astatus AND i.totalAmount > :totalAmount "
				+ "AND i.lateFeeInvoice IS NULL AND i.invoiceType != :invoiceType";
		Query query = em.createQuery(hql);
		query.setParameter("dueDate", cal.getTime());
		query.setParameter("status", TransactionStatus.Unpaid);
		query.setParameter("fee", InvoiceFee.LATEFEE);
		query.setParameter("oldfee", InvoiceFee.OLDLATEFEE);
		query.setParameter("astatus", AccountStatus.Active);
		query.setParameter("totalAmount", BigDecimal.ZERO);
		query.setParameter("invoiceType", InvoiceType.LateFee);
		return query.getResultList();
	}

    public List<Transaction> findTransactionsToSapSync() {
        String sql = " SELECT i.* " +
                " FROM accounts a " +
                " JOIN invoice i ON a.id = i.accountID " +
                " JOIN ref_country rc ON a.country = rc.isocode " +
                " WHERE a.status NOT IN ('Pending','Declined','Demo') " +
                " AND i.status != 'Void' " +
                " AND i.sapLastSync IS NULL " +
                " AND i.sapSync = 0 " +
                " AND i.creationDate >= '2011-11-01' " +
                " AND i.creationDate < DATE_SUB(NOW(), INTERVAL 1 DAY)" +
                " AND rc.businessUnitID IN (select value FROM app_properties WHERE property = 'SAP.BusinessUnits.Enabled')";

        Query query = em.createNativeQuery(sql);
        return query.getResultList();
    }

    public void updateTransactionsToSapSync() {
        String sql = " UPDATE accounts a " +
                " JOIN invoice i ON a.id = i.accountID " +
                " JOIN ref_country rc ON a.country = rc.isocode " +
                " SET i.sapSync = 1 " +
                " WHERE a.status NOT IN ('Pending','Declined','Demo') " +
                " AND i.status != 'Void' " +
                " AND i.sapLastSync IS NULL " +
                " AND i.sapSync = 0 " +
                " AND i.creationDate >= '2011-11-01' " +
                " AND i.creationDate < DATE_SUB(NOW(), INTERVAL 1 DAY)" +
                " AND rc.businessUnitID IN (select value FROM app_properties WHERE property = 'SAP.BusinessUnits.Enabled')";

        Query query = em.createNativeQuery(sql);
        query.executeUpdate();
    }
}
