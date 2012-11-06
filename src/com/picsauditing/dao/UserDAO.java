package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class UserDAO extends PicsDAO {
	
	private static final String FIND_USERS_BY_IDS = "SELECT u FROM User u WHERE u.id IN ( :userIds ) ORDER BY u.name ";
	
	private final Logger logger = LoggerFactory.getLogger(UserDAO.class);
	
	@Transactional(propagation = Propagation.NESTED)
	public User save(User o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public User find(int id) {
		return em.find(User.class, id);
	}

	public List<User> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT u FROM User u " + where + " ORDER BY u.account.name, u.name");
		return query.getResultList();
	}

	public List<User> findByEmail(String email) {
		Query query = em.createQuery("SELECT u FROM User u WHERE email = ?");
		query.setParameter(1, email);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<User> findAuditors() {
		return findByGroup(User.GROUP_AUDITOR);
	}

	public List<User> findByGroup(int groupID) {
		List<User> userList = new ArrayList<User>();

		Query query = em.createQuery("FROM User u " + "WHERE u.isActive = 'Yes' " + "AND u.isGroup = 'No' "
				+ "AND u IN (SELECT user FROM UserGroup WHERE group.id = " + groupID + ") " + "ORDER BY u.name");
		userList.addAll(query.getResultList());

		return userList;
	}

	/**
	 * 
	 * @param uName
	 * @param uID
	 * @return true if the username is already in use by another user
	 */
	public boolean duplicateUsername(String uName, int uID) {
		try {
			User user = findName(uName);
			if (user == null)
				return false;

			int id = user.getId();
			if (id > 0) {
				// found a user with this username
				if (id != uID)
					// This is in use by another user
					return true;
			}
		} catch (Exception e) {
			logger.error("Exception in checkUserName: {}", e.getMessage());
		}
		return false;
	}

	public User findName(String userName) {
		if (userName == null)
			userName = "";

		try {
			Query query = em.createQuery("SELECT u FROM User u WHERE username = ?");
			query.setParameter(1, userName);
			return (User) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public int getUsersCounts() {
		Query query = em.createQuery("SELECT count(u) FROM User u " + "WHERE u.isActive = 'Yes'");
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public List<User> findRecentLoggedContractors() {
		String sql = "SELECT u FROM User u WHERE u.account.type = 'Contractor' ORDER BY u.lastLogin DESC";
		Query query = em.createQuery(sql);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<User> findRecentLoggedOperators() {
		Query query = em.createQuery("SELECT u FROM User u WHERE u.account.type IN ('Operator','Corporate') "
				+ "ORDER BY u.lastLogin DESC");
		query.setMaxResults(10);
		return query.getResultList();
	}

	/**
	 * 
	 * @param id
	 * @param isActive
	 *            Yes, No, or ""
	 * @param isGroup
	 *            Yes, No, or ""
	 * @return
	 */
	public List<User> findByAccountID(int id, String isActive, String isGroup) {
		String where = "";
		if ("Yes".equals(isGroup) || "No".equals(isGroup))
			where += " AND isGroup = '" + isGroup + "' ";

		if ("Yes".equals(isActive) || "No".equals(isActive))
			where += " AND isActive = '" + isActive + "' ";

		Query query = em.createQuery("SELECT u FROM User u WHERE account.id = ? " + where + " ORDER BY isGroup, name");

		query.setParameter(1, id);
		return query.getResultList();
	}

	public List<ContractorWatch> findContractorWatchWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT c FROM ContractorWatch c" + where
				+ " ORDER BY c.user.name, c.contractor.name");

		return query.getResultList();
	}

	public User findByApiKey(String apiKey) {
		if (apiKey == null) {
			return null;
		}

		try {
			Query query = em.createQuery("SELECT u FROM User u WHERE apiKey = ?");
			query.setParameter(1, apiKey);
			return (User) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<User> findByIds(Collection<Integer> ids) {
		Query query = em.createQuery(FIND_USERS_BY_IDS);
		query.setParameter("userIds", ids);
		return query.getResultList();		
	}
}