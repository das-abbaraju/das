// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.dao;

import static org.junit.Assert.assertTrue;

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
import com.klark.user.model.UserMessageBeneficiary;

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
public class UserMessageBeneficiaryDAOIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

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
    protected UserMessageBeneficiaryDAO userMessageBeneficiaryDAO;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddUserMessageBeneficiary() throws Exception {
        User user = userDAO.getContact(2L);
        UserMessageBeneficiary userMessage = new UserMessageBeneficiary();

        userMessage.setBeneficiaryId(1L);
        userMessage.setUser(user);
        userMessage.setBenificiaryCatetory(1);
        userMessage.setBeneficiaryUserId(2L);
        userMessage.setMessageId(3L);
        UserMessageBeneficiary contactExist = userMessageBeneficiaryDAO.addUserBeneficiary(userMessage);

        assertTrue(contactExist.getId() != null);
    }

    @Test
    public void testGetUserMessage() throws Exception {
        User user = userDAO.getContact(2L);
        List<UserMessageBeneficiary> userMsgs = userMessageBeneficiaryDAO.getUserBeneficiarysByUserId(user);
        assertTrue(userMsgs.size() > 0);
        assertTrue(userMsgs.get(0).getUser().getId() != 0);
    }

    @Test
    public void testGetUserMessageById() throws Exception {
        List<UserMessageBeneficiary> userMsgs = userMessageBeneficiaryDAO.getUserBeneficiaryByMessageId(3L);
        assertTrue(userMsgs.size() > 0);
        assertTrue(userMsgs.get(0).getUser().getId() != 0);
    }

    @Test
    public void testCountUserBeneficiary() throws Exception {
        int count = userMessageBeneficiaryDAO.countUserBeneficiary(2L);
        assertTrue(count > 0);
    }

    @Test
    public void testRecievedMsgs() throws Exception {
        int count = userMessageBeneficiaryDAO.countReceivedMessages(2L);
        assertTrue(count > 0);
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
