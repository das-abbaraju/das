package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorNote;

@SuppressWarnings("unchecked")
public class ContractorNoteDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorNote save(ContractorNote o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public ContractorNote find(int id) {
		return em.find(ContractorNote.class, id);
	}

	public List<ContractorNote> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT a from ContractorNote a " + where);
		return query.getResultList();
	}
	
}
