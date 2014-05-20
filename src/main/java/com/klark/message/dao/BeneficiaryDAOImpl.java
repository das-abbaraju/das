package com.klark.message.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;

@Repository
public class BeneficiaryDAOImpl implements BeneficiaryDAO {

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

    public Beneficiary addBeneficiary(Beneficiary Beneficiary) {
        Long id = (Long) sessionFactory.getCurrentSession().save(Beneficiary);
        Beneficiary.setId(id);
        return Beneficiary;

    }

    public List<Beneficiary> getBeneficiaryByMessageId(Long messageId) {
        @SuppressWarnings("unchecked")
        List<Beneficiary> list = sessionFactory.getCurrentSession().createQuery("from Beneficiary where id=:id").setParameter("id", messageId).list();
        return list;
    }

    public int countBeneficiarys(Long userId) {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<Beneficiary> getBeneficiarysByUserId(User user) {
        @SuppressWarnings("unchecked")
        List<Beneficiary> list = sessionFactory.getCurrentSession().createQuery("from Beneficiary where user=:user").setParameter("user", user).list();
        return list;
    }

    public List<Beneficiary> getBeneficiarysByBenificiarryId(Long beneficiaryId) {
        @SuppressWarnings("unchecked")
        List<Beneficiary> list = sessionFactory.getCurrentSession().createQuery("from Beneficiary where id=:beneficiaryId").setParameter("beneficiary", beneficiaryId).list();
        return list;
    }

    public List<Beneficiary> getBeneficiarysByUserId(Long userId) {
        return null;
    }

    public List<Beneficiary> getBeneficiariesByUserId(User user) {
        @SuppressWarnings("unchecked")
        List<Beneficiary> list = sessionFactory.getCurrentSession().createQuery("from Beneficiary where user=:user").setParameter("user", user).list();
        return list;
    }

    public List<Beneficiary> getBeneficiariesByBenificiarryId(Beneficiary beneficiaryId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Beneficiary> getBeneficiariesByMessageId(Long messageId) {
        // TODO Auto-generated method stub
        return null;
    }

    public int countBeneficiaries(Long userId) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Beneficiary getBeneficiaryById(Long beneficiaryId) {
        return (Beneficiary) sessionFactory.getCurrentSession().get(Beneficiary.class, beneficiaryId);
    }

    public List<Beneficiary> getBeneficiariesByBenificiaryId(Long beneficiaryId) {
        // TODO Auto-generated method stub
        return null;
    }
}
