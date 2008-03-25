package com.picsauditing.dao;

import java.util.List;
import javax.persistence.EntityTransaction;
import com.picsauditing.jpa.entities.User;

public class UserDaoTest extends AbstractDaoTest {
	private UserDAO dao;
	private EntityTransaction tx;
	public UserDaoTest() {
		super();
	}
	public void setUp() throws Exception {
		super.setUp();
		dao = new UserDAO();
		dao.setEntityManager(this.em);
		tx = dao.em.getTransaction();
		tx.begin();
	}
	public void tearDown() {
		tx.rollback();
		tx = null;
		dao = null;
		super.tearDown();
	}
	public final void testFind() {
		try {
			User row = dao.find(1);
			assertEquals("PQF", row.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public final void testFindAll() {
		try {
			List<User> rows = dao.findWhere("username LIKE 'tallred'");
			assertTrue(rows.size() > 5);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
