package com.picsauditing.employeeguard.daos;

import com.picsauditing.jpa.entities.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

public class PicsorgDAO {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@PersistenceContext
	protected EntityManager em;


	public List<Account> findContractorClientSitesNotAttachedToProjects(Collection<Integer> excludeClientSites){
		return null;
	}
}
