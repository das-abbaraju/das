package com.picsauditing.jpa.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.Column;

@SuppressWarnings("serial")
@Entity
@Table(name = "idp_user")
public class IdpUser extends BaseTable {

    private static final Logger logger = LoggerFactory.getLogger(IdpUser.class);
    private User user;
    private String idpUserName;
    private String idp;


    @ManyToOne
    @JoinColumn(name = "userID", nullable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "idpUserName")
    public String getIdpUserName() {
        return idpUserName;
    }

    public void setIdpUserName(String idpUserName) {
        this.idpUserName = idpUserName;
    }

    @Column(name = "idp")
    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }

}
