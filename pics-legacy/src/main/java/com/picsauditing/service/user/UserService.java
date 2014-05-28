package com.picsauditing.service.user;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.service.user.events.publisher.UserEventPublisher;
import com.picsauditing.service.user.events.publisher.UserEventType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.util.Date;

public class UserService {

    @Autowired
    private AppUserService appUserService;
   	@Autowired
	private UserDAO userDAO;
	@Autowired
	private UserEventPublisher userEventPublisher;

	public void persist(User u) {
		userDAO.save(u);

		if (u.getId() == 0) {
			userDAO.refresh(u);
			publish(u, UserEventType.Creation);
		}

	}

	private void publish(User u, UserEventType event) {
		userEventPublisher.publish(u, event);
	}

	public User loadUser(int userId) {
		return userDAO.find(userId);
	}

	public User loadUserByUsername(String username) {
		User user;
		try {
			user = userDAO.findUserByAppUserID(appUserService.findByUsername(username).getId());
		} catch (Exception e) {
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

	public User findByAppUserId(int appUserId) {
		return userDAO.findUserByAppUserID(appUserId);
	}

	public User findById(final int userId) {
		return userDAO.find(userId);
	}

    public void saveUser(User user) {
        userDAO.save(user);
    }

    public void updateUserForSuccessfulLogin(User user) {
        user.unlockLogin();
        user.setLastLogin(new Date());
        saveUser(user);
    }

    public User findByName(String username) {
        return userDAO.findName(username);
    }
}
