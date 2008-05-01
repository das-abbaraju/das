package com.picsauditing.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.WidgetUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class WidgetUserDAOTest {
	@Autowired
	private WidgetUserDAO dao;
	
	@Test
	public void findByUser() {
		List<WidgetUser> widgets = dao.findByUser();
		for(WidgetUser wu : widgets) {
			System.out.println(wu.getWidget().getCaption());
		}
	}
}
