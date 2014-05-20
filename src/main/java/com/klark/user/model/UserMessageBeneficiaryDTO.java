// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.model;

import com.klark.common.AbstractEntityDTO;

/**
 * Description here!
 * 
 * 
 * @author
 */
public class UserMessageBeneficiaryDTO extends AbstractEntityDTO<UserMessage> {

    /**
     * 
     */
    private static final long serialVersionUID = 666661L;

    private Long id;

    private int benificiaryCatetory;

    private Long messageId;

    private Long beneficiaryId;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long beneficiaryUserId;

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

    @Override
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
