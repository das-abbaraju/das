// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.dao;

import static org.junit.Assert.assertTrue;

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
import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;
import com.klark.user.model.UserMessage;

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
public class UserMessageDAOIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    protected static int SIZE = 2;
    protected static Integer ID = new Integer(1);
    protected static String FIRST_NAME = "Joe";
    protected static String LAST_NAME = "Smith";
    protected static String CHANGED_LAST_NAME = "Jackson";

    @Autowired
    protected BeneficiaryDAO beneficiaryDAO;

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected UserMessageDAO userMessageDAO;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddUserMessage() throws Exception {
        User user = userDAO.getContact(3L);
        UserMessage userMessage = new UserMessage();
        userMessage.setDateEntered(new Date());
        userMessage.setUser(user);
        userMessage.setSubject("You have new message!");
        userMessage.setMessageBody("messgae body");
        UserMessage contactExist = userMessageDAO.addUserMessage(userMessage);

        assertTrue(contactExist.getId() != null);
    }

    public Beneficiary addBeneficiary() throws Exception {
        User user = userDAO.getContact(2L);
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAddress("Los Angeles");
        beneficiary.setCity("LA");
        beneficiary.setUser(user);
        return beneficiary;
    }

    @Test
    public void testGetUserMessage() throws Exception {
        User user = userDAO.getContact(2L);
        List<UserMessage> userMsgs = userMessageDAO.getUserMessagesByUserId(user);
        assertTrue(userMsgs.size() > 0);
        assertTrue(userMsgs.get(0).getUser().getId() != 0);
    }

    @Test
    public void testGetUserMessageCount() throws Exception {
        User user = userDAO.getContact(2L);
        int count = userMessageDAO.countUserMessages(user);
        assertTrue(count > 0);
    }

    @Test
    public void testGetUserMessageSubjects() throws Exception {
        User user = userDAO.getContact(2L);
        List<String> subjectLines = userMessageDAO.getSubjectLines(user, 3);
        assertTrue(subjectLines.size() == 3);
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
