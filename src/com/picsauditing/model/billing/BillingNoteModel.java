package com.picsauditing.model.billing;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class BillingNoteModel {

	@Autowired
	private UserDAO userDAO;

	public User findUserForPaymentNote(Permissions permissions) {
		if (permissions == null) {
			throw new IllegalArgumentException("Permissions object cannot be null.");
		}

		if (isSwitchedToUser(permissions)) {
			return userDAO.find(permissions.getAdminID());
		}

		return userDAO.find(permissions.getUserId());
	}

	private boolean isSwitchedToUser(Permissions permissions) {
		return permissions.getUserId() != permissions.getAdminID() && permissions.getAdminID() > 0;
	}

}
