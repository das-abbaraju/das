package com.picsauditing.actions.auditType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.importpqf.ImportStopAt;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.BaseTable;

public class ManageQuestionTest extends PicsTranslationTest {

	private ManageQuestion manageQuestion;
	private List<AuditCategory> ancestors;

	@Mock
	private AuditQuestionDAO auditQuestionDAO;
	@Mock
	private BasicDAO dao;
	@Mock
	private AuditExtractOption auditExtractOption;
	@Mock
	private AuditQuestion auditQuestion;
	@Mock
	private AuditCategory category;
	@Mock
	private AuditCategory ancestorCategory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		manageQuestion = new ManageQuestion();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(manageQuestion, this);
		when(auditQuestion.getCategory()).thenReturn(category);
		ancestors = new ArrayList<AuditCategory>();
		ancestors.add(ancestorCategory);
		when(category.getAncestors()).thenReturn(ancestors);
		when(auditQuestion.getId()).thenReturn(1);
	}

	@Test
	public void testLoad_NullExtractOption() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(null);

		manageQuestion.load(auditQuestion);

		assertThat(manageQuestion.getStopAt(), is(equalTo("None")));
		assertThat(manageQuestion.getStartingPoint(), is(equalTo("")));
	}

	@Test
	public void testLoad_ExtractOptionSetsManageQuestionProperties() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(auditExtractOption);
		when(auditExtractOption.getStopAt()).thenReturn(ImportStopAt.NextLine);
		when(auditExtractOption.getStoppingPoint()).thenReturn("TEST_STOP_AT");
		when(auditExtractOption.getStartingPoint()).thenReturn("TEST_START_AT");
		when(auditExtractOption.isStartAtBeginning()).thenReturn(true);
		when(auditExtractOption.isCollectAsLines()).thenReturn(true);

		manageQuestion.load(auditQuestion);

		assertThat(manageQuestion.getStopAt(), is(equalTo(ImportStopAt.NextLine.toString())));
		assertThat(manageQuestion.getStartingPoint(), is(equalTo("TEST_START_AT")));
		assertThat(manageQuestion.getStoppingPoint(), is(equalTo("TEST_STOP_AT")));
		assertTrue(manageQuestion.isStartAtBeginning());
		assertTrue(manageQuestion.isCollectAsLines());
	}

	@Test
	public void testManageExtractOption_NullOptionExtractNotDefinedDoesNothing() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(null);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", false);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(dao, never()).save((BaseTable) any());
		verify(dao, never()).remove((BaseTable) any());
	}

	@Test
	public void testManageExtractOption_NullOptionDefinedExtractCreatesNewOptionInDb() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(null);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", true);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(dao).save((BaseTable) any());
		verify(dao, never()).remove((BaseTable) any());

	}

	@Test
	public void testManageExtractOption_NotNullOptionNotDefinedExtractDeletesOptionFromDb() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(auditExtractOption);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", false);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(dao, never()).save((BaseTable) any());
		verify(dao).remove((BaseTable) any());

	}

	@Test
	public void testManageExtractOption_NotNullOptionExtractIsDefinedReliesOnHibernateSessionToSave() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(auditExtractOption);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", true);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(dao, never()).save((BaseTable) any());
		verify(dao, never()).remove((BaseTable) any());
	}

	@Test
	public void testManageExtractOption_NotNullOptionExtractIsDefinedPopulatesOptionProperties() throws Exception {
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(auditExtractOption);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", true);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);
		Whitebox.setInternalState(manageQuestion, "collectAsLines", true);
		Whitebox.setInternalState(manageQuestion, "startAtBeginning", true);
		Whitebox.setInternalState(manageQuestion, "stopAt", "Text");

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(auditExtractOption).setCollectAsLines(true);
		verify(auditExtractOption).setStopAt(ImportStopAt.Text);
		verify(auditExtractOption).setStartAtBeginning(true);
		verify(auditExtractOption).setStartingPoint(null);
		verify(auditExtractOption).setStoppingPoint(null);
	}

	@Test
	public void testManageExtractOption_NotNullOptionExtractIsDefinedSetsTrimmedStartingAndStoppingPoints()
			throws Exception {
		String TEXT_NEEDS_TRIMMING = "This Text Needs Trimming     ";
		String TEXT_IS_TRIMMED = "This Text Needs Trimming";
		when(auditQuestionDAO.findAuditExtractOptionByQuestionId(anyInt())).thenReturn(auditExtractOption);
		Whitebox.setInternalState(manageQuestion, "extractOptionDefined", true);
		Whitebox.setInternalState(manageQuestion, "question", auditQuestion);
		Whitebox.setInternalState(manageQuestion, "startingPoint", TEXT_NEEDS_TRIMMING);
		Whitebox.setInternalState(manageQuestion, "stoppingPoint", TEXT_NEEDS_TRIMMING);

		Whitebox.invokeMethod(manageQuestion, "manageExtractOption");

		verify(auditExtractOption).setStartingPoint(TEXT_IS_TRIMMED);
		verify(auditExtractOption).setStoppingPoint(TEXT_IS_TRIMMED);
	}

}
