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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class IdpUser extends BaseTable {
    private static final Logger logger = LoggerFactory.getLogger(IdpUser.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private User user;

    private String idpUserName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "idpusername", length = 255, nullable = false)
    public String getIdpUserName() {
        return idpUserName;
    }

    public void setIdpUserName(String idpUserName) {
        this.idpUserName = idpUserName;
    }
}
