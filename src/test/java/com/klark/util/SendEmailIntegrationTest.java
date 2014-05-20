// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.klark.common.Mail;

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
public class SendEmailIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static ApplicationContext context;

    @BeforeTransaction
    public void beforeTransaction() {
        // testPerson(true, LAST_NAME);
    }

    static {
        context = new ClassPathXmlApplicationContext(new String[] { "classpath:/applicationContextTest.xml", });
    }

    @Test
    public void testSendEmail() throws Exception {
        Mail mail = (Mail) context.getBean("mail");

        SendEmail sendEmail = new SendEmail();
        sendEmail.send("Test", "body test", "dasaradhik@yahoo.com");
    }
}