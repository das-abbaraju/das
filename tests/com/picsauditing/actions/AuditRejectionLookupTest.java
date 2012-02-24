package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;

//import org.apache.struts2.StrutsSpringJUnit4TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.opensymphony.xwork2.Action;
import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/tests.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
public class AuditRejectionLookupTest /* extends StrutsSpringJUnit4TestCase */ {
	private AuditRejectionLookup action;
	public User user;
	public Permissions permissions;


	@Before
	public void setUp() throws Exception {
		action = new AuditRejectionLookup();
	}

	@Test
	public void testExecute() throws Exception {
		setUpBasicModelObjects();
		assertEquals(Action.SUCCESS,action.execute());
		assertEquals("[{\"id\":\"111\",\"value\":\"Another message\"},{\"id\":\"112\",\"value\":\"Some message\"}]",action.getJsonArray().toString());
		
	}
	private void setUpBasicModelObjects() {
		user = EntityFactory.makeUser();
		permissions = EntityFactory.makePermission(user);

	}


}
