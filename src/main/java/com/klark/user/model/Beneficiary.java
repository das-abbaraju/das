// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.klark.common.AbstractEntity;
import com.klark.common.IgnoreFieldTransform;
import com.sun.istack.NotNull;

/**
 * Description here!
 * 
 * 
 * @author
 */

/*
 * +---------------------+--------------+------+-----+---------+----------------+ | Field | Type |
 * Null | Key | Default | Extra |
 * +---------------------+--------------+------+-----+---------+----------------+ | id | int(11) |
 * NO | PRI | NULL | auto_increment | | beneficiary_user_id | int(11) | YES | | NULL | | | firstname
 * | varchar(256) | YES | | NULL | | | lastname | varchar(256) | YES | | NULL | | | middlename |
 * varchar(256) | YES | | NULL | | | primary_photo | blob | YES | | NULL | | | email | varchar(100)
 * | YES | | NULL | | | address | varchar(256) | YES | | NULL | | | city | varchar(256) | YES | |
 * NULL | | | state | varchar(256) | YES | | NULL | | | zipcode | varchar(10) | YES | | NULL | | |
 * home_phone | varchar(20) | YES | | NULL | | | mobile_phone | varchar(20) | YES | | NULL | |
 * +---------------------+--------------+------+-----+---------+----------------+
 */

@Entity
@Table(name = "beneficiaries")
public class Beneficiary extends AbstractEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "beneficiary_user_id", nullable = true)
    @IgnoreFieldTransform
    private User user;

    @NotNull
    @Column(name = "firstname")
    private String firstName;

    @Column(name = "middlename")
    private String middleName;

    @Column(name = "primary_photo")
    private String primaryPhoto;

    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "home_phone")
    private String homePhone;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "zipcode")
    private Date zipcode;

    @Column(name = "thumb_url")
    private String thumbURL;

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPrimaryPhoto() {
        return primaryPhoto;
    }

    public void setPrimaryPhoto(String primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Date getZipcode() {
        return zipcode;
    }

    public void setZipcode(Date zipcode) {
        this.zipcode = zipcode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}