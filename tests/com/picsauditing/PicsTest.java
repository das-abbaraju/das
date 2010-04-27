package com.picsauditing;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class PicsTest extends TestCase {
	@BeforeClass
	static public void before() {
		System.setProperty("pics.ftpDir", "P:\\Development\\ftp_dir");
	}
}
