package com.picsauditing.dao;

import java.util.List;

import com.picsauditing.jpa.entities.RulesRow;

public class RulesRowDaoTest extends AbstractDaoTest {
	private RulesRowDAO dao;
	public RulesRowDaoTest() {
		super();
	}
	public void setUp() throws Exception {
		super.setUp();
		dao = new RulesRowDAO();
		dao.setEntityManager(this.em);
	}
	public final void testFind() {
		try {
			RulesRow row = dao.find(1);
			assertEquals("pricing", row.getTableName());
			assertEquals(10, row.getSequence());
			assertEquals("IsTrue", row.getOperator1());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public final void testFindAll() {
		try {
			List<RulesRow> rows = dao.findAll();
			assertTrue(rows.size() > 10);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
