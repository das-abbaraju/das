package com.picsauditing.PICS;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;

/**
 * Utility class for managing a user's watch list. Note: Previous incarnations
 * of this code maintained a local copy of the watchlist in memory and took care
 * to adjust that copy as well as persist changes through Hibernate. This code
 * instead lets Hibernate to do all the work and then relies on cashing to make
 * it efficient.
 */
public class ContractorWatchlistHelper {
	@Autowired
	private UserDAO userDAO;

	private int removedContractorId;

	public List<ContractorWatch> getWatched(User user) {
		return user.getWatchedContractors();
	}

	public void addContractorToWatchList(ContractorAccount contractor, Permissions permissions, User user) {
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

	public boolean isWatching(ContractorAccount contractor, User user) {
		for (ContractorWatch watch : getWatched(user)) {
			if (contractor.equals(watch.getContractor())) {
				return true;
			}
		}
		return false;
	}

	public List<ContractorWatch> getWatchedSortedByContractorName(User user) {
		List<ContractorWatch> watchList = getWatched(user);
		Collections.sort(watchList, new Comparator<ContractorWatch>() {
			public int compare(ContractorWatch o1, ContractorWatch o2) {
				return o1.getContractor().getName().compareTo(o2.getContractor().getName());
			}
		});

		return watchList;
	}

	/**
	 * For giving the user feedback.
	 */
	public int getRemovedContractorId() {
		return removedContractorId;
	}

	public void setRemovedContractorId(int removedContractorId) {
		this.removedContractorId = removedContractorId;
	}
}
