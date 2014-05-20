// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.model;

import java.util.List;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class UserMessagesDTO {

    private UserMessage userMessage;

    private List<UserMessageBeneficiary> userMessageBeneficiary;

    public List<UserMessageBeneficiary> getUserMessageBeneficiary() {
        return userMessageBeneficiary;
    }

    public void setUserMessageBeneficiary(List<UserMessageBeneficiary> userMessageBeneficiary) {
        this.userMessageBeneficiary = userMessageBeneficiary;
    }

    public UserMessage getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(UserMessage userMessage) {
        this.userMessage = userMessage;
    }

}
