package com.picsauditing.dao;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserSwitch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class UserSwitchDAOTest {
	@Autowired
	UserSwitchDAO userSwitchDao;
	
	
	@Test
	public void testSave() {
		UserSwitch userSwitch = new UserSwitch();
		int switchToID	= 792;	//info@picsauditing.com
		int userID		= 941;	//tallred
		
		userSwitch.setUser(new User(userID));
		userSwitch.setSwitchTo(new User(switchToID));
		
		userSwitch = userSwitchDao.save(userSwitch);
		assertTrue("userSwitch should have an id > 0", userSwitch.getId() > 0);
		
		userSwitchDao.remove(userSwitch.getId());
		assertNull("userSwitch was not removed", userSwitchDao.find(userSwitch.getId()));
		
	}

}
