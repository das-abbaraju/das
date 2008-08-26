package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@Transactional
@SuppressWarnings("unchecked")
public class UserDAO extends PicsDAO {
	public User save(User o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		User row = find(id);
		remove(row);
	}

	public void remove(User row) {
		if (row != null) {
			em.remove(row);
		}
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

	public List<User> findAuditors() {
		List<User> userList = new ArrayList<User>();

		Query query = em.createQuery("FROM User u " + "WHERE u.isActive = 'Yes' " + "AND u.isGroup = 'No' "
				+ "AND u IN (SELECT user FROM UserGroup WHERE group.id = " + User.GROUP_AUDITOR + ") "
				+ "ORDER BY u.name");
		userList.addAll(query.getResultList());

		return userList;
	}

	public boolean checkUserName(String uName, int uID) {
		try {
			User user = findName(uName);
			int id = user.getId();
			if (id == uID || id != 0)
				return true;
			else {
				AccountDAO accountDAO = (AccountDAO) SpringUtils.getBean("AccountDAO");
				id = accountDAO.findByID(uName);
				if (id != 0 || id == uID)
					return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean usernameExists(String username) {
		return checkUserName(username, -1);
	}

	public User findName(String userName) {
		if (userName == null)
			userName = "";
		Query query = em.createQuery("SELECT u FROM User u WHERE username = ?");
		query.setParameter(1, userName);
		return (User) query.getSingleResult();
	}

	public int getUsersCounts() {
		Query query = em.createQuery("SELECT count(u) FROM User u " + "WHERE u.isActive = 'Yes'");
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public List<User> findRecentLoggedOperators() {
		Query query = em.createQuery("SELECT u FROM User u WHERE u.account "
				+ "IN (SELECT o FROM OperatorAccount o)  ORDER BY u.lastLogin DESC");
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<User> findByAccountID(int id, String isActive, String isGroup) {
		String where = "";
		if ("Yes".equals(isGroup) || "No".equals(isGroup))
			where += " AND isGroup = '" + isGroup + "' ";
		
		if ("Yes".equals(isActive) || "No".equals(isActive))
			where += " AND isActive = '" + isActive + "' ";
		
		Query query = em.createQuery("SELECT u FROM User u WHERE account.id = ? " + where +
				" ORDER BY isGroup DESC, name");

		query.setParameter(1, id);
		return query.getResultList();
	}
}
