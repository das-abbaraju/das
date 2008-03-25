package com.picsauditing.dao;

import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.jpa.entities.Account;
import java.util.List;

@Transactional
@SuppressWarnings("unchecked")
public class AccountDAO extends PicsDAO {
	public Account save(Account o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		Account row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public Account find(int id) {
        return em.find(Account.class, id);
    }

    public List<Account> findWhere(String where) {
    	if (where == null) where = "";
    	if (where.length() > 0) where = "WHERE " + where;
        Query query = em.createQuery("select a from Account a "+where+" order by a.name");
        return query.getResultList();
    }
    
    public List<Account> findOperators() {
        Query query = em.createQuery("select ac from Account ac where ac.type='Operator' order by ac.name");
        return query.getResultList();
    }
	/*
    public List<Account> findContractors() {
        Query query = em.createQuery("select ac from Account ac where ac.type='Contractor' order by ac.name");
        return query.getResultList();
    }
    */
}
