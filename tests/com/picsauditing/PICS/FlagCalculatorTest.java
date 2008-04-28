package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class FlagCalculatorTest extends TestCase {
	@Autowired
	FlagCalculator2 calculator;
	@Autowired
	AuditDataDAO dao;
	
	@Test
	public void testCalculator() {
		calculator.setDebug(true);
		calculator.runByContractor(2657);
		//calculator.runOne(2657, 1197);
    }
}
