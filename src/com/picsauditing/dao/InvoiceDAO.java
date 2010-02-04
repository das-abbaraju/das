package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.util.PermissionQueryBuilder;

@Transactional
public class InvoiceDAO extends PicsDAO {
	public Invoice save(Invoice o) {
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

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		qb.setAccountAlias("i.account");
		qb.setShowPendingDeactivated(true);
		String hql = "SELECT i FROM Invoice i " +
				"WHERE i.dueDate < NOW() AND i.paid = 0 " +
				"AND i.account.status = 'Active' "
				+ qb.toString() + " ORDER BY i.dueDate";
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
	
}
