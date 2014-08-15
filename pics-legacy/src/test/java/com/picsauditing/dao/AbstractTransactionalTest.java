package com.picsauditing.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@TestExecutionListeners({TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = {
        "/ApplicationContextTest.xml"
})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class AbstractTransactionalTest {
}
