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
	public List<OperatorTag> findByOperator(int opID, boolean active) {
		String hql = "SELECT t FROM OperatorTag t WHERE " +
				"t.operator.id = :opID OR " +
				"(t.inheritable = 1 AND " +
				"t.operator IN (SELECT corporate FROM Facility WHERE operator.id = :opID)" +
				")";
		if (active)
			hql += " AND t.active = 1";
		hql += " ORDER BY tag";
		Query query = em.createQuery(hql);
		query.setParameter("opID", opID);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OperatorTag> findUnused(int opID){
		Query query = em.createQuery("FROM OperatorTag o WHERE o.operator.id = ? AND o.operator.id NOT IN "+ 
				"(SELECT c.tag.id FROM ContractorTag c)");
		query.setParameter(1, opID);		
		
		return query.getResultList();
	}
	
}
