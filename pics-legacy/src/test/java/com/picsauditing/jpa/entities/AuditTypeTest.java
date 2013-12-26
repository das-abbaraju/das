package com.picsauditing.jpa.entities;

import com.picsauditing.service.i18n.ExplicitUsageContext;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class AuditTypeTest {
    private static final String TEST_SLUG = "TestSlug";
    private AuditType auditType;

    @Before
    public void setUp() throws Exception {
        auditType = new AuditType();
    }

    @Test
    public void testContext_ReturnsExplicitUsageContextWithAuditTypeSlugAsPageName() throws Exception {
        auditType.setSlug(TEST_SLUG);

        ExplicitUsageContext result = auditType.context();

        assertTrue(TEST_SLUG.equals(result.pageName()));
    }

    @Test
    public void testContext_ReturnsExplicitUsageContextWithNullPageOrder() throws Exception {
        auditType.setSlug(TEST_SLUG);

        ExplicitUsageContext result = auditType.context();

        assertNull(result.pageOrder());
    }
}
