package com.klark.message.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klark.exception.NotFoundException;
import com.klark.message.dao.UserMessageDAO;
import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;
import com.klark.user.model.UserMessageBeneficiary;
import com.klark.user.model.UserMessageDTO;
import com.klark.user.model.UserMessagesDTO;

@Service
public class UserMessageServiceImpl extends BaseEntityServiceImpl<UserMessage, UserMessageDTO> implements UserMessageService {

    @Autowired
    private UserMessageDAO userMessageDAO;

    @Autowired
    private UserMessageBeneficiaryService userMessageBeneficiaryService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Transactional
    public UserMessagesDTO addUserMessage(User user, UserMessagesDTO userMessageDto) {

        UserMessage userMessage = userMessageDAO.addUserMessage(userMessageDto.getUserMessage());

        // adding into relation table
        List<UserMessageBeneficiary> userMessageBeneficiaryList = userMessageDto.getUserMessageBeneficiary();
        if (userMessageBeneficiaryList != null) {
            for (UserMessageBeneficiary userMessageBeneficiary : userMessageBeneficiaryList) {
                addIntoRelMapping(user, userMessage.getId(), userMessageBeneficiary.getBeneficiaryId(), userMessageBeneficiary.getBenificiaryCatetory());

            }
        }
        return userMessageDto;
    }

    @Transactional
    public List<UserMessagesDTO> getUserMessagesDTOByUserId(User user) {
        List<UserMessagesDTO> userMessageDTOList = new ArrayList<UserMessagesDTO>();
        UserMessagesDTO userMessageDTO = new UserMessagesDTO();
        List<UserMessage> userMessages = userMessageDAO.getUserMessagesByUserId(user);
        for (UserMessage userMessage : userMessages) {
            userMessageDTO.setUserMessage(userMessage);
            List<UserMessageBeneficiary> relationList = userMessageBeneficiaryService.getUserBeneficiarysByUserId(user);
            userMessageDTO.setUserMessageBeneficiary(relationList);
            userMessageDTOList.add(userMessageDTO);
        }
        return userMessageDTOList;
    }

    @Transactional
    public List<UserMessageDTO> getUserMessagesByBenificiaryId(Beneficiary beneficiary) {
        List<UserMessage> userMessages = userMessageDAO.getUserMessagesByBenificiarryId(beneficiary);
        List<UserMessageDTO> dtos = new ArrayList<UserMessageDTO>();
        for (UserMessage userMessage : userMessages) {
            dtos.add(toDTO(userMessage, true));
        }
        return dtos;

    }

    @Transactional
    public UserMessage getUserMessageByMessageId(Long messageId) {
        return userMessageDAO.getUserMessageByMessageId(messageId);
    }

    @Transactional
    public int countUserMessages(User userId) {
        return userMessageDAO.countUserMessages(userId);
    }

    @Transactional
    public List<String> getSubjectLines(User user, int fetchCount) {
        return userMessageDAO.getSubjectLines(user, fetchCount);
    }

    private void addIntoRelMapping(User user, Long messageId, Long beneficiaryId, int categoryId) {
        UserMessageBeneficiary userMessageBeneficiary = new UserMessageBeneficiary();
        userMessageBeneficiary.setBeneficiaryId(beneficiaryId);
        userMessageBeneficiary.setBenificiaryCatetory(categoryId);
        userMessageBeneficiary.setMessageId(messageId);
        userMessageBeneficiary.setUser(user);
        if (beneficiaryId != null && beneficiaryId > 0) {
            Beneficiary b = beneficiaryService.getBeneficiaryById(beneficiaryId);
            userMessageBeneficiary.setBeneficiaryUserId(b.getUser().getId());
        }
        userMessageBeneficiaryService.addUserBeneficiary(userMessageBeneficiary);
    }

    @Transactional
    public List<UserMessageDTO> getUserMessagesByUserId(User user) {
        List<UserMessage> userMessages = userMessageDAO.getUserMessagesByUserId(user);
        List<UserMessageDTO> dtos = new ArrayList<UserMessageDTO>();
        for (UserMessage userMessage : userMessages) {
            dtos.add(toDTO(userMessage, true));
        }
        return dtos;

    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public void clearDtoCache() {
        // TODO Auto-generated method stub

    }

}