package com.klark.user.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.klark.user.model.User;

@Repository
public class ContactDAOImpl implements ContactDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public User addContact(User contact) {
        contact.setCreated(new Date());
        Long id = (Long) sessionFactory.getCurrentSession().save(contact);
        contact.setId(id);
        return contact;
    }

    @SuppressWarnings("unchecked")
    public List<User> listContact() {
        return sessionFactory.getCurrentSession().createQuery("from User").list();
    }

    public User isContactAlreadyExist(String emailId) {
        @SuppressWarnings("unchecked")
        ArrayList<User> list = (ArrayList<User>) sessionFactory.getCurrentSession().createQuery("from User where email=:emailId").setParameter("emailId", emailId).list();
        return list != null && list.size() > 0 ? list.get(0) : null;

    }

    public User authenticated(String userId, String pwd) {
        @SuppressWarnings("unchecked")
        List<User> list = sessionFactory.getCurrentSession().createQuery("from User where email=:emailId and password=:pwd").setParameter("emailId", userId).setParameter("pwd", pwd).list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public User getContact(Long id) {
        return (User) sessionFactory.getCurrentSession().get(User.class, id);
    }

    public void removeContact(Integer id) {
        User contact = (User) sessionFactory.getCurrentSession().load(User.class, id);
        if (null != contact) {
            sessionFactory.getCurrentSession().delete(contact);
        }
    }
}
