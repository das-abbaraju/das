package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Webcam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class WebcamDAOTest extends TestCase {

	@Autowired
	private WebcamDAO dao;

	@Test
	public void testFind() {
		Webcam webcam = dao.find(1);
		assertEquals(1, webcam.getId());
	}

	@Test
	public void testFindWhere() {
		List<Webcam> webcam = dao.findWhere(null);
		assertTrue(webcam.size() > 0);
	}

}
