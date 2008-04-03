package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/tests.xml")
public class UserDaoTest extends TestCase {
	
	@Autowired
	protected UserDAO dao;

	@Test
	public final void testFind() {
		try {
			User row = dao.find(1);
			assertEquals("PQF", row.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public final void testFindAll() {
		try {
			List<User> rows = dao.findWhere("username LIKE 'tallred'");
			assertTrue(rows.size() > 5);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
