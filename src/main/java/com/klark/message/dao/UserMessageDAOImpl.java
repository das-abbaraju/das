package com.klark.message.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;

@Repository
public class UserMessageDAOImpl implements UserMessageDAO {

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

    public UserMessage addUserMessage(UserMessage userMessage) {
        userMessage.setDateEntered(new Date());
        Long id = (Long) sessionFactory.getCurrentSession().save(userMessage);
        userMessage.setId(id);
        return userMessage;

    }

    public UserMessage getUserMessageByMessageId(Long messageId) {
        return (UserMessage) sessionFactory.getCurrentSession().get(UserMessage.class, messageId);
    }

    public int countUserMessages(User user) {

        int count = ((Long) sessionFactory.getCurrentSession().createQuery("select distinct count(id) from UserMessage where userid=:userId").setParameter("userId", user).uniqueResult()).intValue();
        return count;
    }

    public List<UserMessage> getUserMessagesByUserId(User user) {
        @SuppressWarnings("unchecked")
        List<UserMessage> list = sessionFactory.getCurrentSession().createQuery("from UserMessage where user=:user").setParameter("user", user).list();
        return list;
    }

    public List<UserMessage> getUserMessagesByBenificiarryId(Beneficiary beneficiary) {
        @SuppressWarnings("unchecked")
        List<UserMessage> list = sessionFactory.getCurrentSession().createQuery("from UserMessage where benificiary=:benificiary").setParameter("beneficiary", beneficiary).list();
        return list;
    }

    public List<UserMessage> getUserMessagesByUserId(Long userId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getSubjectLines(User user, int fetchCount) {
        @SuppressWarnings("unchecked")
        List<String> list = sessionFactory.getCurrentSession().createQuery("select subject from UserMessage where user=:user order by date_entered desc").setParameter("user", user)
                .setMaxResults(fetchCount).list();
        return list;

    }
}
