// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.klark.common.AbstractEntity;

/**
 * Description here!
 * 
 * 
 * @author
 */

@NamedNativeQueries({
        @NamedNativeQuery(name = "receivedMsgs", query = "select * from user_messages a inner join users_to_beneficiaries b on a.message_id=b.message_id LEFT OUTER JOIN users u on u.userid=b.beneficiary_user_id where  b.beneficiary_user_id=:userId", resultClass = ReceivedMessage.class),
        @NamedNativeQuery(name = "scheduledMsgs", query = "select * from user_messages a inner join users_to_beneficiaries b on a.message_id=b.message_id LEFT OUTER JOIN users u on u.userid=b.beneficiary_user_id where b.beneficiary_id is not null  and a.userid=:userId", resultClass = ScheduleorUnscheduleMessage.class),
        @NamedNativeQuery(name = "unScheduledMsgs", query = "select * from user_messages a inner join users_to_beneficiaries b on a.message_id=b.message_id LEFT OUTER JOIN users u on u.userid=b.beneficiary_user_id where a.userid=:userId", resultClass = ScheduleorUnscheduleMessage.class) })
@Entity
@Table(name = "users_to_beneficiaries")
public class UserMessageBeneficiary extends AbstractEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 666661L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "beneficiary_category")
    private int benificiaryCatetory;

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = true)
    private User user;

    @Column(name = "beneficiary_user_id")
    private Long beneficiaryUserId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getBeneficiaryUserId() {
        return beneficiaryUserId;
    }

    public void setBeneficiaryUserId(Long beneficiaryUserId) {
        this.beneficiaryUserId = beneficiaryUserId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBenificiaryCatetory() {
        return benificiaryCatetory;
    }

    public void setBenificiaryCatetory(int benificiaryCatetory) {
        this.benificiaryCatetory = benificiaryCatetory;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

}
