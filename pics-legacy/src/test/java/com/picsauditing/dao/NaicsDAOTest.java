package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.Naics;

public class NaicsDAOTest {
	private NaicsDAO naicsDAO;

	@Mock
	private EntityManager em;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		naicsDAO = new NaicsDAO();
		naicsDAO.setEntityManager(em);
		
		List<Naics> naicsList = new ArrayList<Naics>();
		naicsList.add(constructNaics("52", 0.9, 0.8, 0.0));
		naicsList.add(constructNaics("52212", 0.0, 0.0, 0.0));
		
		naicsList.add(constructNaics("541", 0.0, 0.50, 0.4));
		naicsList.add(constructNaics("5412", 0.0, 0.0, 0.0));
		naicsList.add(constructNaics("54121", 0.0, 0.0, 0.0));

		for (Naics naics : naicsList) {
			when(em.find(Naics.class, naics.getCode())).thenReturn(naics);
		}
	}

	private Naics constructNaics(String code, double trir, double lwcr, double dart) {
		Naics naics = new Naics();
		naics.setCode(code);
		naics.setTrir((float) trir);
		naics.setLwcr((float) lwcr);
		naics.setDart((float) dart);
		return naics;
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
		Naics naics = Whitebox.invokeMethod(naicsDAO, "findParent", "52212");
		assertEquals("52", naics.getCode());
		
		Naics anotherParentSearch = Whitebox.invokeMethod(naicsDAO, "findParent", "52");
		assertNull(anotherParentSearch);
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
		Naics naics = naicsDAO.find("52212");
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
		Naics naics = naicsDAO.find("54121");
		assertEquals(0.0, naics.getLwcr(), 0.01);
		naics = naicsDAO.getBroaderNaics(true,naics);
		assertEquals("541", naics.getCode());
		assertEquals(0.5, naics.getLwcr(), 0.01);
	}

	/*
	 * The 2008 data has this for DART:
	 * 541        0.40
	 * 5412       0.00
	 * 54121      0.00
	 * So, asking for the DART for 54121 should "walk the tree" through 5412 up to 541, to find the first non-zero value.
	 */
	@Test
	public void testGetBroaderNaicsForDart() throws Exception {
		Naics naics = naicsDAO.find("54121");

		assertEquals(0.0, naics.getDart(), 0.40);
		naics = Whitebox.invokeMethod(naicsDAO, "getBroaderNaicsForDart", naics);
		assertEquals("541", naics.getCode());
		
		assertEquals(0.5, naics.getDart(), 0.40);
	}

}
