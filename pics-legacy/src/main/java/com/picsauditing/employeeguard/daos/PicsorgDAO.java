package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PicsorgDAO {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@PersistenceContext
	protected EntityManager em;


	public List<Account> findContractorClientSitesNotAttachedToProjects(int contractorId, Collection<Integer> excludeClientSites){
		List<Account> accounts = new ArrayList<>();
		try {

			if(excludeClientSites.size()==0)
				return this.findContractorClientSitesNotAttachedToProjects(contractorId);

			String nativeQueryString="SELECT a.*, o.* FROM contractor_operator co " +
							"	JOIN operators o on o.id=co.opID" +
							" JOIN contractor_info ci on ci.id=co.conID " +
							" JOIN accountemployeeguard aeg ON aeg.accountID=o.id " +
								" JOIN accounts a ON a.id=aeg.accountID AND a.type='Operator'" +
							" WHERE " +
							" co.conID = :contractorId AND " +
							" co.opID NOT IN (:excludeClientSites)";

			Query query = em.createNativeQuery(nativeQueryString, OperatorAccount.class);

			query.setParameter("contractorId", contractorId);
			query.setParameter("excludeClientSites", excludeClientSites);

			return  query.getResultList();

		} catch (Exception e) {
			logger.error("Error finding Contractor Client Sites Not Attached  to Projects", e);
		}

		return accounts;

	}


	private List<Account> findContractorClientSitesNotAttachedToProjects(int contractorId){
		List<Account> accounts = new ArrayList<>();
		try {


			String nativeQueryString="SELECT a.*, o.* FROM contractor_operator co " +
							"	JOIN operators o on o.id=co.opID" +
							" JOIN contractor_info ci on ci.id=co.conID " +
							" JOIN accountemployeeguard aeg ON aeg.accountID=o.id " +
							" JOIN accounts a ON a.id=aeg.accountID AND a.type='Operator'" +
							" WHERE " +
							" co.conID = :contractorId ";

			Query query = em.createNativeQuery(nativeQueryString, OperatorAccount.class);

			query.setParameter("contractorId", contractorId);

			return  query.getResultList();

		} catch (Exception e) {
			logger.error("Error finding Contractor Client Sites Not Attached  to Projects", e);
		}

		return accounts;

	}



}
