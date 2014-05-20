// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.service;

import static org.junit.Assert.assertTrue;

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

import com.klark.user.dao.ContactDAO;
import com.klark.user.model.User;

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
public class ContactServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    protected static int SIZE = 2;
    protected static Integer ID = new Integer(1);
    protected static String FIRST_NAME = "Joe";
    protected static String LAST_NAME = "Smith";
    protected static String CHANGED_LAST_NAME = "Jackson";

    @Autowired
    protected ContactDAO personDao;

    @Autowired
    protected ContactService contactService;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddUser() throws Exception {
        User contact = new User();
        contact.setFirstname("das");
        contact.setBirthDate(null);
        contact.setEmail("test123@g12.com");
        contact.setLastname("last");
        contact.setPassword("test1234");
        contact.setZipcode("92689");
        User contactExist = contactService.addContact(contact);
        assertTrue(contactExist.getId() != null);
    }

    @Test
    public void testUserAlreadyExist() {
        User contactExist = contactService.isContactAlreadyExist("test123@g12.com");
        assertTrue(contactExist != null);
    }

    @Test
    public void testAuthenticate() throws Exception {
        User contactExist = contactService.authenticated("test123@g12.com", "test1234");
        assertTrue(contactExist != null);
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
