package com.picsauditing.PICS;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
public class AuditBuilderTest extends TestCase {
	@Autowired
	private AuditBuilder builder;

	@Test
	public void build() {
		try {
			builder.buildAudits(1784);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
