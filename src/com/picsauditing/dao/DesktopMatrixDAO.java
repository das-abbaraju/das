package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.DesktopMatrix;
import com.picsauditing.util.Strings;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class DesktopMatrixDAO extends PicsDAO {
	@Transactional
	public DesktopMatrix save(DesktopMatrix o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		DesktopMatrix row = find(id);
		remove(row);
	}

	@Transactional
	public void remove(DesktopMatrix row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public DesktopMatrix find(int id) {
		return em.find(DesktopMatrix.class, id);
	}

	
	public List<DesktopMatrix> findByQuestions(int[] questionIDs) {
		String list = Strings.implode(questionIDs, ",");
		Query query = em.createQuery("FROM DesktopMatrix t WHERE t.question.id IN (" + list + ")");
		return query.getResultList();
	}
}
