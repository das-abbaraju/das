package com.picsauditing.models.audits;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.util.SlugService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class InsuranceCategoryBuilderTest extends PicsTranslationTest {

    private Permissions permissions;
    @Mock
    private AuditTypeDAO typeDAO;
    @Mock
    private Database databaseForTesting;
	@Mock
	private SlugService slugService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(typeDAO.save((BaseTable) any())).thenReturn(null);
        permissions = Permissions.builder().userId(1).build();
    }

    @Test
    public void testBuild() throws Exception {
        AuditType insuranceType = AuditType.builder()
                .name("General Liability")
                .categories(AuditCategory.builder()
                        .name("General Liability")
                        .build())
                .build();
        OperatorAccount operator = OperatorAccount.builder()
                .name("Wolfram & Hart")
                .build();

        AuditCategory insuranceCategory = InsuranceCategoryBuilder.builder().build(typeDAO, insuranceType, permissions, operator);

        assertEquals(2, insuranceType.getCategories().size());
        assertEquals(operator.getName(), insuranceCategory.getName());
        assertEquals(insuranceCategory.getQuestions().get(0).getName(),
                "Upload a Certificate of Insurance or other supporting documentation for this policy.");
        assertEquals(insuranceCategory.getQuestions().get(1).getName(),
                "This insurance policy complies with all additional " + operator.getName() + " requirements.");
    }
}
