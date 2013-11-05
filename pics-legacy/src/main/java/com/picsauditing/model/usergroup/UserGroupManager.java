package com.picsauditing.model.usergroup;

import com.picsauditing.access.Permissions;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/*
    This class and the package it is in could be temporary if we ever split Users and Groups into separate
    classes and/or separate tables. For now, this is to house common functions while presenting a different
    API to consumers of the Model Service Classes.
 */
public class UserGroupManager {
    @Autowired
    protected UserDAO userDAO;
	@Autowired
	protected AppUserDAO appUserDAO;
	@Autowired
	protected AppUserService appUserService;

    protected void resetUserOrGroup(User user) throws Exception {
        userDAO.refresh(user);
    }

    protected User initializeNewUserOrGroup(Account account) {
        User user = new User();
	    saveNewAppUser(user);
        user.setAccount(account);
        user.setActive(true);
        return user;
    }

	protected void saveNewAppUser(User user) {
		String username = user.getUsername();
		JSONObject appUserResponse = appUserService.createNewAppUser(username, "");
		if (appUserResponse != null && "SUCCESS".equals(appUserResponse.get("status").toString())) {
			int appUserID = NumberUtils.toInt(appUserResponse.get("id").toString());
			AppUser appUser = appUserDAO.findByAppUserID(appUserID);
			user.setAppUser(appUser);
		}
	}

    protected void deactivate(User user, Permissions permissions) throws Exception {
        userDAO.refresh(user);
        user.setAuditColumns(permissions);
        user.setActive(false);
        userDAO.save(user);
    }


    protected void delete(User user, Permissions permissions) throws Exception {
        user.setUsername("DELETE-" + user.getId() + "-" + Strings.hashUrlSafe(user.getUsername()));
        user.setAuditColumns(permissions);
        userDAO.save(user);
    }

    protected User saveWithAuditColumnsAndRefresh(User user, Permissions permissions) throws Exception {
        if (user != null) {
            user.setAuditColumns(permissions);
            user = userDAO.save(user);
            userDAO.refresh(user);
        }
        return user;
    }

}
