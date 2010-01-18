package com.picsauditing.actions;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContactInfo extends AccountActionSupport {
	private int userid;
	private User user;
	private UserDAO userDAO;

	public ContactInfo(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public String execute() throws Exception {
		try {
			user = userDAO.find(userid);
			if (user == null)
				throw new Exception("No such user exists");
		} catch (Exception e) {
			addActionError("No such user exists");
		}
		return SUCCESS;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}