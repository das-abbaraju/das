package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.User;

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
		Query query = em.createQuery("SELECT u FROM User u " + where + " ORDER BY u.name");
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

	public boolean checkUserName(int uID, String uName) {
		try {
			com.picsauditing.access.User user = new com.picsauditing.access.User();

			int id = user.findID(uName);
			if (id == 0 || id == uID) {
				com.picsauditing.PICS.AccountBean aBean = new com.picsauditing.PICS.AccountBean();
				id = aBean.findID(uName);

				if (id == 0 || id == uID)
					return true;
			}
		} catch (Exception e) {

		}

		return false;
	}

	public User findName(String userName) {
		if (userName == null)
			userName = "";
		Query query = em.createQuery("SELECT u FROM User u WHERE username = " + "'" + userName + "'");
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
}
