package com.picsauditing.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.picsauditing.jpa.entities.Naics;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tests.xml" })
@TransactionConfiguration(transactionManager = "transactionManager")
public class NaicsDAOTest {
	@Autowired
	NaicsDAO naicsDAO;
	private Naics naics;

	@Before
	public void setUp() throws Exception {
	}

	/*
	 * The 2008 data has this for TRIR:
	 * 5          (no record)
	 * 52         0.90
	 * 522        (no record)
	 * 5221       (no record)
	 * 52212      0.00
	 * So, asking for the parent of 52212 should return 52.  And, asking for the parent of 52 should return null.
	 */
	@Test
	public void testFindParent() throws Exception {
		naics = naicsDAO.findParent("52212");
		assertEquals("52", naics.getCode());
		assertNull( naicsDAO.findParent("52"));
	}
	
	/*
	 * The 2008 data has this for TRIR:
	 * 52         0.90
	 * 522        (no record)
	 * 5221       (no record)
	 * 52212      0.00
	 * So, asking for the TRIR for 52212 should "walk the tree" up to 52, to find the first non-zero value.
	 */
	@Test
	public void testGetBroaderNaicsForTrir() {
		naics = naicsDAO.find("52212");
		assertEquals(0.0, naics.getTrir(), 0.01);
		naics = naicsDAO.getBroaderNaics(false,naics);
		assertEquals("52", naics.getCode());
		assertEquals(0.9, naics.getTrir(), 0.01);
	}

	/*
	 * The 2008 data has this for LWCR:
	 * 541        0.50
	 * 5412       0.00
	 * 54121      0.00
	 * So, asking for the LWCR for 54121 should "walk the tree" through 5412 up to 541, to find the first non-zero value.
	 */
	@Test
	public void testGetBroaderNaicsForLcwr() {
		naics = naicsDAO.find("54121");
		assertEquals(0.0, naics.getLwcr(), 0.01);
		naics = naicsDAO.getBroaderNaics(true,naics);
		assertEquals("541", naics.getCode());
		assertEquals(0.5, naics.getLwcr(), 0.01);
	}

	/*
	 * The 2008 data has this for DART:
	 * 541        0.50
	 * 5412       0.00
	 * 54121      0.00
	 * So, asking for the DART for 54121 should "walk the tree" through 5412 up to 541, to find the first non-zero value.
	 */
	@Test
	public void testGetBroaderNaicsForDart() {
		naics = naicsDAO.find("54121");
		// FIXME Currenty, we actually have DART data loaded in the LWCR column, so we're just using LWCR for both.
		// assertEquals(0.0, naics.getDart(), 0.01);
		assertEquals(0.0, naics.getLwcr(), 0.01);
		naics = naicsDAO.getBroaderNaicsForDart(naics);
		assertEquals("541", naics.getCode());
		// FIXME Currenty, we actually have DART data loaded in the LWCR column, so we're just using LWCR for both.
		// assertEquals(0.5, naics.getDart(), 0.01);
		assertEquals(0.5, naics.getLwcr(), 0.01);
	}

}
