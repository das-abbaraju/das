package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.util.Strings;

public class RequestNewContractorTest {
	private boolean successful;

	private String addToNotes;
	private String contactType;
	private ContractorRegistrationRequest newContractor;
	
	private static String PERSONAL = "Personal Email";
	private static String EMAIL = "Email";
	private static String PHONE = "Phone";

	@Before
	public void setUp() {
		successful = true;
		newContractor = new ContractorRegistrationRequest();
	}
	
	@Test
	public void testContactByPersonalEmail() {
		contactType = PERSONAL;
		
		assertTrue(Strings.isEmpty(addToNotes));
		
		checkNotesAndIncrementContactCount();
		
		assertTrue(successful);
		assertEquals(1, newContractor.getContactCountByEmail());
		assertEquals(1, newContractor.getContactCount());
	}

	@Test
	public void testContactByDraftEmailMissingNotes() {
		contactType = EMAIL;
		
		assertTrue(Strings.isEmpty(addToNotes));
		
		checkNotesAndIncrementContactCount();
		
		assertFalse(successful);
		assertEquals(0, newContractor.getContactCountByEmail());
		assertEquals(0, newContractor.getContactCount());
	}
	
	@Test
	public void testContactByDraftEmailWithNotes() throws Exception {
		contactType = EMAIL;
		addToNotes = "Testing";
		
		assertFalse(Strings.isEmpty(addToNotes));
		
		checkNotesAndIncrementContactCount();
		
		assertTrue(successful);
		assertEquals(1, newContractor.getContactCountByEmail());
		assertEquals(1, newContractor.getContactCount());
	}
	
	@Test
	public void testContactByPhoneMissingNotes() throws Exception {
		contactType = PHONE;
		
		assertTrue(Strings.isEmpty(addToNotes));
		
		checkNotesAndIncrementContactCount();
		
		assertFalse(successful);
		assertEquals(0, newContractor.getContactCountByPhone());
		assertEquals(0, newContractor.getContactCount());
	}

	@Test
	public void testContactByPhone() {
		contactType = PHONE;
		addToNotes = "Testing";
		
		assertFalse(Strings.isEmpty(addToNotes));
		
		checkNotesAndIncrementContactCount();
		
		assertTrue(successful);
		assertEquals(1, newContractor.getContactCountByPhone());
		assertEquals(1, newContractor.getContactCount());
	}
	
	private void checkNotesAndIncrementContactCount() {
		if (Strings.isEmpty(addToNotes) && !PERSONAL.equals(contactType)) {
			successful = false;
			return;
		}

		if (EMAIL.equals(contactType)) {
			newContractor.contactByEmail();
		} else if (PERSONAL.equals(contactType)) {
			newContractor.contactByEmail();
		} else
			newContractor.contactByPhone();		
	}
}
