package com.klark.message.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klark.exception.NotFoundException;
import com.klark.message.dao.UserMessageBeneficiaryDAO;
import com.klark.user.model.Beneficiary;
import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;
import com.klark.user.model.UserMessageBeneficiary;
import com.klark.user.model.UserMessageDTO;

@Service
public class UserMessageBeneficiaryServiceImpl extends BaseEntityServiceImpl<UserMessage, UserMessageDTO> implements UserMessageBeneficiaryService {

    @Autowired
    private UserMessageBeneficiaryDAO userMessageBeneficiaryDAO;

    @Transactional
    public UserMessageBeneficiary addUserBeneficiary(UserMessageBeneficiary userMessage) {
        return userMessageBeneficiaryDAO.addUserBeneficiary(userMessage);
    }

    @Transactional
    public List<UserMessageBeneficiary> getUserBeneficiarysByUserId(User user) {
        return userMessageBeneficiaryDAO.getUserBeneficiarysByUserId(user);
    }

    @Transactional
    public List<UserMessageBeneficiary> getUserBeneficiarysByBenificiarryId(Beneficiary beneficiaryId) {
        return userMessageBeneficiaryDAO.getUserBeneficiarysByBenificiarryId(beneficiaryId.getId());
    }

    @Transactional
    public List<UserMessageBeneficiary> getUserBeneficiaryByMessageId(Long messageId) {
        return userMessageBeneficiaryDAO.getUserBeneficiaryByMessageId(messageId);
    }

    @Transactional
    public int countUserBeneficiary(Long userId) {
        return userMessageBeneficiaryDAO.countUserBeneficiary(userId);
    }

    @Transactional
    public int countReceivedMessages(Long userId) {
        return userMessageBeneficiaryDAO.countReceivedMessages(userId);
    }

    @Transactional
    public List<ReceivedMessage> receivedMessages(Long userId) {
        return userMessageBeneficiaryDAO.getReceivedMessages(userId);
    }

    @Transactional
    public List<ScheduleorUnscheduleMessage> getScheduledMessages(User user) {
        List<ScheduleorUnscheduleMessage> userMessages = userMessageBeneficiaryDAO.getScheduledMessages(user.getId());
        return userMessages;

    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public void clearDtoCache() {
        // TODO Auto-generated method stub

    }

    @Transactional
    public List<ScheduleorUnscheduleMessage> getUnScheduledMessages(User user) {
        List<ScheduleorUnscheduleMessage> userMessages = userMessageBeneficiaryDAO.getUnScheduledMessages(user.getId());
        return userMessages;
    }

}
