package com.picsauditing.access;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.util.Date;

@SuppressWarnings("serial")
public class UserService {

	@Autowired
	protected UserDAO userDAO;

	public User loadUserByUsername(String username) {
		User user;
		try {
			user = userDAO.findName(username);
		} catch (NoResultException e) {
			user = null;
		}
		return user;
	}

	public boolean isUserActive(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null.");
		}

		Account account = user.getAccount();
		if (account.isOperatorCorporate()) {
			if (!account.getStatus().isActiveOrDemo()) {
				return false;
			}
		}
		if (account.isContractor() && account.getStatus().isDeleted()) {
			return false;
		}
		return user.getIsActive() == YesNo.Yes;
	}

	public boolean isPasswordExpired(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null.");
		}

		PasswordSecurityLevel passwordSecurityLevel = user.getAccount().getPasswordSecurityLevel();
		Date passwordLastChangedDate = user.getPasswordChanged();

		if (passwordLastChangedDate != null && passwordSecurityLevel.enforcePasswordExpiration()) {
			if (DateBean.isMoreThanXMonthsAgo(passwordLastChangedDate, passwordSecurityLevel.expirationMonths)) {
				return true;
			}
		}
		return false;
	}

}
