package com.picsauditing.dao;

import java.util.List;

import javax.persistence.EntityTransaction;

import com.picsauditing.jpa.entities.AuditType;

public class AuditTypeDaoTest extends AbstractDaoTest {
	private AuditTypeDAO dao;
	private EntityTransaction tx;
	public AuditTypeDaoTest() {
		super();
	}
	public void setUp() throws Exception {
		super.setUp();
		dao = new AuditTypeDAO();
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
			AuditType row = dao.find(1);
			assertEquals("PQF", row.getAuditName());
			assertEquals(false, row.isHasMultiple());
			assertEquals(false, row.isScheduled());
			assertEquals(false, row.isHasAuditor());
			assertEquals("2009-03-01", row.getDateToExpire().toString());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public final void testFindAll() {
		try {
			List<AuditType> rows = dao.findAll();
			assertTrue(rows.size() > 5);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public final void testUpdate() {
		try {
			AuditType row = dao.find(1);
			String name = "PQF";
			row.setAuditName("PQF");
			row = dao.save(row);
			tx.commit();
			row = dao.find(1);
			assertEquals("PQFFoo", row.getAuditName());
			row.setAuditName("PQF");
			row = dao.save(row);
			tx.commit();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public final void testInsert() {
		try {
			AuditType row = new AuditType();
			row.setAuditName("JUnit Test");
			row.setDescription("this is a test");
			row = dao.save(row);
			tx.commit();
			assertTrue(row.getAuditTypeID() > 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
