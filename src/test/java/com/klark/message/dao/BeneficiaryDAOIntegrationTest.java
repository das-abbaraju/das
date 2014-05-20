// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.dao;

import static org.junit.Assert.assertTrue;

import java.util.Date;

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
public class BeneficiaryDAOIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

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
    protected UserMessageDAO userMsgDAO;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddBeneficiary() throws Exception {
        User user = userDAO.getContact(2L);
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAddress("Los Angeles");
        beneficiary.setCity("LA");
        beneficiary.setUser(user);
        Beneficiary contactExist = beneficiaryDAO.addBeneficiary(beneficiary);
        assertTrue(contactExist.getId() != null);
    }

    public UserMessage addUserMessage() throws Exception {
        User user = userDAO.getContact(2L);

        UserMessage usermsg = new UserMessage();
        usermsg.setDateEntered(new Date());
        // usermsg.setBenificiary(beneficiary);
        usermsg.setUser(user);
        usermsg.setSubject("You have new message!");
        usermsg.setMessageBody("messgae body");

        return userMsgDAO.addUserMessage(usermsg);
    }

    @Test
    public void testGetBeneficiary() throws Exception {
        Beneficiary b = beneficiaryDAO.getBeneficiaryById(1L);
        assertTrue(b.getId() != null);
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
