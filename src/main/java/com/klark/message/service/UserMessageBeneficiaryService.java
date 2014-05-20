// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import java.util.List;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;
import com.klark.user.model.UserMessageBeneficiary;

/**
 * Description here!
 * 
 * 
 * @author
 */

public interface UserMessageBeneficiaryService {

    UserMessageBeneficiary addUserBeneficiary(UserMessageBeneficiary userMessage);

    List<UserMessageBeneficiary> getUserBeneficiarysByUserId(User user);

    List<UserMessageBeneficiary> getUserBeneficiarysByBenificiarryId(Beneficiary beneficiaryId);

    List<UserMessageBeneficiary> getUserBeneficiaryByMessageId(Long messageId);

    int countUserBeneficiary(Long userId);

    int countReceivedMessages(Long userId);

    List<ReceivedMessage> receivedMessages(Long userId);

    List<ScheduleorUnscheduleMessage> getScheduledMessages(User user);

    List<ScheduleorUnscheduleMessage> getUnScheduledMessages(User user);

}
