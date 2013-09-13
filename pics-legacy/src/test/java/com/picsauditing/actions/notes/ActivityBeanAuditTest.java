package com.picsauditing.actions.notes;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.NoteCategory;

public class ActivityBeanAuditTest {
	private AuditType auditType;
	
	@Before
	public void setUp() throws Exception {
		auditType = new AuditType();
	}
	@Test
	public void testGetNoteCategory_Audit() throws Exception {
		auditType.setClassType(AuditTypeClass.Audit);
		ActivityBean bean = new ActivityBeanAudit();
		bean.setAuditType(auditType);
		assertEquals(NoteCategory.Audits, bean.getNoteCategory());
	}
	@Test
	public void testGetNoteCategory_PQF() throws Exception {
		auditType.setClassType(AuditTypeClass.PQF);
		ActivityBean bean = new ActivityBeanAudit();
		bean.setAuditType(auditType);
		assertEquals(NoteCategory.Audits, bean.getNoteCategory());
	}
	@Test
	public void testGetNoteCategory_Insurance() throws Exception {
		auditType.setClassType(AuditTypeClass.Policy);
		ActivityBean bean = new ActivityBeanAudit();
		bean.setAuditType(auditType);
		assertEquals(NoteCategory.Insurance, bean.getNoteCategory());
	}
	@Test
	public void testGetNoteCategory_IM() throws Exception {
		auditType.setClassType(AuditTypeClass.IM);
		ActivityBean bean = new ActivityBeanAudit();
		bean.setAuditType(auditType);
		assertEquals(NoteCategory.Employee, bean.getNoteCategory());
	}
	@Test
	public void testGetNoteCategory_Employee() throws Exception {
		auditType.setClassType(AuditTypeClass.Employee);
		ActivityBean bean = new ActivityBeanAudit();
		bean.setAuditType(auditType);
		assertEquals(NoteCategory.Employee, bean.getNoteCategory());
	}
}
