package com.picsauditing.actions.users;

import static org.junit.Assert.*;


import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;

public class ChangePasswordTest {
	User testUser = new User();
	
	private void createUser(boolean accountType){		
		Account account = new Account();
		
		if (accountType){						
			account.setType("Contractor");
		} else{
			account.setType("Admin");
		}		
		
		this.testUser.setAccount(account);		
	}
	@Test
	public void testemailPassword() throws Exception{		
	}
	
	@Test
	public void testsendRecoveryEmail() throws Exception {		
	}
	
	@Test
	public void testchangePassword() throws Exception{		
	}
	
	@Test
	public void testisHasProfileEdit() throws Exception{							
		createUser(true);				
		assertTrue(testUser.getAccount().isContractor());
		createUser(false);		
		assertFalse(testUser.getAccount().isContractor());
	}
}
