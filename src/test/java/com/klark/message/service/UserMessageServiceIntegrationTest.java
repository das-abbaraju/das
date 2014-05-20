// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.klark.user.dao.UserDAO;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;
import com.klark.user.model.UserMessageBeneficiary;
import com.klark.user.model.UserMessagesDTO;

/**
 * Description here!
 * 
 * 
 * @author
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContextTest.xml")
@TransactionConfiguration
@Transactional
public class UserMessageServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    protected static int SIZE = 2;
    protected static Integer ID = new Integer(1);
    protected static String FIRST_NAME = "Joe";
    protected static String LAST_NAME = "Smith";
    protected static String CHANGED_LAST_NAME = "Jackson";

    @Autowired
    protected UserDAO userDao;

    @Autowired
    protected UserMessageService userMessageService;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddUserMessage() throws Exception {
        UserMessagesDTO userMessageDTO = new UserMessagesDTO();

        User user = userDao.getContact(3L);

        UserMessage userMessage = new UserMessage();
        userMessage.setDateEntered(new Date());
        userMessage.setUser(user);
        userMessage.setSubject("You have new message!");
        userMessage.setMessageBody("messgae body");

        UserMessageBeneficiary userMessageB = new UserMessageBeneficiary();
        ArrayList<UserMessageBeneficiary> userMessageBList = new ArrayList<UserMessageBeneficiary>();

        userMessageDTO.setUserMessage(userMessage);

        userMessageB.setBeneficiaryId(1L);
        userMessageB.setUser(user);
        userMessageB.setBenificiaryCatetory(1);
        userMessageB.setBeneficiaryUserId(2L);
        userMessageB.setMessageId(3L);
        userMessageBList.add(userMessageB);
        userMessageDTO.setUserMessageBeneficiary(userMessageBList);
        UserMessagesDTO dto = userMessageService.addUserMessage(user, userMessageDTO);

        assertTrue(dto.getUserMessage().getId() > 0);
        // assertTrue(dto.getUserMessageBeneficiary().get(0).getId() > 0);

    }

    @Test
    public void tesGetUserMessage() throws Exception {
        User user = userDao.getContact(2L);

        List<UserMessagesDTO> dtoList = userMessageService.getUserMessagesDTOByUserId(user);
        for (UserMessagesDTO userMessageDTO : dtoList) {
            assertTrue(userMessageDTO.getUserMessage().getId() != null);
            List<UserMessageBeneficiary> relList = userMessageDTO.getUserMessageBeneficiary();
            for (UserMessageBeneficiary rel : relList) {
                assertTrue(rel.getId() != null);

            }
        }
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
