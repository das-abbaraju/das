package com.klark.user.dao;

import java.util.List;

import com.klark.user.model.User;

public interface ContactDAO {

    User addContact(User contact);

    List<User> listContact();

    void removeContact(Integer id);

    User isContactAlreadyExist(String emailId);

    User authenticated(String userId, String pwd);

    User getContact(Long id);
}
