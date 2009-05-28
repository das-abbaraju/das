package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AmBest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AmBestDAOTest extends TestCase {

	@Autowired
	private AmBestDAO dao;

	@Test
	public void testFind() {
		List<AmBest> list = dao.findByCompanyName("insurance");
		for(AmBest amb : list)
			System.out.println(amb.toString());
	}

	public void testParseCompany() {
		assertEquals("foobar", AmBestDAO.parseCompany("foobar"));
		assertEquals("foobar", AmBestDAO.parseCompany("foobar (1234)"));
		assertEquals("foobar", AmBestDAO.parseCompany("foobar(1234)"));
		assertEquals("foobar )1234(", AmBestDAO.parseCompany("foobar )1234("));
		assertEquals("foobar (1234", AmBestDAO.parseCompany("foobar (1234"));
		assertEquals("foobar 1234)", AmBestDAO.parseCompany("foobar 1234)"));
		assertEquals("foobar (inc)", AmBestDAO.parseCompany("foobar (inc) (1234)"));
	}


}
