package com.picsauditing.actions.audits;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;

public class VerifyAuditTest extends PicsTest {
	private VerifyAudit verifyAudit = new VerifyAudit();
	
	private ContractorAccount contractor;
	private ContractorAudit conAudit;

	@Mock
	private AuditDataDAO auditDataDao = new AuditDataDAO();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		MockitoAnnotations.initMocks(this);
		autowireEMInjectedDAOs(verifyAudit);

		contractor = EntityFactory.makeContractor();
		conAudit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);

		PicsTestUtil.forceSetPrivateField(verifyAudit, "auditDataDao",
				auditDataDao);
		PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit",
				conAudit);
		
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

}