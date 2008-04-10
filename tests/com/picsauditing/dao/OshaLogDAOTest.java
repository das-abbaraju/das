package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class OshaLogDAOTest {

	@Autowired
	private OshaLogDAO oshalogDAO;

	@Test
	public void testSave() {
		OshaLog oshalog = new OshaLog();
		oshalog.setContractorAccount(new ContractorAccount());
		oshalog.getContractorAccount().setId(3666);
		oshalog.setType(OshaType.OSHA);
		oshalog.setLocation("irvine");
		oshalog.setManHours1(12);
		oshalog.setFatalities1(123);
		oshalog = oshalogDAO.save(oshalog);
		assertEquals("irvine", oshalog.getLocation());
		assertTrue(oshalog.getId() > 0);
		oshalogDAO.remove(oshalog.getId());
		OshaLog oshalog1 = oshalogDAO.find(oshalog.getId());
		assertNull(oshalog1);

	}

}
