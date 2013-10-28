package com.picsauditing.util;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dr.domain.fields.QueryFilterOperator;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.PermissionService;
import com.picsauditing.service.ReportPreferencesService;
import com.picsauditing.service.ReportSearchResults;
import com.picsauditing.service.ReportService;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class SlugServiceTest extends PicsTranslationTest {

	private SlugService slugService;

	@Mock
	private PicsDAO basicDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		slugService = new SlugService();

		setInternalState(slugService, "basicDao", basicDao);
	}

    @Test
    public void testGenerateSlug_firstRemoveHyphens()
            throws Exception {
        when(basicDao.findBySlug(AuditType.class, eq(any(String.class)))).thenReturn(new ArrayList<AuditType>());
        String slug = null;
        slug = slugService.generateSlug(AuditType.class, "Manual-Audit",2);
        assertEquals("manualaudit",slug);
    }

    @Test
	public void testGenerateSlug_SecondSpacesToHyphen()
			throws Exception {
        when(basicDao.findBySlug(AuditType.class, eq(any(String.class)))).thenReturn(new ArrayList<AuditType>());
        String slug = null;
        slug = slugService.generateSlug(AuditType.class, "Manual Audit",2);
        assertEquals("manual-audit",slug);
	}

    @Test
    public void testGenerateSlug_ThirdStrictAlpha()
            throws Exception {
        when(basicDao.findBySlug(AuditType.class, eq(any(String.class)))).thenReturn(new ArrayList<AuditType>());
        String slug = null;
        slug = slugService.generateSlug(AuditType.class, "Manual      Audit###",2);
        assertEquals("manual-audit",slug);
    }

    @Test
    public void testSlugHasDuplicate_No()
            throws Exception {
        when(basicDao.findBySlug(AuditQuestion.class, "slug")).thenReturn(new ArrayList<AuditQuestion>());
        assertFalse(slugService.slugHasDuplicate(AuditQuestion.class, "slug", 0));
    }

    @Test
    public void testSlugHasDuplicate_Itself()
            throws Exception {
        ArrayList<AuditQuestion> questions = new ArrayList<AuditQuestion>();
        AuditQuestion question = new AuditQuestion();
        question.setId(1);
        questions.add(question);
        when(basicDao.findBySlug(AuditQuestion.class, "slug")).thenReturn(questions);
        assertFalse(slugService.slugHasDuplicate(AuditQuestion.class, "slug", 1));
    }

    @Test
    public void testSlugHasDuplicate_Duplicate()
            throws Exception {
        ArrayList<AuditQuestion> questions = new ArrayList<AuditQuestion>();
        AuditQuestion question = new AuditQuestion();
        question.setId(1);
        questions.add(question);
        when(basicDao.findBySlug(AuditQuestion.class, "slug")).thenReturn(questions);
        assertTrue(slugService.slugHasDuplicate(AuditQuestion.class, "slug", 2));
    }

    @Test
    public void testSlugIsURICompliant()
            throws Exception {
        assertTrue(slugService.slugIsURICompliant("manual-audit"));
        assertTrue(slugService.slugIsURICompliant("Manual-Audit"));
        assertFalse(slugService.slugIsURICompliant("Manual Audit"));
        assertFalse(slugService.slugIsURICompliant("!@#$%^&*()_+-=--------"));
    }
}
