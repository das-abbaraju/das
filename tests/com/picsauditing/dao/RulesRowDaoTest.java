package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.RulesRow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/tests.xml")
public class RulesRowDaoTest extends TestCase {
	
	@Autowired 
	private RulesRowDAO dao;

	@Test
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

	@Test
	public final void testFindAll() {
		try {
			List<RulesRow> rows = dao.findAll();
			assertTrue(rows.size() > 10);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
