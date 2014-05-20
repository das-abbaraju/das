package com.klark.user.service;

import java.util.List;

import com.klark.user.model.User;

public interface UserService {

    User addUser(User contact) throws Exception;

    List<User> listContact();

    void removeContact(Integer id);

    User isUserAlreadyExist(String emailId);

    User authenticated(String userId, String pwd) throws Exception;

    User getUser(Long id);

    User update(User user) throws Exception;
}
