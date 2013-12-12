package com.picsauditing.jpa.entities;

import com.picsauditing.service.i18n.ExplicitUsageContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuditQuestionTest {
    private static final String TEST_SLUG = "TestSlug";
    private AuditQuestion auditQuestion;

    @Mock
    private AuditCategory auditCategory;
    @Mock
    private AuditType auditType;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        auditQuestion = new AuditQuestion();

        auditQuestion.setNumber(1);
        auditQuestion.setCategory(auditCategory);
        when(auditType.getSlug()).thenReturn(TEST_SLUG);
        when(auditCategory.getAuditType()).thenReturn(auditType);
        when(auditCategory.getFullNumber()).thenReturn("2.3");
    }

    @Test
    public void testContext_ReturnsExplicitUsageContextWithAuditTypeSlugAsPageName() throws Exception {
        ExplicitUsageContext result = auditQuestion.context();

        assertTrue(TEST_SLUG.equals(result.pageName()));
    }

    @Test
    public void testContext_ReturnsExplicitUsageContextWithPaddedFullNumberAsPageOrder() throws Exception {
        ExplicitUsageContext result = auditQuestion.context();

        assertTrue("002.003.001".equals(result.pageOrder()));
    }
}
