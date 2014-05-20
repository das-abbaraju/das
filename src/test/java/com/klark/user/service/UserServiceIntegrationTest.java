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

import com.klark.user.dao.UserDAO;
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
public class UserServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    protected static int SIZE = 2;
    protected static Integer ID = new Integer(1);
    protected static String FIRST_NAME = "Joe";
    protected static String LAST_NAME = "Smith";
    protected static String CHANGED_LAST_NAME = "Jackson";

    @Autowired
    protected UserDAO personDao;

    @Autowired
    protected UserService userService;

    /**
     * Tests that the size and first record match what is expected before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    @Test
    public void testAddUser() throws Exception {
        User userExist = userService.addUser(getUser());
        assertTrue(userExist.getId() != null);
    }

    @Test
    public void testUserAlreadyExist() throws Exception {
        userService.addUser(getUser());
        User userExist = userService.isUserAlreadyExist("test123@g12.com");
        assertTrue(userExist != null);
    }

    private User getUser() {
        User user = new User();
        user.setFirstName("das");
        user.setBirthDate(null);
        user.setEmail("test123@g12.com");
        user.setLastName("last");
        user.setPassword("test1234");
        user.setZipCode("92689");
        return user;
    }

    @Test
    public void testUserUpdate() throws Exception {
        User user = new User();
        user.setFirstName("test");
        user.setEmail("abbarajud6@gmail.com");
        user.setPassword("test123");
        userService.update(user);
        User userExist = userService.isUserAlreadyExist(user.getEmail());
        assertTrue("test".equals(userExist.getFirstName()));
    }

    @Test
    public void testAuthenticate() throws Exception {
        userService.addUser(getUser());
        User userExist = userService.authenticated("test123@g12.com", "test1234");
        assertTrue(userExist != null);
    }

    /**
     * Tests that the size and first record match what is expected after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        // testPerson(false, LAST_NAME);
    }

}
