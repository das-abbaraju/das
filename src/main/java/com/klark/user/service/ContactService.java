package com.klark.user.service;

import java.util.List;

import com.klark.user.model.User;

public interface ContactService {

    User addContact(User contact) throws Exception;

    List<User> listContact();

    void removeContact(Integer id);

    User isContactAlreadyExist(String emailId);

    User authenticated(String userId, String pwd) throws Exception;

    User getContact(Long id);
}
