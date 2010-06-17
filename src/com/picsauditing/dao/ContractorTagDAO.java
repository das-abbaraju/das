package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorTag;

@Transactional
public class ContractorTagDAO extends PicsDAO {
	public ContractorTag save(ContractorTag o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorTag row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(ContractorTag row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorTag find(int id) {
		return em.find(ContractorTag.class, id); 
	}
	
	public int numberInUse(int tagID){ 
		String hql = "SELECT Count(*) FROM ContractorTag WHERE tag.id = ?"; 
		Query query = em.createQuery(hql); 
		query.setParameter(1, tagID); 
		
		return Integer.parseInt(query.getSingleResult().toString()); 
	}
	
	@SuppressWarnings("unchecked")
	public List<ContractorTag> getTagsByTagID(int tagID){ 
		Query query = em.createQuery("FROM ContractorTag WHERE tag.id = ?");
		query.setParameter(1, tagID); 
		
		return query.getResultList(); 
	}

}
