package com.picsauditing.actions.notes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.NoteCategory;

public class ActivityBeanTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInNoteCategory_InList() {
		ActivityBean bean = new ActivityBeanNote();
		bean.setNoteCategory(NoteCategory.Audits);
		NoteCategory[] filterCategory = new NoteCategory[1];
		filterCategory[0] = NoteCategory.Audits;
		assertTrue(bean.inNoteCategory(filterCategory));
	}
	@Test
	public void testInNoteCategory_NotInList() {
		ActivityBean bean = new ActivityBeanNote();
		bean.setNoteCategory(NoteCategory.Audits);
		NoteCategory[] filterCategory = new NoteCategory[1];
		filterCategory[0] = NoteCategory.Insurance;
		assertFalse(bean.inNoteCategory(filterCategory));
	}
	@Test
	public void testInNoteCategory_NullList() {
		ActivityBean bean = new ActivityBeanNote();
		bean.setNoteCategory(NoteCategory.Audits);
		NoteCategory[] filterCategory = null;
		assertTrue(bean.inNoteCategory(filterCategory));
	}
	@Test
	public void testInNoteCategory_EmptyList() {
		ActivityBean bean = new ActivityBeanNote();
		bean.setNoteCategory(NoteCategory.Audits);
		NoteCategory[] filterCategory = new NoteCategory[0];
		assertTrue(bean.inNoteCategory(filterCategory));
	}

}
