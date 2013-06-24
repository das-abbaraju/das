package com.picsauditing.models.audits;

import static junit.framework.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.test.TranslatorFactorySetup;

public class AuditorAssignorTest {

	@BeforeClass
	public static void classSetUp() {
		TranslatorFactorySetup.setupTranslatorFactoryForTest();
	}

	@AfterClass
	public static void classTearDown() {
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Test
	public void testAssignClosingAuditor() throws Exception {
		int accountId = 3;
		int userId = 2;
		User closingAuditor = User.builder().name("John Test").build();
		ContractorAudit audit = ContractorAudit.builder().contractor(ContractorAccount.builder().id(accountId).build())
				.auditType(AuditType.builder().name("Test Audit").build()).build();
		Note note = Note.builder().build();
		Permissions permissions = Permissions.builder().userId(userId).build();
		AuditorAssignor.assignClosingAuditor(closingAuditor, audit, note, permissions);

        assertEquals("Assigned John Test as Closing Auditor for Test Audit", note.getSummary());
		assertEquals(userId, audit.getUpdatedBy().getId());
		assertEquals(accountId, note.getAccount().getId());
		assertEquals(NoteCategory.Audits, note.getNoteCategory());
		assertEquals(Account.EVERYONE, note.getViewableBy().getId());
		assertEquals(closingAuditor, audit.getClosingAuditor());
	}

	@Test
	public void testAssignClosingAuditor_NullAuditor() throws Exception {
		int accountId = 3;
		int userId = 2;
		User closingAuditor = null;
		ContractorAudit audit = ContractorAudit.builder().contractor(ContractorAccount.builder().id(accountId).build())
				.auditType(AuditType.builder().name("Test Audit").build()).build();
		Note note = Note.builder().build();
		Permissions permissions = Permissions.builder().userId(userId).build();
		AuditorAssignor.assignClosingAuditor(closingAuditor, audit, note, permissions);

        assertEquals("Unassigned closing auditor for Test Audit", note.getSummary());
		assertEquals(userId, audit.getUpdatedBy().getId());
		assertEquals(accountId, note.getAccount().getId());
		assertEquals(NoteCategory.Audits, note.getNoteCategory());
		assertEquals(Account.EVERYONE, note.getViewableBy().getId());
		assertEquals(closingAuditor, audit.getClosingAuditor());
	}

	@Test
	public void testAssignClosingAuditor_AccountOnAuditType() throws Exception {
		int accountId = 3;
		int userId = 2;
		int operatorId = 4;
		User closingAuditor = User.builder().name("John Test").build();
		ContractorAudit audit = ContractorAudit
				.builder()
				.contractor(ContractorAccount.builder().id(accountId).build())
				.auditType(
						AuditType.builder().name("Test Audit")
								.account(OperatorAccount.builder().id(operatorId).build()).build()).build();
		Note note = Note.builder().build();
		Permissions permissions = Permissions.builder().userId(userId).build();
		AuditorAssignor.assignClosingAuditor(closingAuditor, audit, note, permissions);

        assertEquals("Assigned John Test as Closing Auditor for Test Audit", note.getSummary());
		assertEquals(userId, audit.getUpdatedBy().getId());
		assertEquals(accountId, note.getAccount().getId());
		assertEquals(NoteCategory.Audits, note.getNoteCategory());
		assertEquals(operatorId, note.getViewableBy().getId());
		assertEquals(closingAuditor, audit.getClosingAuditor());
	}
}
