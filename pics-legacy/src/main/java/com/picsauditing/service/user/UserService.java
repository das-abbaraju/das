package com.picsauditing.service.user;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.user.events.UserEvent;
import com.picsauditing.service.user.events.publisher.UserEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserEventPublisher userEventPublisher;


    public void createUserFor(ContractorAccount contractor, User user) {
        //set up default data
        //validate email
        //app user


    }

    public void persist(User u) {

    }

    private void publish(UserEvent event, User u) {
        //userEventPublisher.publish(UserEvent.Creation, user);
    }
}
