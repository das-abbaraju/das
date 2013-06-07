package com.picsauditing.actions.contractors;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
	AuditCategoryRuleCache auditCategoryRuleCache;
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

		operator = EntityFactory.makeOperator();
		contractor = EntityFactory.makeContractor();
	}

	@Test
	public void testCertificateChangeAfterComplete() throws Exception {
		// set up an audit with
		ContractorAudit audit = setUpInsurancePolicy();
		ContractorAuditOperator cao = audit.getOperators().get(0);

		Whitebox.setInternalState(test, "contractor", contractor);
		Whitebox.setInternalState(test, "auditCategoryRuleCache", auditCategoryRuleCache);

		AuditData data = setUpCategoryAndAuditData(audit);

		// Pending
		cao.changeStatus(AuditStatus.Pending, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Pending, cao.getStatus());

		// Complete
		cao.changeStatus(AuditStatus.Complete, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Incomplete, cao.getStatus());

		// Approved
		cao.changeStatus(AuditStatus.Approved, null);
		Whitebox.invokeMethod(test, "certificateUploadStatusAdjustments", data);
		assertEquals(AuditStatus.Incomplete, cao.getStatus());
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

	private AuditData setUpCategoryAndAuditData(ContractorAudit audit) {
		AuditCategory cat = EntityFactory.addCategories(audit.getAuditType(), 1, "Test");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setCategory(cat);
		AuditData auditData = EntityFactory.makeAuditData("Yes");
		auditData.setQuestion(question);
		auditData.setAudit(audit);
		audit.getData().add(auditData);

		AuditCategoryRule rule1 = new AuditCategoryRule();
		rule1.setAuditType(audit.getAuditType());
		rule1.setOperatorAccount(operator);
		rule1.setAuditCategory(cat);
		rule1.setRootCategory(true);
		List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
		catRules.add(rule1);

		when(auditCategoryRuleCache.getRules(audit.getContractorAccount(),
				audit.getAuditType())).thenReturn(catRules);

		List<AuditCategoryRule> rules = auditCategoryRuleCache.getRules(audit.getContractorAccount(), audit.getAuditType());
		return auditData;
	}
}
