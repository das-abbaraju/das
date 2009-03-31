package com.picsauditing.dao;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class NoteDAOTest extends TestCase {

	@Autowired
	private NoteDAO noteDAO;

	@Test
	public void testSaveAndRemove() {
		Note note = new Note();
		note.setAccount(new Account());
		note.getAccount().setId(3);
		note.setCreationDate(new Date());
		note.setCreatedBy(new User(3));
		note.setSummary("Junit Testing the notes entity");
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableBy(note.getAccount());

		noteDAO.save(note);
		assertTrue(note.getId() > 0);
		assertTrue(note.getStatus().equals(NoteStatus.Closed));

		noteDAO.remove(note.getId());
		assertNull(noteDAO.find(note.getId()));
	}
}
