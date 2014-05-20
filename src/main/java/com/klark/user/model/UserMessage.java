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
import javax.persistence.Transient;

import com.klark.common.AbstractEntity;
import com.sun.istack.NotNull;

/*
 +----------------------+--------------+------+-----+---------+----------------+
 | Field                | Type         | Null | Key | Default | Extra          |
 +----------------------+--------------+------+-----+---------+----------------+
 | message_id           | int(11)      | NO   | PRI | NULL    | auto_increment |
 | userid               | int(11)      | NO   |     | NULL    |                |
 | date_entered         | datetime     | NO   |     | NULL    |                |
 | date_of_delivery     | datetime     | YES  |     | NULL    |                |
 | beneficiary_id       | int(11)      | YES  |     | NULL    |                |
 | beneficiary_category | int(11)      | YES  |     | NULL    |                |
 | subject              | varchar(256) | YES  |     | NULL    |                |
 | message_body         | longtext     | YES  |     | NULL    |                |
 +----------------------+--------------+------+-----+---------+----------------+
 */
@Entity
@Table(name = "user_messages")
public class UserMessage extends AbstractEntity {

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @NotNull
    @Column(name = "date_entered")
    private Date dateEntered;

    @Column(name = "date_of_delivery")
    private Date dateOfDelivery;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message_body")
    private String messageBody;

    @Transient
    private Long userId;

    public Long getUserId() {
        return user.getId();
    }

    public void setUserId(Long userId) {
        this.userId = user.getId();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public Date getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(Date dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();

    }

}
