package com.picsauditing.dao;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedKey;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class IdpUserDAO extends PicsDAO {

    private final Logger logger = LoggerFactory.getLogger(IdpUserDAO.class);

    @Transactional(propagation = Propagation.NESTED)
    public IdpUser save(IdpUser o) {
        if (o.getId() == 0) {
            em.persist(o);
        } else {
            o = em.merge(o);
        }
        return o;
    }

    public IdpUser find(int id) {
        return em.find(IdpUser.class, id);
    }

    public IdpUser findBy(String idpUserName, String idp) {
        Query query = em.createQuery("select * from IdpUser i join User u on i.user.id = u.id " +
                "where i.idpUserName=:idpUserName and i.idp=:idp");
        query.setParameter(idpUserName,"idpUserName");
        query.setParameter(idp,"idp");

        return (IdpUser) query.getSingleResult();
    }

}