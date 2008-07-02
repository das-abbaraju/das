package com.picsauditing.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditCategoryDataTestDAO {
	@Autowired
	private AuditCategoryDataDAO dao;

	@Test
	public final void testFind() {

	}

}
