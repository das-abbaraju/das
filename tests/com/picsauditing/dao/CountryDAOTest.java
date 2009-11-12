package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Country;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class CountryDAOTest extends TestCase {
	@Autowired
	private CountryDAO dao;

	@Test
	public final void testFind() throws Exception {
		List<Country> countries = dao.findAll();
		assertTrue(countries.size() > 0);
	}
}
