package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.picsauditing.service.i18n.ExplicitUsageContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.util.test.TranslatorFactorySetup;

import static junit.framework.Assert.assertTrue;

public class AuditCategoryTest {

    private static final String TEST_SLUG = "TestSlug";
    private List<AuditCategory> list = new ArrayList<AuditCategory>();

	private AuditType auditType = EntityFactory.makeAuditType();

	@AfterClass
	public static void classTearDown() {
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Test
	public void testCompareTo() throws Exception {
		TranslatorFactorySetup.setupTranslatorFactoryForTest();

		buildCategory(2);
		AuditCategory cat = buildCategory(1);
		buildCategory(cat, 1);
		buildCategory(cat, 2);
		buildCategory(11);
		Collections.shuffle(list);
		Collections.sort(list);

		Assert.assertEquals("1", list.get(0).getFullNumber());
		Assert.assertEquals("1.1", list.get(1).getFullNumber());
		Assert.assertEquals("1.2", list.get(2).getFullNumber());
		Assert.assertEquals("2", list.get(3).getFullNumber());
		Assert.assertEquals("11", list.get(4).getFullNumber());
	}

	private AuditCategory buildCategory(int number) {
		AuditCategory cat = new AuditCategory();
		list.add(cat);
		cat.setNumber(number);
		cat.setAuditType(auditType);
		return cat;
	}

	private AuditCategory buildCategory(AuditCategory parent, int number) {
		AuditCategory cat = buildCategory(number);
		cat.setParent(parent);
		return cat;
	}

    @Test
    public void testContext_ReturnsExplicitUsageContextWithAuditTypeSlugAsPageName() throws Exception {
        auditType.setSlug(TEST_SLUG);
        AuditCategory cat = buildCategory(1);

        ExplicitUsageContext result = cat.context();

        assertTrue(TEST_SLUG.equals(result.pageName()));
    }

    @Test
    public void testContext_ReturnsExplicitUsageContextWithPaddedFullNumberAsPageOrder() throws Exception {
        auditType.setSlug(TEST_SLUG);
        AuditCategory cat = buildCategory(1);

        ExplicitUsageContext result = cat.context();

        assertTrue("001".equals(result.pageOrder()));
    }

}
