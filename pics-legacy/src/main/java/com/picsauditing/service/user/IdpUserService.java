package com.picsauditing.service.user;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.IdpUserDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.user.events.publisher.UserEventPublisher;
import com.picsauditing.service.user.events.publisher.UserEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class IdpUserService {

    @Autowired
    private IdpUserDAO idpUserDAO;

    private static final Logger logger = LoggerFactory.getLogger(IdpUserService.class);

    public IdpUser loadIdpUser(int id) {
        return idpUserDAO.find(id);
    }

    public IdpUser loadIdpUserBy(String idpUserName, String idp) {
        return idpUserDAO.findBy(idpUserName, idp);
    }

    public void saveIdpUser(IdpUser user) {
        idpUserDAO.save(user);
    }
}
