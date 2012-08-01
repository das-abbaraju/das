package com.picsauditing.models.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;

public class CaoSaveModelTest extends PicsTest {

	@Mock
	I18nCache i18nCache;
	@Mock
	AuditPercentCalculator auditPercentCalculator;
	
	private CaoSaveModel caoSaveModel;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		caoSaveModel = new CaoSaveModel();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(caoSaveModel, this);
		PicsTestUtil.forceSetPrivateField(caoSaveModel, "i18nCache", i18nCache);
		PicsTestUtil.forceSetPrivateField(caoSaveModel, "auditPercentCalculator", auditPercentCalculator);
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
		
		caoSaveModel.updatePqfOnIncomplete(pqf, AuditStatus.Incomplete);
		assertNull(data.getAuditor());
		assertFalse(data.isVerified());
	}

	@Test
	public void testAddCommentsToNote() throws Exception {
		AuditData normalAuditData = setupAuditData();
		
		assertEquals("Comment : O hai\n",
				caoSaveModel.addAuditDataComment(normalAuditData));
	}

	@Test
	public void testAddCommentsToNote_EmrCategory() throws Exception {
		AuditData emrAuditData = setupAuditData();
		
		emrAuditData.getQuestion().setCategory(
				EntityFactory.makeAuditCategory(AuditCategory.EMR));
		assertEquals("EMR : O hai\n",
				caoSaveModel.addAuditDataComment(emrAuditData));
	}

	@Test
	public void testAddCommentsToNote_OshaCategory() throws Exception {
		stubI18nCache();
		AuditData oshaAuditData = setupAuditData();

		oshaAuditData.getQuestion().setCategory(
				EntityFactory.makeAuditCategory(OshaAudit.CAT_ID_OSHA));
		assertEquals("OSHA : O hai\n",
				caoSaveModel.addAuditDataComment(oshaAuditData));
	}
	
	@Test
	public void generateNote_EmptyList() {
		assertEquals("", caoSaveModel.generateNote(new ArrayList<AuditData>()));
	}
	
	@Test
	public void generateNote_mixedList() {
		stubI18nCache();
		String expectedString = 
			"EMR : O hai\n" + 
			"Comment : O hai\n" + 
			"OSHA : O hai\n";
		
		List<AuditData> testDataList = new ArrayList<AuditData>();

		AuditData emrAuditData = setupAuditData();
		emrAuditData.getQuestion().setCategory(
				EntityFactory.makeAuditCategory(AuditCategory.EMR));
		
		AuditData normalAuditData = setupAuditData();
			
		AuditData oshaAuditData = setupAuditData();
		oshaAuditData.getQuestion().setCategory(
				EntityFactory.makeAuditCategory(OshaAudit.CAT_ID_OSHA));
		
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
	
	private void stubI18nCache() {
		when(i18nCache.hasKey("OSHA", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("OSHA", Locale.ENGLISH)).thenReturn("OSHA");

	}

}