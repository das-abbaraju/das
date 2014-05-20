package com.klark.message.dao;

import java.util.List;

import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;
import com.klark.user.model.UserMessageBeneficiary;

public interface UserMessageBeneficiaryDAO {

    UserMessageBeneficiary addUserBeneficiary(UserMessageBeneficiary userMessage);

    List<UserMessageBeneficiary> getUserBeneficiarysByUserId(User user);

    List<UserMessageBeneficiary> getUserBeneficiarysByBenificiarryId(Long beneficiaryId);

    List<UserMessageBeneficiary> getUserBeneficiaryByMessageId(Long messageId);

    int countUserBeneficiary(Long userId);

    int countReceivedMessages(Long userId);

    List<ReceivedMessage> getReceivedMessages(Long userId);

    List<ScheduleorUnscheduleMessage> getScheduledMessages(Long userId);

    List<ScheduleorUnscheduleMessage> getUnScheduledMessages(Long userId);
}
