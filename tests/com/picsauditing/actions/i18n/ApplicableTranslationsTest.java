package com.picsauditing.actions.i18n;

import static com.picsauditing.EntityFactory.makeAuditQuestion;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.Database;

public class ApplicableTranslationsTest extends PicsTranslationTest {
	private ApplicableTranslations applicableTranslations;

	@Mock
	private BasicDAO basicDAO;
	@Mock
	private Database database;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		applicableTranslations = new ApplicableTranslations();

		Whitebox.setInternalState(applicableTranslations, "dao", basicDAO);
		Whitebox.setInternalState(applicableTranslations, "database", database);
	}

	@Test
	public void testExecute() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, applicableTranslations.execute());
	}

	@Test
	public void testUpdate_Null() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, applicableTranslations.update());

		verify(basicDAO, never()).findWhere(eq(AuditQuestion.class), anyString());
		verify(basicDAO, never()).save(any(AuditQuestion.class));
	}

	@Test
	public void testUpdate_Other() throws Exception {
		applicableTranslations.setType("Other");

		assertEquals(PicsActionSupport.SUCCESS, applicableTranslations.update());

		verify(basicDAO, never()).findWhere(eq(AuditQuestion.class), anyString());
		verify(basicDAO, never()).save(any(AuditQuestion.class));
	}

	@Test
	public void testUpdate_AuditQuestions() throws Exception {
		AuditQuestion expiredAuditQuestion = makeAuditQuestion();
		expiredAuditQuestion.setExpirationDate(new Date());

		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		questions.add(expiredAuditQuestion);

		when(basicDAO.findWhere(eq(AuditQuestion.class), anyString())).thenReturn(questions);

		applicableTranslations.setType("Audit Question");

		assertEquals(PicsActionSupport.SUCCESS, applicableTranslations.update());

		verify(database, times(2)).executeUpdate(anyString());
	}
}
