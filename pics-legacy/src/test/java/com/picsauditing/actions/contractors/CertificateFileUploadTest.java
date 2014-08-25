package com.picsauditing.actions.contractors;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CertificateFileUploadTest extends PicsTest {

	CertificateFileUpload test;

	@Mock
	ContractorAccountDAO accountDao;
	@Mock
	ContractorAuditDAO auditDao;
	@Mock
	CertificateDAO certificateDAO;
	@Mock
	AuditQuestionDAO questionDAO;
	@Mock
	AuditDataDAO dataDAO;
    @Mock
    AuditBuilderFactory auditBuilderFactory;
	@Mock
	protected BasicDAO dao;

	OperatorAccount operator;
	ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();

		test = new CertificateFileUpload(accountDao, auditDao, certificateDAO,
				questionDAO, dataDAO);
		Whitebox.setInternalState(test, "dao", dao);
		Whitebox.setInternalState(test, "auditDao", auditDao);
        Whitebox.setInternalState(test, "auditBuilderFactory", auditBuilderFactory);

		operator = EntityFactory.makeOperator();
		contractor = EntityFactory.makeContractor();

        when(auditBuilderFactory.isCategoryApplicable(any(AuditCategory.class), any(ContractorAudit.class), any(ContractorAuditOperator.class))).thenReturn(true);
	}

	@Test
	public void testCertificateChangeAfterComplete() throws Exception {
		// set up an audit with
		ContractorAudit audit = setUpInsurancePolicy();
		ContractorAuditOperator cao = audit.getOperators().get(0);

		Whitebox.setInternalState(test, "contractor", contractor);

		AuditData data = setUpAndAuditData(audit);

		// Pending
		cao.changeStatus(AuditStatus.Pending, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Pending, cao.getStatus());

		// Complete
		cao.changeStatus(AuditStatus.Complete, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Resubmitted, cao.getStatus());

		// Approved
		cao.changeStatus(AuditStatus.Approved, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Resubmitted, cao.getStatus());
	}

	private ContractorAudit setUpInsurancePolicy() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Policy);

		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
		audit.getOperators().add(cao);

		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setCao(cao);
		caop.setOperator(operator);
		cao.getCaoPermissions().add(caop);

		return audit;
	}

	private AuditData setUpAndAuditData(ContractorAudit audit) {
		AuditCategory cat = EntityFactory.addCategories(audit.getAuditType(), 1, "Test");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setCategory(cat);
		AuditData auditData = EntityFactory.makeAuditData("Yes");
		auditData.setQuestion(question);
		auditData.setAudit(audit);
		audit.getData().add(auditData);

		return auditData;
	}
}
