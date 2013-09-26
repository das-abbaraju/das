package com.picsauditing.models.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.audit.AuditPeriodService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import org.springframework.beans.factory.annotation.Autowired;

public class CaoSaveModelTest extends PicsTest {

	private CaoSaveModel caoSaveModel;

	@Mock
	AuditPercentCalculator auditPercentCalculator;
    @Mock
    protected BasicDAO dao;

    protected AuditPeriodService auditPeriodService = new AuditPeriodService();
    protected ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		caoSaveModel = new CaoSaveModel();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(caoSaveModel, this);

        auditPeriodService = new AuditPeriodService();
        contractor = EntityFactory.makeContractor();

		PicsTestUtil.forceSetPrivateField(caoSaveModel, "auditPercentCalculator", auditPercentCalculator);
        PicsTestUtil.forceSetPrivateField(caoSaveModel, "auditPeriodService", auditPeriodService);
        PicsTestUtil.forceSetPrivateField(caoSaveModel, "dao", dao);
	}

    @Test
    public void testUpdateParentAuditOnCompleteIncomplete() {
        ContractorAudit yearly = createPeriodAudit(AuditTypePeriod.Yearly);
        ContractorAudit quarterly = createPeriodAudit(AuditTypePeriod.Quarterly);
        ContractorAudit monthly = createPeriodAudit(AuditTypePeriod.Monthly);

        quarterly.getAuditType().setParent(yearly.getAuditType());
        monthly.getAuditType().setParent(quarterly.getAuditType());
        yearly.setAuditFor("2012");
        quarterly.setAuditFor("2012:1");
        monthly.setAuditFor("2012-01");

//        when(auditPeriodService.getParentAuditFor(org.mockito.Matchers.any(AuditType.class), org.mockito.Matchers.eq("2012-01"))).thenReturn("2012:1");
//        when(auditPeriodService.getParentAuditFor(org.mockito.Matchers.any(AuditType.class), org.mockito.Matchers.eq("2012:1"))).thenReturn("2012");

        // monthly - pending, quarterly - pending, yearly - pending
        caoSaveModel.updateParentAuditOnCompleteIncomplete(monthly, AuditStatus.Incomplete);
        verify(auditPercentCalculator, times(2)).percentCalculateComplete(org.mockito.Matchers.any(ContractorAudit.class), org.mockito.Matchers.eq(true));
    }

    private ContractorAudit createPeriodAudit(AuditTypePeriod period) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(period.ordinal(), contractor);
        audit.getAuditType().setPeriod(period);

        EntityFactory.addCao(audit, EntityFactory.makeOperator());
        changeStatus(audit, AuditStatus.Pending);

        return audit;
    }

    private void changeStatus(ContractorAudit audit, AuditStatus status) {
        audit.getOperators().get(0).changeStatus(status, null);
    }

	@Test
	public void testAddAuditDataComment_BlankIfAlreadyVerified() {
		int catIdOsha = OshaAudit.CAT_ID_OSHA;
		AuditData auditData = EntityFactory.makeAuditData("pdf");
		auditData.getQuestion().getCategory().setId(catIdOsha);
		auditData.setVerified(true);
		auditData.setComment("I'm not empty.");

		String comment = caoSaveModel.addAuditDataComment(auditData);

		assertEquals("", comment);
	}

	@Test
	public void testAddAuditDataComment_BlankIfNoComment() {
		int catIdOsha = OshaAudit.CAT_ID_OSHA;
		AuditData auditData = EntityFactory.makeAuditData("pdf");
		auditData.getQuestion().getCategory().setId(catIdOsha);
		auditData.setVerified(false);
		auditData.setComment("");

		String comment = caoSaveModel.addAuditDataComment(auditData);

		assertEquals("", comment);
	}

	@Test
	public void testAddAuditDataComment_OshaAdditionalDoesntCauseNullPointerException() {
		int catId = OshaAudit.CAT_ID_OSHA_ADDITIONAL;
		AuditData auditData = EntityFactory.makeAuditData("pdf");
		auditData.getQuestion().getCategory().setId(catId);
		auditData.setVerified(false);
		auditData.setComment("I'm not empty.");

		caoSaveModel.addAuditDataComment(auditData);
	}

	@Test
	public void testUpdatePqfOnIncomplete() {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
		AuditData data = EntityFactory.makeAuditData("pdf");
		data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
		data.setVerified(true);
		data.setAuditor(EntityFactory.makeUser());
		pqf.getData().add(data);

		caoSaveModel.unverifySafetyManualQuestionInPqf(pqf, AuditStatus.Incomplete);
		assertNull(data.getAuditor());
		assertFalse(data.isVerified());
	}

	@Test
	public void testAddCommentsToNote() throws Exception {
		AuditData normalAuditData = setupAuditData();

		assertEquals("Comment : O hai\n", caoSaveModel.addAuditDataComment(normalAuditData));
	}

	@Test
	public void testAddCommentsToNote_EmrCategory() throws Exception {
		AuditData emrAuditData = setupAuditData();

		emrAuditData.getQuestion().setCategory(EntityFactory.makeAuditCategory(AuditCategory.EMR));
		assertEquals("EMR : O hai\n", caoSaveModel.addAuditDataComment(emrAuditData));
	}

	@Test
	public void testAddCommentsToNote_OshaCategory() throws Exception {
		stubTranslationService();
		AuditData oshaAuditData = setupAuditData();

		oshaAuditData.getQuestion().setCategory(EntityFactory.makeAuditCategory(OshaAudit.CAT_ID_OSHA));
		assertEquals("OSHA : O hai\n", caoSaveModel.addAuditDataComment(oshaAuditData));
	}

	@Test
	public void generateNote_EmptyList() {
		assertEquals("", caoSaveModel.generateNote(new ArrayList<AuditData>()));
	}

	@Test
	public void generateNote_mixedList() {
		stubTranslationService();
		String expectedString = "EMR : O hai\n" + "Comment : O hai\n" + "OSHA : O hai\n";

		List<AuditData> testDataList = new ArrayList<AuditData>();

		AuditData emrAuditData = setupAuditData();
		emrAuditData.getQuestion().setCategory(EntityFactory.makeAuditCategory(AuditCategory.EMR));

		AuditData normalAuditData = setupAuditData();

		AuditData oshaAuditData = setupAuditData();
		oshaAuditData.getQuestion().setCategory(EntityFactory.makeAuditCategory(OshaAudit.CAT_ID_OSHA));

		testDataList.add(emrAuditData);
		testDataList.add(normalAuditData);
		testDataList.add(oshaAuditData);

		assertEquals(expectedString, caoSaveModel.generateNote(testDataList));
	}

	private AuditData setupAuditData() {
		AuditData auditData = new AuditData();

		auditData = EntityFactory.makeAuditData("Yes");

		auditData.setComment("O hai");
		auditData.setVerified(false);

		return auditData;
	}

	private void stubTranslationService() {
		when(translationService.hasKey("OSHA", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(translationService.getText("OSHA", Locale.ENGLISH)).thenReturn("OSHA");

	}

}