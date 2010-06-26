package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class AssessmentResultDAO extends PicsDAO {
	public AssessmentResult save(AssessmentResult o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AssessmentResult row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AssessmentResult find(int id) {
		AssessmentResult a = em.find(AssessmentResult.class, id);
		return a;
	}

	public List<AssessmentResult> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT a FROM AssessmentResult a WHERE employeeID = ?");
		query.setParameter(1, employeeID);

		return query.getResultList();
	}
	
	public Map<Employee, List<AssessmentResult>> findByAccount(Account account) {
		Query query = em.createQuery("SELECT a FROM AssessmentResult a WHERE a.employee.account = :account");
		query.setParameter("account", account);
		
		Map<Employee, List<AssessmentResult>> map = new HashMap<Employee, List<AssessmentResult>>();
		List<AssessmentResult> list = query.getResultList();
		for (AssessmentResult item : list) {
			if (!map.containsKey(item.getEmployee()))
				map.put(item.getEmployee(), new ArrayList<AssessmentResult>());
			
			map.get(item.getEmployee()).add(item);
		}
		
		return map;
	}

	public List<AssessmentResult> findAll() {
		Query query = em.createQuery("SELECT a FROM AssessmentResult a");

		return query.getResultList();
	}

	public List<AssessmentResult> findExpired(String where, Date date) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " AND " + where;

		Query query = em.createQuery("SELECT a FROM AssessmentResult a WHERE expirationDate <= ?"
				+ where + " ORDER BY expirationDate DESC");
		query.setParameter(1, date);
		
		return query.getResultList();
	}

	public List<AssessmentResult> findInEffect(String where, Date date) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " AND " + where;

		Query query = em.createQuery("SELECT a FROM AssessmentResult a WHERE (expirationDate > :date"
				+ " OR expirationDate IS NULL) AND (effectiveDate <= :date" + where + 
				" OR effectiveDate IS NULL) ORDER BY effectiveDate DESC");
		query.setParameter("date", date);
		return query.getResultList();
	}

	public List<Date> findHistory(String where) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT DISTINCT a.effectiveDate FROM AssessmentResult a"
				+ where + " ORDER BY effectiveDate DESC");
		return query.getResultList();
	}
	
	public List<AssessmentResult> findByAssessmentCenter(int centerID) {
		Query query = em.createQuery("SELECT a FROM AssessmentResult a " +
				"WHERE a.assessmentTest.assessmentCenter.id = ? ORDER BY effectiveDate DESC");
		query.setParameter(1, centerID);
		
		return query.getResultList();
	}
}
