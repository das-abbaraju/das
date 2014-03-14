package com.picsauditing.service.user;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.user.events.publisher.UserEventPublisher;
import com.picsauditing.service.user.events.publisher.UserEventType;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
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
}
