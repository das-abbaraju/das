package com.klark.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klark.common.Encryption;
import com.klark.user.dao.UserDAO;
import com.klark.user.model.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDao;

    @Transactional
    public User addUser(User contact) throws Exception {
        contact.setPassword(Encryption.getInstance().encrypt((contact.getPassword())));
        return userDao.addContact(contact);
    }

    @Transactional
    public List<User> listContact() {
        return userDao.listContact();
    }

    @Transactional
    public User isUserAlreadyExist(String emailId) {
        return userDao.isContactAlreadyExist(emailId);
    }

    @Transactional
    public User authenticated(String userId, String pwd) throws Exception {
        return userDao.authenticated(userId, Encryption.getInstance().encrypt(pwd));
    }

    @Transactional
    public User getUser(Long id) {
        return userDao.getContact(id);
    }

    @Transactional
    public void removeContact(Integer id) {
        userDao.removeContact(id);
    }

    @Transactional
    public User update(User user) throws Exception {
        user.setPassword(Encryption.getInstance().encrypt((user.getPassword())));
        return userDao.update(user);
    }
}
