package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AssessmentTest;

@Transactional
@SuppressWarnings("unchecked")
public class AssessmentTestDAO extends PicsDAO {
	public AssessmentTest save(AssessmentTest o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AssessmentTest row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AssessmentTest find(int id) {
		AssessmentTest a = em.find(AssessmentTest.class, id);
		return a;
	}
	
	public List<AssessmentTest> findByAssessmentCenter(int centerID) {
		Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE assessmentCenterID = ?");
		query.setParameter(1, centerID);
		
		return query.getResultList();
	}
}
