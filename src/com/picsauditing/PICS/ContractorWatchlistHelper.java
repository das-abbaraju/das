package com.picsauditing.PICS;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;

/**
 * Utility class for managing a user's watch list. Note: Previous incarnations
 * of this code maintained a local copy of the watchlist in memory and took care
 * to adjust that copy as well as persist changes through Hibernate. This code instead
 * lets Hibernate to do all the work and then relies on cashing to make it efficient.
 */
public class ContractorWatchlistHelper implements InitializingBean {
	private Permissions permissions;
	private User user;
	private int removedContractorId;

	/**
	 * For giving the user feedback.
	 */
	public int getRemovedContractorId() {
		return removedContractorId;
	}

	private UserDAO userDAO;

	public UserDAO getUserDAO() {
		return userDAO;
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public ContractorWatchlistHelper(User user, Permissions permissions) {
		super();
		this.user = user;
		this.permissions = permissions;
	}

	public List<ContractorWatch> getWatched() {
		return user.getWatchedContractors();
	}

	public void addContractorToWatchList(ContractorAccount contractor) {
		ContractorWatch watch = new ContractorWatch();
		watch.setAuditColumns(permissions);
		watch.setContractor(contractor);
		watch.setUser(user);
		userDAO.save(watch);
	}

	public void removeContractorFromWatchList(ContractorWatch watchToRemove) {
		removedContractorId = 0;
		if (watchToRemove == null) {
			return;
		}
		removedContractorId = watchToRemove.getContractor().getId();
		userDAO.remove(watchToRemove);
	}

	public boolean isWatching(ContractorAccount contractor) {
		for (ContractorWatch watch : getWatched()) {
			if (contractor.equals(watch.getContractor())) {
				return true;
			}
		}
		return false;
	}

	public List<ContractorWatch> getWatchedSortedByContractorName() {
		List<ContractorWatch> watchList = getWatched();
		Collections.sort(watchList, new Comparator<ContractorWatch>() {
			public int compare(ContractorWatch o1, ContractorWatch o2) {
				return o1.getContractor().getName().compareTo(o2.getContractor().getName());
			}
		});
		return watchList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// nothing to do
	}

}
