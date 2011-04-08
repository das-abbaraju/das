package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditTypeDaoTest extends TestCase {
	@Autowired
	private AuditTypeDAO dao;

	@Test
	public final void testFind() {
		AuditType row = dao.find(3);
		assertEquals("Office Audit", row.getName().toString());
		assertEquals(false, row.isHasMultiple());
		assertEquals(true, row.isScheduled());
		// assertEquals(null, row.getDateToExpire());
		// assertEquals(36, (int) row.getMonthsToExpire());
		// assertEquals("2009-03-01", row.getDateToExpire().toString());

	}

	@Test
	public final void testFindAll() {
		try {
			List<AuditType> rows = dao.findAll();
			assertTrue(rows.size() > 5);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	// @Test
	public final void testUpdate() {
		try {
//			AuditType row = dao.find(1);
//			String name = "PQF";
//			row.setAuditName(name);
//			row = dao.save(row);
//			row = dao.find(1);
//			assertEquals("PQFFoo", row.getAuditName());
//			row.setAuditName(name);
//			row = dao.save(row);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public final void testInsert() {
		try {
			AuditType row = new AuditType();
			//row.setName("JUnit Test");
			row.setDescription("this is a test");
			row = dao.save(row);
			assertTrue(row.getId() > 0);
			dao.remove(row.getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
