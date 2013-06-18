package com.picsauditing.actions.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.search.Database;

public class VerifyAuditTest {
	private VerifyAudit verifyAudit;
	
	private ContractorAccount contractor;
	private ContractorAudit conAudit;

	@Mock private AuditDataDAO auditDataDao;
	@Mock private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		verifyAudit = new VerifyAudit();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(verifyAudit, this);

		contractor = EntityFactory.makeContractor();
		conAudit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);

		PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit", conAudit);
	}

	@Test
	public void testGetPqfQuestions_QuestionsToValidate() {
		ArrayList<AuditData> list = new ArrayList<AuditData>();
		AuditData auditData = EntityFactory.makeAuditData("Yes");
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		
		auditCatData.getCategory().setId(auditData.getQuestion().getCategory().getId());
		conAudit.getCategories().add(auditCatData);
		
		
		when(auditDataDao.findCustomPQFVerifications(Matchers.anyInt())).thenReturn(list);
		
		assertEquals(0, verifyAudit.getPqfQuestions().size()); // no categories
		
		list.add(auditData);
		PicsTestUtil.forceSetPrivateField(verifyAudit, "pqfQuestions", null);
		assertEquals(1, verifyAudit.getPqfQuestions().size()); // one applicable
		
		auditCatData.setApplies(false);
		PicsTestUtil.forceSetPrivateField(verifyAudit, "pqfQuestions", null);
		assertEquals(0, verifyAudit.getPqfQuestions().size()); // no applicable
	}

	@Test
	public void testShowOsha() {
		ContractorAccount con = EntityFactory.makeContractor();
		
		ContractorAudit annual= EntityFactory.makeAnnualUpdate(11, con, "2010");
		PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit", annual);
		
		
		assertFalse(verifyAudit.showOsha(OshaType.OSHA));
		AuditData oshaKept = EntityFactory.makeAuditData("Yes", 2064);
		annual.getData().add(oshaKept);
		assertTrue(verifyAudit.showOsha(OshaType.OSHA));
;	}

}
