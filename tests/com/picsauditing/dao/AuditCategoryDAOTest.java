package com.picsauditing.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditCategoryDAOTest {

	@Autowired
	private AuditCategoryDAO auditCategoryDAO;

	@Test
	public void testFindDesktopCategories() {
		List<AuditCategory> aList = auditCategoryDAO.findDesktopCategories(1784);
		System.out.println(aList.get(0).getId());
	}
}
