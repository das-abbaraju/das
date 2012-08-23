package com.picsauditing.actions.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.audits.ContractorAuditDownload.SheetStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.PermissionToViewContractor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContractorAuditDownload.class, HSSFSheet.class, HSSFRow.class, I18nCache.class,
		ServletActionContext.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorAuditDownloadTest {
	private ContractorAuditDownload auditDownload;
	private PicsTestUtil testUtil = new PicsTestUtil();

	private AuditType auditType;
	private ContractorAccount contractor;
	private SheetStatus sheetStatus;

	@Mock
	private ContractorAudit audit;
	@Mock
	private EntityManager entityManager;
	@Mock
	private HSSFCell cell;
	@Mock
	private HSSFRow row;
	@Mock
	private HSSFSheet sheet;
	@Mock
	private HttpServletResponse response;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private Permissions permissions;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;
	@Mock
	private ServletOutputStream outputStream;
	@Mock
	private TranslatableString name;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initStatic();

		auditDownload = spy(new ContractorAuditDownload());
		auditType = EntityFactory.makeAuditType();
		contractor = EntityFactory.makeContractor();
		testUtil.autowireEMInjectedDAOs(auditDownload, entityManager);

		sheetStatus = (auditDownload).new SheetStatus();
		sheetStatus.sheet = sheet;

		Whitebox.setInternalState(auditDownload, "permissions", permissions);
		Whitebox.setInternalState(auditDownload, "permissionToViewContractor", permissionToViewContractor);

		setExpectedBehavior();
	}

	@Test
	public void testExecute() throws Exception {
		verifyDownload();
	}

	@Test
	public void testExecute_HasVisibleAndInvisibleCategories() throws Exception {
		AuditCatData visible = EntityFactory.makeAuditCatData();

		AuditCatData override = EntityFactory.makeAuditCatData();
		override.setApplies(false);
		override.setOverride(true);

		AuditCatData invisible = EntityFactory.makeAuditCatData();
		invisible.setApplies(false);

		List<AuditCatData> categoryData = new ArrayList<AuditCatData>();
		categoryData.add(visible);
		categoryData.add(override);
		categoryData.add(invisible);

		when(audit.getCategories()).thenReturn(categoryData);

		verifyDownload();
	}

	@Test
	public void testFillExcelCategories_NoViewableCategories() throws Exception {
		SheetStatus status = Whitebox.invokeMethod(auditDownload, "fillExcelCategories", sheetStatus,
				Collections.emptySet(), EntityFactory.makeAuditCategory());
		assertEquals(sheetStatus, status);

		verify(sheet, never()).createRow(anyInt());
		verify(row, never()).createCell(anyInt(), anyInt());
	}

	@Test
	public void testFillExcelCategories_ViewableCategory() throws Exception {
		AuditCategory category = EntityFactory.makeAuditCategory();

		Set<Integer> viewable = new HashSet<Integer>();
		viewable.add(category.getId());

		Whitebox.invokeMethod(auditDownload, "fillExcelCategories", sheetStatus, viewable, category);

		verify(sheet).createRow(anyInt());
		verify(row).createCell(anyInt(), anyInt());
	}

	@Test
	public void testFillExcelCategories_ViewableCategoryWithSubcat() throws Exception {
		AuditCategory category = EntityFactory.makeAuditCategory();
		AuditCategory child = EntityFactory.makeAuditCategory();
		category.getSubCategories().add(child);

		Set<Integer> viewable = new HashSet<Integer>();
		viewable.add(category.getId());
		viewable.add(child.getId());

		Whitebox.invokeMethod(auditDownload, "fillExcelCategories", sheetStatus, viewable, category);

		verify(sheet, times(2)).createRow(anyInt());
		verify(row, times(2)).createCell(anyInt(), anyInt());
	}

	@Test
	public void testFillExcelQuestions_NoQuestions() throws Exception {
		SheetStatus status = Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus,
				Collections.emptyList());
		assertEquals(sheetStatus, status);

		verify(sheet, never()).createRow(anyInt());
		verify(row, never()).createCell(anyInt(), anyInt());
	}

	@Test
	public void testFillExcelQuestions_QuestionNotCurrent() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setExpirationDate(new Date());

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(question);

		Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus, questions);

		verify(sheet, never()).createRow(anyInt());
		verify(row, never()).createCell(anyInt());
	}

	@Test
	public void testFillExcelQuestions_QuestionCurrent() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(question);

		Whitebox.setInternalState(auditDownload, "conAudit", audit);
		Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus, questions);

		verify(sheet).createRow(anyInt());
		verify(row).createCell(anyInt());
	}

	@Test
	public void testFillExcelQuestions_QuestionHyperlinkEmptyAnswer() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setName(EntityFactory.makeTranslatableString("<a href=\"Link\" target=\"Target\">Test</a>"));

		AuditData data = EntityFactory.makeAuditData("", question);
		List<AuditData> datas = new ArrayList<AuditData>();
		datas.add(data);

		when(audit.getData()).thenReturn(datas);

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(question);

		Whitebox.setInternalState(auditDownload, "conAudit", audit);
		Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus, questions);

		verify(sheet, times(2)).createRow(anyInt());
		verify(row, times(2)).createCell(anyInt());
	}

	@Test
	public void testFillExcelQuestions_QuestionCurrentAuditData() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditData data = EntityFactory.makeAuditData("Answer", question);
		List<AuditData> datas = new ArrayList<AuditData>();
		datas.add(data);

		when(audit.getData()).thenReturn(datas);

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(question);

		Whitebox.setInternalState(auditDownload, "conAudit", audit);
		Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus, questions);

		verify(sheet).createRow(anyInt());
		verify(row, times(2)).createCell(anyInt());
	}

	@Test
	public void testFillExcelQuestions_QuestionCurrentAuditDataWithComment() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditData data = EntityFactory.makeAuditData("Answer", question);
		data.setComment("Comment");
		List<AuditData> datas = new ArrayList<AuditData>();
		datas.add(data);

		when(audit.getData()).thenReturn(datas);

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(question);

		Whitebox.setInternalState(auditDownload, "conAudit", audit);
		Whitebox.invokeMethod(auditDownload, "fillExcelQuestions", sheetStatus, questions);

		verify(sheet).createRow(anyInt());
		verify(row, times(3)).createCell(anyInt());
	}

	private void initStatic() {
		PowerMockito.mockStatic(I18nCache.class);
		PowerMockito.mockStatic(ServletActionContext.class);
		when(I18nCache.getInstance()).thenReturn(i18nCache);
		when(ServletActionContext.getResponse()).thenReturn(response);
	}

	private void setExpectedBehavior() throws Exception {
		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(audit.isVisibleTo(permissions)).thenReturn(true);
		when(entityManager.find(eq(ContractorAudit.class), anyInt())).thenReturn(audit);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(permissionToViewContractor.check(anyBoolean())).thenReturn(true);
		when(response.getOutputStream()).thenReturn(outputStream);
		// Sheet
		PowerMockito.doReturn(row).when(sheet).createRow(anyInt());
		PowerMockito.doReturn(cell).when(row).createCell(anyInt());
		PowerMockito.doReturn(cell).when(row).createCell(anyInt(), anyInt());
	}

	private void verifyDownload() throws Exception {
		assertNull(auditDownload.execute());

		verify(response).setContentType(anyString());
		verify(response).setHeader(anyString(), anyString());
		verify(outputStream).flush();
		verify(response).flushBuffer();
	}
}