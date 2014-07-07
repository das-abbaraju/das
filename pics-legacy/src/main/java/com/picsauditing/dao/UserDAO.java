package com.picsauditing.dao;

import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.security.EncodedKey;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class UserDAO extends PicsDAO {

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

    public List<User> findWhere(String where, int limit) {
        if (where == null) {
            where = "";
        }

        if (where.length() > NO_LIMIT) {
            where = "WHERE " + where;
        }

        Query query = em.createQuery("SELECT u from User u " + where + " ORDER BY u.name");
        if (limit > 0) {
            query.setMaxResults(limit);
        }

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

	public List<User> findAuditors(Set<Integer> assignerGroupIds) {
		List<User> userList = new ArrayList<User>();

		Query query = em.createNativeQuery("select * from users u "
				+ "join usergroup grp on u.id=grp.userID "
				+ "join audit_type atype on atype.assignAudit in ("
				+ Strings.implode(assignerGroupIds)+ ") "
				+ "where grp.groupID = atype.editAudit", User.class);
		userList.addAll(query.getResultList());

		return userList;
	}

	public List<User> findCorporateAuditors(int corporateId) {
		List<User> userList = new ArrayList<User>();

		Query query = em.createNativeQuery("select * from users u "
				+ "JOIN usergroup member ON member.userID = u.id "
				+ "JOIN users grp ON grp.id = member.groupID "
				+ "JOIN useraccess ua ON ua.userID = grp.id "
				+ "WHERE ua.accessType IN ('AuditEdit','ClientAuditEdit') "
				+ "AND ua.viewFlag = 1 "
				+ "AND u.isActive = 1 "
				+ "AND grp.accountID=" + corporateId, User.class);
		userList.addAll(query.getResultList());
		return userList;
	}

	public List<User> findByGroup(int groupID) {
		List<User> userList = new ArrayList<User>();

		Query query = em.createQuery("FROM User u " + "WHERE u.isActive = 'Yes' " + "AND u.isGroup = 'No' "
				+ "AND u IN (SELECT user FROM UserGroup WHERE group.id = " + groupID + ") " + "ORDER BY u.name");
		userList.addAll(query.getResultList());

		return userList;
	}

	public List<User> findByGroupAndUserName(int groupId, String name) {
		Query query = em.createQuery("FROM User u " + "WHERE u.isActive = 'Yes' " + "AND u.isGroup = 'No' AND u.name LIKE '%" + name + "%'"
				+ "AND u IN (SELECT user FROM UserGroup WHERE group.id = " + groupId + ") " + "ORDER BY u.name");
		return query.getResultList();
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
			if (user == null) {
				return false;
            }

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
			Query query = em.createNativeQuery("SELECT u.* FROM users u JOIN app_user a ON a.id = u.appUserID WHERE a.username = :username", User.class);
			query.setParameter("username", userName);
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

	public List<User> findUsersByAccountId(int accountId) {
		String queryString = "SELECT u" +
				" FROM User u" +
				" WHERE u.account.id = :accountId" +
				" AND u.isGroup = '" + YesNo.No.toString() + "'";

		Query query = em.createQuery(queryString);
		query.setParameter("accountId", accountId);

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

	public User findByApiKey(String apiKey) throws SecurityException {
		if (apiKey == null) {
			return null;
		}
        EncodedKey.verifySufficientlyComplex(apiKey);

		try {
			TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.apiKey = :apiKey", User.class);
			query.setParameter("apiKey", apiKey);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

    public List<User> findByOperatorAccount(OperatorAccount operator, int limit) {
        try {
            Query query = em.createQuery(
                    "FROM User u " +
                            "WHERE u.account.id = :opId " +
                            "AND u.isGroup = 'No' " +
                            "AND u.isActive = 'Yes' " +
                            "ORDER BY u.lastLogin DESC "
            );
            query.setParameter("opId", operator.getId());
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return Collections.EMPTY_LIST;
	}

	public User findUserByAppUserID(int appUserID) {
		Query query = em.createNativeQuery("select u.* from users u WHERE u.appUserID = :appUserID", User.class);
		query.setParameter("appUserID", appUserID);

		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
            logger.info("Exception finding app user by ID {} ", appUserID, e);
		}

		return null;
	}


	public int findUserIDByAppUserID(int appUserID) {
		User user = findUserByAppUserID(appUserID);
		if (user != null) {
			return user.getId();
		}

		return 0;
    }

	public int findAppUserID(int userId) {
		try {
			Query q = em.createNativeQuery("SELECT u.appUserID FROM users u where u.id = :userID");
			q.setParameter("userID", userId);

			return (Integer) q.getSingleResult();
		} catch (NoResultException e) {
			return 0;
		}
	}
}