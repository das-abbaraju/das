package com.picsauditing.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AccountUser;

@Transactional
@SuppressWarnings("unchecked")
public class AccountUserDAO extends PicsDAO {
	public AccountUser save(AccountUser o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AccountUser row = find(id);
		remove(row);
	}

	public void remove(AccountUser row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public AccountUser find(int id) {
		return em.find(AccountUser.class, id);
	}
	
	public List<AccountUser> findByAccount(int id) {
		Query query =  em.createQuery("SELECT au FROM AccountUser au where au.account.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}
	
	public List<AccountUser> findByUser(int id) {
		Query query =  em.createQuery("SELECT au FROM AccountUser au where au.user.id = :id AND :today BETWEEN au.startDate AND au.endDate AND au.role = 'PICSAccountRep'");
		query.setParameter("id", id);
		Calendar calendar = Calendar.getInstance();
		query.setParameter("today", calendar.getTime());
		return query.getResultList();
	}
}
