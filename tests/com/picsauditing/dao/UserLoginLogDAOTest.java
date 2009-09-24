package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class UserLoginLogDAOTest {

	@Autowired
	private UserLoginLogDAO userLoginLogDAO;

	@Test
	public void testSave() {
		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setUser(new User(1098));
		loginLog.setAdmin(new User(941));
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress("70.12.12.23");
		loginLog.setSuccessful('Y');
		userLoginLogDAO.save(loginLog);
		int id = loginLog.getId();
		loginLog = userLoginLogDAO.find(id);
		assertEquals("70.12.12.23", loginLog.getRemoteAddress());
		userLoginLogDAO.remove(id);
	}
}
