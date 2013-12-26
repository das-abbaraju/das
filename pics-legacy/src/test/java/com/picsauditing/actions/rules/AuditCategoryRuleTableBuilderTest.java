package com.picsauditing.actions.rules;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditCategoryRuleTableBuilderTest extends PicsActionTest {
    public static final int CATEGORY_ID = 12;
    public static final int AUDIT_TYPE_ID = 21;
    public static final int OPERATOR_ID = 3;
    public static final int PARENT_OPERATOR_ID = 4;
    @Mock
    private AuditDecisionTableDAO ruleDAO;
    @Mock
    private OperatorAccountDAO operatorDAO;
    @Mock
    private AuditCategoryDAO auditCategoryDAO;

    private AuditCategoryRuleTableBuilder auditCategoryRuleTableBuilder;

    @Before
    public void setup() {
        auditCategoryRuleTableBuilder = new AuditCategoryRuleTableBuilder();
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(auditCategoryRuleTableBuilder, "ruleDAO", ruleDAO);
        Whitebox.setInternalState(auditCategoryRuleTableBuilder, "operatorDAO", operatorDAO);
        Whitebox.setInternalState(auditCategoryRuleTableBuilder, "auditCategoryDAO", auditCategoryDAO);
    }

    @Test
    public void testFindRules_OperatorAccountSpecified(){
        AuditCategoryRule comparisonRule = AuditCategoryRule.builder()
                .include()
                .operator(buildOperator())
                .build();
        auditCategoryRuleTableBuilder.setComparisonRule(comparisonRule);

        ArgumentCaptor<String> whereClause = ArgumentCaptor.forClass(String.class);

        auditCategoryRuleTableBuilder.findRules();

        verify(ruleDAO).findWhere(eq(AuditCategoryRule.class), whereClause.capture(), eq(0));

        assertEquals("(t.effectiveDate < NOW()" +
                " AND t.expirationDate > NOW())" +
                " AND t.operatorAccount.id IN (" + OPERATOR_ID + "," + PARENT_OPERATOR_ID + ")",
                whereClause.getValue());
    }

    @Test
    public void testFindRules_OperatorAccountSpecified_AuditCategorySpecified(){
        AuditCategoryRule comparisonRule = AuditCategoryRule.builder()
                .include()
                .operator(buildOperator())
                .category(buildAuditCategory())
                .build();
        auditCategoryRuleTableBuilder.setComparisonRule(comparisonRule);

        ArgumentCaptor<String> whereClause = ArgumentCaptor.forClass(String.class);

        auditCategoryRuleTableBuilder.findRules();

        verify(ruleDAO).findWhere(eq(AuditCategoryRule.class), whereClause.capture(), eq(0));

        assertEquals("(t.effectiveDate < NOW() AND t.expirationDate > NOW())" +
                " AND (((t.auditType IS NULL OR t.auditType.id = " + AUDIT_TYPE_ID + ")" +
                " AND t.auditCategory IS NULL" +
                " AND (t.rootCategory = 1 OR t.rootCategory IS NULL))" +
                " OR t.auditCategory.id = " + CATEGORY_ID + ")" +
                " AND (t.operatorAccount IS NULL" +
                " OR t.operatorAccount.id IN (" + OPERATOR_ID + "," + PARENT_OPERATOR_ID + "))",
                whereClause.getValue());
    }

    private AuditCategory buildAuditCategory() {
        AuditCategory auditCategory = AuditCategory.builder()
                .id(CATEGORY_ID)
                .auditType(AuditType.builder()
                        .id(AUDIT_TYPE_ID)
                        .build())
                .build();
        when(auditCategoryDAO.find(CATEGORY_ID)).thenReturn(auditCategory);
        return auditCategory;
    }

    private OperatorAccount buildOperator() {
        OperatorAccount operator = OperatorAccount.builder()
                    .parentAccount(OperatorAccount.builder().id(PARENT_OPERATOR_ID).build())
                    .id(OPERATOR_ID).build();
        when(operatorDAO.find(OPERATOR_ID)).thenReturn(operator);
        return operator;
    }
}
