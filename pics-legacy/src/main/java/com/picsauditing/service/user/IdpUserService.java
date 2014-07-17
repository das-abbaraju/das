package com.picsauditing.service.user;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.IdpUserDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.user.events.publisher.UserEventPublisher;
import com.picsauditing.service.user.events.publisher.UserEventType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class IdpUserService {
   	@Autowired
	private IdpUserDAO idpUserDAO;

	public IdpUser loadIdpUser(int id) {
		return idpUserDAO.find(id);
	}

	public IdpUser loadIdpUserBy(String idpUserName,String idp) {
        IdpUser idpUser;
		try {
            idpUser = idpUserDAO.findBy(idpUserName,idp);
		} catch (Exception e) {
            idpUser = null;
        }

		return idpUser;
	}

    public void saveUser(IdpUser user) {
        idpUserDAO.save(user);
    }

}
