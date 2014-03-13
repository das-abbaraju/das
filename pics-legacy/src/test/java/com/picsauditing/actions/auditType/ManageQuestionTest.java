package com.picsauditing.actions.auditType;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.importpqf.ImportStopAt;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SlugService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class ManageQuestionTest extends PicsActionTest {
    private static final String QUESTION_NAME = "Test Question Name";
    private static final String QUESTION_TYPE_NOT_MULTIPLE_CHOICE = "Not MultipleChoice";

	private ManageQuestion manageQuestion;
	private List<AuditCategory> ancestors;
    private List<String> auditQuestionLanguages = new ArrayList<>();

    @Mock
    private SlugService slugService;
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
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AuditType auditType;
    @Mock
    private AuditOptionGroup auditOptionGroup;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		manageQuestion = new ManageQuestion();
        super.setUp(manageQuestion);

        Whitebox.setInternalState(manageQuestion,"slugService",slugService);
        Whitebox.setInternalState(manageQuestion,"featureToggleChecker",featureToggleChecker);
        Whitebox.setInternalState(manageQuestion,"auditType",auditType);
        Whitebox.setInternalState(manageQuestion, "question", auditQuestion);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(manageQuestion, this);
		when(auditQuestion.getCategory()).thenReturn(category);
		ancestors = new ArrayList<>();
		ancestors.add(ancestorCategory);
		when(category.getAncestors()).thenReturn(ancestors);
		when(auditQuestion.getId()).thenReturn(1);
		when(auditQuestion.getName()).thenReturn(QUESTION_NAME);

        auditQuestionLanguages.add("en");
	}

    @Test
    public void testLoad_NullQuestion() throws Exception {
        manageQuestion.load((AuditQuestion) null);
        verify(auditQuestionDAO, never()).findAuditExtractOptionByQuestionId(anyInt());
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

    @Test
    public void testValidateSlug() {
        when(slugService.slugHasDuplicate(AuditQuestion.class,"manual-audit",0)).thenReturn(true);
        when(slugService.slugIsURICompliant("manual-audit")).thenReturn(true);

        Whitebox.setInternalState(manageQuestion, "slug", "manual-audit");

        String response = manageQuestion.validateSlug();
        assertEquals("json",response);
        assertEquals("{\"isURI\":true,\"isUnique\":false}",manageQuestion.getJson().toString());
    }

    @Test
    public void testGenerateSlug() throws Exception {
        when(slugService.generateSlug(AuditQuestion.class,"Manual Audit",0)).thenReturn("manual-audit");

        Whitebox.setInternalState(manageQuestion, "stringToSlugify", "Manual Audit");

        String response = manageQuestion.generateSlug();
        assertEquals("json",response);
        assertEquals("{\"slug\":\"manual-audit\"}",manageQuestion.getJson().toString());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOn() throws Exception {
        setupInputValidationMocks();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(true);

        manageQuestion.save();

        assertFalse(manageQuestion.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationServiceOff() throws Exception {
        setupInputValidationMocks();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)).thenReturn(false);
        when(auditQuestion.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageQuestion.save();

        assertEmitsRequiredLanguageError(manageQuestion);
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOn() throws Exception {
        setupInputValidationMocks();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(true);

        manageQuestion.save();

        assertFalse(manageQuestion.hasActionErrors());
    }

    @Test
    public void testSave_InputValidation_MissingChildRequiredLanguages_TranslationDataSourceOff() throws Exception {
        setupInputValidationMocks();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE)).thenReturn(false);
        when(auditQuestion.hasMissingChildRequiredLanguages()).thenReturn(Boolean.TRUE);

        manageQuestion.save();

        assertEmitsRequiredLanguageError(manageQuestion);
    }

    private void setupInputValidationMocks() throws Exception {
        when(auditQuestion.getQuestionType()).thenReturn(QUESTION_TYPE_NOT_MULTIPLE_CHOICE);
        when(auditQuestion.getOption()).thenReturn(auditOptionGroup);
        when(auditQuestion.getLanguages()).thenReturn(auditQuestionLanguages);
        when(auditQuestion.getNumber()).thenReturn(123);
        when(auditQuestionDAO.save(auditQuestion)).thenReturn(auditQuestion);
    }
}
