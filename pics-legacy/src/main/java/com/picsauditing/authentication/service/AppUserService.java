package com.picsauditing.authentication.service;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.security.EncodedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AppUserService {

	@Autowired
	private AppUserDAO appUserDAO;

	public boolean isUserNameAvailable(final String username) {
		return findByUsername(username) == null;
	}

	@Transactional(propagation = Propagation.NESTED)
	public AppUser generateNewAppUser(final String username, final String password) {
		AppUser newAppUser = new AppUser();

		newAppUser.setUsername(username);
		newAppUser = appUserDAO.save(newAppUser);

		String hashSalt = generateHashSalt(newAppUser);
		newAppUser.setHashSalt(hashSalt);
		newAppUser.setPassword(encodePassword(password, hashSalt));

		return appUserDAO.save(newAppUser);
	}

	public AppUser findById(final int appUserId) {
		return appUserDAO.findById(appUserId);
	}

	public AppUser findByUsername(final String username) {
		return appUserDAO.findByUserName(username);
	}

	public AppUser findByUsernameAndUnencodedPassword(final String username, final String password) {
		AppUser appUser = findByUsername(username);
		return appUserDAO.findByUserNameAndPassword(username, encodePassword(password, appUser.getHashSalt()));
	}

    public AppUser encodeAndSavePassword(int appUserID, String password) {
        AppUser appUser = findById(appUserID);
        String hashSalt = generateHashSalt(appUser);
        appUser.setHashSalt(hashSalt);
        appUser.setPassword(encodePassword(password, hashSalt));
        return appUserDAO.save(appUser);
    }

	private String encodePassword(String password, String hashSalt) {
		return EncodedMessage.hash(password + hashSalt);
	}

	private String generateHashSalt(AppUser newAppUser) {
		return Integer.toString(newAppUser.getId());
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(AppUser appUser) {
		appUserDAO.save(appUser);
	}
}
