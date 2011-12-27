package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.picsauditing.util.FileUtils;

public class NoteTest {

	@Test
	public void testConvertNote_NoBody() throws Exception {
		Note note = new Note();
		note.setOriginalText("5/19/04 3:00 PM PDT (John): A note with less than 99 characters and no carriage returns");
		note.convertNote();

		assertEquals(new Date("5/19/04 3:00 PM PDT"), note.getCreationDate());
		assertEquals("A note with less than 99 characters and no carriage returns", note.getSummary());
		assertEquals("", note.getBody());
	}

	@Test
	public void testConvertNote_LongSummary() throws Exception {
		Note note = new Note();
		note.setOriginalText("5/19/04 3:00 PM PDT (John): A note with more than ninety-nine characters will arbitrarily take the first 99 characters as the summary and the rest will be the body of the note.");
		note.convertNote();

		assertEquals(new Date("5/19/04 3:00 PM PDT"), note.getCreationDate());
		assertEquals(
				"A note with more than ninety-nine characters will arbitrarily take the first 99 characters as the s",
				note.getSummary());
		assertEquals("ummary and the rest will be the body of the note.", note.getBody());
	}

	@Test
	public void testConvertNote_LineBreaks() throws Exception {
		Note note = new Note();
		note.setOriginalText("5/19/04 3:00 PM PDT (John): Any note with a line break (carriage return) within the first 99 characters\n will take the first line to be interpreted as the summary and the rest will be the body of the note.\n");
		note.convertNote();

		assertEquals(new Date("5/19/04 3:00 PM PDT"), note.getCreationDate());
		assertEquals("Any note with a line break (carriage return) within the first 99 characters", note.getSummary());
		// FIXME I wrote these tests to explore how notes work. (I was just curious.) I don't understand why the
		// following assertion is not true. -- Craig Jones 12/22/2011
		
		// "the dot will not match a newline character by default." -- http://www.regular-expressions.info/dot.html
		// Updated Note.convertNote() method to use Pattern.DOTALL flag instead of Pattern.CANON_EQ
		assertEquals(
				" will take the first line to be interpreted as the summary and the rest will be the body of the note.\n",
				note.getBody());
	}

	@Test
	public void testNotes() throws Exception {
		// FIXME This code was originally in StringsTest. Now that we have this NoteTest class, I moved it here. This
		// test is still incomplete, though.
		String noteText = FileUtils.readFile("tests/test_notes.txt");

		BufferedReader reader = new BufferedReader(new StringReader(noteText));

		String line;
		List<Note> notes = new Vector<Note>();
		List<Note> thisSet = new Vector<Note>();
		List<List<Note>> badNotes = new Vector<List<Note>>();

		while ((line = reader.readLine()) != null) {
			Note note = new Note();
			note.setOriginalText(line);
			notes.add(note);
		}

		for (Note note : notes) {
			try {

				note.convertNote();

				if (thisSet.size() > 1) {
					badNotes.add(new Vector<Note>(thisSet));
				}
				thisSet.clear();
				thisSet.add(note);
			} catch (Exception e) {
				thisSet.add(note);
			}
		}

		for (List<Note> badSet : badNotes) {
			for (Note note : badSet) {
				// System.out.println(note.getOriginalText());
			}
		}

	}

}
