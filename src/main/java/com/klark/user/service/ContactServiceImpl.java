package com.klark.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klark.common.Encryption;
import com.klark.user.dao.ContactDAO;
import com.klark.user.model.User;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactDAO contactDAO;

    @Transactional
    public User addContact(User contact) throws Exception {
        contact.setPassword(Encryption.getInstance().encrypt((contact.getPassword())));
        return contactDAO.addContact(contact);
    }

    @Transactional
    public List<User> listContact() {

        return contactDAO.listContact();
    }

    @Transactional
    public User isContactAlreadyExist(String emailId) {
        return contactDAO.isContactAlreadyExist(emailId);
    }

    @Transactional
    public User authenticated(String userId, String pwd) throws Exception {
        return contactDAO.authenticated(userId, Encryption.getInstance().encrypt(pwd));
    }

    @Transactional
    public User getContact(Long id) {
        return contactDAO.getContact(id);
    }

    @Transactional
    public void removeContact(Integer id) {
        // TODO Auto-generated method stub

    }
}
