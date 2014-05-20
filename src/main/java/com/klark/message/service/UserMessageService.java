package com.klark.message.service;

import java.util.List;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;
import com.klark.user.model.UserMessageDTO;
import com.klark.user.model.UserMessagesDTO;

public interface UserMessageService {

    UserMessagesDTO addUserMessage(User user, UserMessagesDTO userMessageDto);

    List<UserMessagesDTO> getUserMessagesDTOByUserId(User user);

    List<UserMessageDTO> getUserMessagesByBenificiaryId(Beneficiary beneficiaryId);

    UserMessage getUserMessageByMessageId(Long messageId);

    int countUserMessages(User userId);

    List<String> getSubjectLines(User user, int fetchCount);

    List<UserMessageDTO> getUserMessagesByUserId(User user);

}
