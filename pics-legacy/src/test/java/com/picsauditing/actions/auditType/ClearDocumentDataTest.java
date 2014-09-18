package com.picsauditing.actions.auditType;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClearDocumentDataTest extends PicsActionTest {
    private static final String TEST_SLUG = "SLUG";
    private static final int TEST_AUDIT_TYPE = 1;
    private static final int TEST_AUDIT_CATEGORY = 2;
    private static final int TEST_AUDIT_CATEGORY_2 = 3;
    private static final int TEST_AUDIT_QUESTION = 4;
    private static final int TEST_AUDIT_QUESTION_2 = 5;
    private static final int TEST_AUDIT_QUESTION_3 = 6;

	private ClearDocumentData clearDocumentData;

	@Mock
	private AuditTypeDAO auditTypeDAO;
	@Mock
	private AuditQuestionDAO auditQuestionDAO;

    private AuditType auditType;
    private AuditCategory category1;
    private AuditCategory category2;
    private AuditQuestion question1;
    private AuditQuestion question2;
    private AuditQuestion question3;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        clearDocumentData = new ClearDocumentData();
        super.setUp(clearDocumentData);
        Whitebox.setInternalState(clearDocumentData, "auditTypeDAO", auditTypeDAO);
        Whitebox.setInternalState(clearDocumentData, "auditQuestionDAO", auditQuestionDAO);

        auditType = new AuditType(TEST_AUDIT_TYPE);

        category1 = new AuditCategory();
        category1.setId(TEST_AUDIT_CATEGORY);
        auditType.getCategories().add(category1);

        question1 = new AuditQuestion();
        question1.setId(TEST_AUDIT_QUESTION);
        category1.getQuestions().add(question1);

        category2 = new AuditCategory();
        category2.setId(TEST_AUDIT_CATEGORY_2);
        auditType.getCategories().add(category2);

        question2 = new AuditQuestion();
        question2.setId(TEST_AUDIT_QUESTION_2);
        category2.getQuestions().add(question2);

        question3 = new AuditQuestion();
        question3.setId(TEST_AUDIT_QUESTION_3);
        category2.getQuestions().add(question3);

	}

    @Test
    public void testCopy() throws Exception {
        Whitebox.setInternalState(clearDocumentData, "slug", TEST_SLUG);

        List<AuditType> auditTypes = new ArrayList<>();
        auditTypes.add(auditType);
        when(auditTypeDAO.findBySlug(AuditType.class, TEST_SLUG)).thenReturn(auditTypes);

        String status = clearDocumentData.clear();
        assertEquals("success", status);
        verify(auditTypeDAO).remove(auditType);
        verify(auditTypeDAO).remove(category1);
        verify(auditTypeDAO).remove(category2);
        verify(auditTypeDAO).remove(question1);
        verify(auditTypeDAO).remove(question2);
        verify(auditTypeDAO).remove(question3);
    }

}
