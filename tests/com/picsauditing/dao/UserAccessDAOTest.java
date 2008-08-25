package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class UserAccessDAOTest {

	@Autowired
	UserAccessDAO userAccessDAO;

	@Test
	public void findByUser() {
		List<UserAccess> list = userAccessDAO.findByUser(941);
		assertEquals(true, list.size() > 0);
	}

	@Test
	public void save() {
		UserAccess access = new UserAccess();
		access.setUser(new User(941));
		access.setViewFlag(true);
		access.setEditFlag(false);
		access.setDeleteFlag(null);
		access.setOpPerm(OpPerms.AllContractors);
		access.setLastUpdate(new Date());
		access.setGrantedBy(access.getUser());
		access = userAccessDAO.save(access);
		
		int id = access.getId();
		access = userAccessDAO.find(id);
		assertEquals(true, access.getViewFlag());
		assertEquals(false, access.getEditFlag());
		assertEquals(null, access.getDeleteFlag());
		userAccessDAO.remove(id);
	}
}
