package com.klark.message.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;
import com.klark.user.model.UserMessageBeneficiary;

@Repository
public class UserMessageBeneficiaryDAOImpl implements UserMessageBeneficiaryDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserMessageDAO userMessageDAO;

    public UserMessageBeneficiary addUserBeneficiary(UserMessageBeneficiary userBeneficiary) {
        Long id = (Long) sessionFactory.getCurrentSession().save(userBeneficiary);
        userBeneficiary.setId(id);
        return userBeneficiary;

    }

    public List<UserMessageBeneficiary> getUserBeneficiaryByMessageId(Long messageId) {
        @SuppressWarnings("unchecked")
        List<UserMessageBeneficiary> list = sessionFactory.getCurrentSession().createQuery("from UserMessageBeneficiary where messageId=:messageId").setParameter("messageId", messageId).list();
        return list;
    }

    public int countUserBeneficiary(Long userId) {
        int count = ((Long) sessionFactory.getCurrentSession().createQuery("select distinct count(beneficiaryId) from UserMessageBeneficiary where userid=:userId").setParameter("userId", userId)
                .uniqueResult()).intValue();
        return count;
    }

    public int countReceivedMessages(Long userId) {
        int count = ((Long) sessionFactory.getCurrentSession().createQuery("select distinct count(beneficiaryUserId) from UserMessageBeneficiary where userid=:userId").setParameter("userId", userId)
                .uniqueResult()).intValue();
        return count;
    }

    public List<ReceivedMessage> getReceivedMessages(Long userId) {
        @SuppressWarnings("unchecked")
        org.hibernate.Query query = sessionFactory.getCurrentSession().getNamedQuery("receivedMsgs").setParameter("userId", userId);
        List<ReceivedMessage> userMessages = query.list();
        return userMessages;
    }

    public List<UserMessageBeneficiary> getUserBeneficiarysByUserId(User user) {
        @SuppressWarnings("unchecked")
        List<UserMessageBeneficiary> list = sessionFactory.getCurrentSession().createQuery("from UserMessageBeneficiary where user=:user").setParameter("user", user).list();
        return list;
    }

    public List<UserMessageBeneficiary> getUserBeneficiarysByBenificiarryId(Long beneficiary) {
        @SuppressWarnings("unchecked")
        List<UserMessageBeneficiary> list = sessionFactory.getCurrentSession().createQuery("from UserMessageBeneficiary where benificiaryId=:benificiary").setParameter("beneficiary", beneficiary)
                .list();
        return list;
    }

    public List<UserMessageBeneficiary> getUserBeneficiarysByUserId(Long userId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ScheduleorUnscheduleMessage> getScheduledMessages(Long userId) {
        @SuppressWarnings("unchecked")
        org.hibernate.Query query = sessionFactory.getCurrentSession().getNamedQuery("scheduledMsgs").setParameter("userId", userId);
        return query.list();
    }

    public List<ScheduleorUnscheduleMessage> getUnScheduledMessages(Long userId) {
        @SuppressWarnings("unchecked")
        org.hibernate.Query query = sessionFactory.getCurrentSession().getNamedQuery("unScheduledMsgs").setParameter("userId", userId);
        return query.list();

    }
}
