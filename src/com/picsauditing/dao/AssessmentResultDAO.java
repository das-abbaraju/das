package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AssessmentResult;

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
	
	public List<AssessmentResult> findAll() {
		Query query = em.createQuery("SELECT a FROM AssessmentResult a");
		
		return query.getResultList();
	}
}
