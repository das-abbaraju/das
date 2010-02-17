package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagCriteriaContractor;

@Transactional
@SuppressWarnings("unchecked")
public class FlagCriteriaContractorDAO extends PicsDAO {
	public List<FlagCriteriaContractor> findByContractor(int conID) {
		Query query = em.createQuery("FROM FlagCriteriaContractor d WHERE contractor.id = ?");
		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public void deleteEntriesForContractor(int conID){
		Query query = em.createQuery("DELETE FROM FlagCriteriaContractor fcc WHERE fcc.contractor.id = ?");
		query.setParameter(1, conID);
		query.executeUpdate();
	}
}
