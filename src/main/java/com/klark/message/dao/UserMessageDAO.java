package com.klark.message.dao;

import java.util.List;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;

public interface UserMessageDAO {

    UserMessage addUserMessage(UserMessage userMessage);

    List<UserMessage> getUserMessagesByUserId(User user);

    List<UserMessage> getUserMessagesByBenificiarryId(Beneficiary beneficiaryId);

    UserMessage getUserMessageByMessageId(Long messageId);

    int countUserMessages(User user);

    List<String> getSubjectLines(User user, int fetchCount);
}
