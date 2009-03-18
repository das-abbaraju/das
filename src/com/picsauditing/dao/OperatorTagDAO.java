package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorTag;

@Transactional
public class OperatorTagDAO extends PicsDAO {
	public OperatorTag save(OperatorTag o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OperatorTag row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(OperatorTag row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorTag find(int id) {
		return em.find(OperatorTag.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<OperatorTag> findByOperator(int opID) {
		Query query = em.createQuery("SELECT t FROM OperatorTag t WHERE t.operator.id = ? ORDER BY tag");
		query.setParameter(1, opID);
		return query.getResultList();
	}

}
