package com.picsauditing.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorTagDAO extends PicsDAO {

	public ContractorTag find(int id) {
		return em.find(ContractorTag.class, id);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		ContractorTag row = find(id);
		remove(row);
	}
	
	public int numberInUse(int tagID) {
		String hql = "SELECT Count(*) FROM ContractorTag WHERE tag.id = ?";
		Query query = em.createQuery(hql);
		query.setParameter(1, tagID);

		return Integer.parseInt(query.getSingleResult().toString());
	}

	
	public List<ContractorTag> getTagsByTagID(int tagID) {
		Query query = em.createQuery("FROM ContractorTag WHERE tag.id = ?");
		query.setParameter(1, tagID);

		return query.getResultList();
	}

	public List<ContractorTag> getContractorTags(int conID, Collection<Integer> tagIDs) {
		Query query = em.createQuery("FROM ContractorTag WHERE contractor.id = ? AND tag.id IN ("
				+ Strings.implode(tagIDs) + ")");
		query.setParameter(1, conID);

		return query.getResultList();
	}
}
