package com.picsauditing.actions.audits;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.AnswerMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class AuditDataUploadTest extends PicsActionTest {
    private AuditDataUpload auditDataUpload;
    private User user;

    @Mock
    private ContractorAuditDAO auditDao;
    @Mock
    private ContractorAccountDAO contractorAccountDao;
    @Mock
    private ContractorAuditOperatorWorkflowDAO caoDAO;
    @Mock
    private ContractorAuditOperatorWorkflowDAO caowDAO;
    @Mock
    private AuditDataDAO auditDataDao;
    @Mock
    private AuditCategoryDataDAO catDataDao;
    @Mock
    private AuditDecisionTableDAO auditRuleDAO;
    @Mock
    private AuditQuestionDAO questionDao;
    @Mock
    private AuditCategoryRuleCache categoryRuleCache;
    @Mock
    private AuditPercentCalculator auditPercentCalculator;
    @Mock
    private AuditCategoriesBuilder builder;

    private ContractorAccount contractor;
    private OperatorAccount operator;
    private AuditData auditData;
    private AuditData auditDataForSignature;
    private ContractorAudit audit;
    private AnswerMap answerMap;
    private AuditCatData catData;


    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        auditDataUpload = Mockito.spy(new AuditDataUpload());
        doReturn(builder).when(auditDataUpload).initAuditCategoriesBuilder();
        when(builder.isCategoryApplicable(any(AuditCategory.class), any(ContractorAuditOperator.class))).thenReturn
                (true);
        super.setUp(auditDataUpload);
        PicsTestUtil.autowireDAOsFromDeclaredMocks(auditDataUpload, this);

        // make some entities
        user = EntityFactory.makeUser();
        when(permissions.getUserId()).thenReturn(user.getId());
        when(permissions.getLocale()).thenReturn(new Locale("en"));
        PicsTestUtil.forceSetPrivateField(auditDataUpload, "user", user);

        contractor = EntityFactory.makeContractor();
        audit = EntityFactory.makeContractorAudit(1, contractor);



        auditDataUpload.setAuditData(auditData);
        auditDataUpload.setContractor(contractor);

        catData = EntityFactory.makeAuditCatData();

        PicsTestUtil.forceSetPrivateField(auditDataUpload, "auditCategoryRuleCache", categoryRuleCache);
        PicsTestUtil.forceSetPrivateField(auditDataUpload, "auditPercentCalculator", auditPercentCalculator);
        PicsTestUtil.forceSetPrivateField(auditDataUpload, "conAudit", audit);

        when(auditDataDao.findAnswers(anyInt(), (Collection<Integer>) Matchers.anyObject())).thenReturn(answerMap);
        when(auditDao.find(anyInt())).thenReturn(audit);
    }

    private AuditData makeUpAnAnswer(String answerText, int questionId) {
        AuditData answer = EntityFactory.makeAuditData(answerText);
        answer.setAudit(audit);
        answer.getQuestion().setId(questionId);
        return answer;
    }

    private void makeUpASetOfCaos(int totalNumberOfCaosToCreate, int numberOfThemToMakeComplete) {
        List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();
        for (int i = 0; i < totalNumberOfCaosToCreate; i++) {
            operator = EntityFactory.makeOperator();

            final ContractorAuditOperator cao = new ContractorAuditOperator();
            cao.setOperator(operator);
            cao.setAudit(audit);
            if (i < numberOfThemToMakeComplete) {
                cao.setStatus(AuditStatus.Complete);
            } else {
                cao.setStatus(AuditStatus.Pending);
            }
            caos.add(cao);
        }
        audit.setOperators(caos);
    }

    @Test
    public void testSafetyManualUploadStatusAdjustments_OneCaoWithCompleteStatus() throws Exception {
        makeUpASetOfCaos(1, 1);
        auditData = makeUpAnAnswer("MySafetyManual.doc",AuditQuestion.MANUAL_PQF);
        auditDataForSignature = makeUpAnAnswer("John Doe / Supervisor",AuditQuestion.MANUAL_PQF_SIGNATURE);
        when(questionDao.find(AuditQuestion.MANUAL_PQF)).thenReturn(auditData.getQuestion());
        when(questionDao.find(AuditQuestion.MANUAL_PQF_SIGNATURE)).thenReturn(auditDataForSignature.getQuestion());
        when(auditDataDao.findAnswerByAuditQuestion(anyInt(), anyInt())).thenReturn
                (auditDataForSignature);

        Whitebox.invokeMethod(auditDataUpload, "safetyManualUploadStatusAdjustments", auditData);
        verify(builder, times(1)).calculate(any(ContractorAudit.class), anyCollectionOf(OperatorAccount.class));
        verify(auditDataDao, times(1)).save(any(AuditData.class));
    }
    @Test
    public void testSafetyManualUploadStatusAdjustments_OneCaoWithCompleteStatusAndOnePending() throws Exception {
        makeUpASetOfCaos(2, 1);
        auditData = makeUpAnAnswer("MySafetyManual.doc",AuditQuestion.MANUAL_PQF);
        auditDataForSignature = makeUpAnAnswer("John Doe / Supervisor",AuditQuestion.MANUAL_PQF_SIGNATURE);
        when(questionDao.find(AuditQuestion.MANUAL_PQF)).thenReturn(auditData.getQuestion());
        when(questionDao.find(AuditQuestion.MANUAL_PQF_SIGNATURE)).thenReturn(auditDataForSignature.getQuestion());
        when(auditDataDao.findAnswerByAuditQuestion(anyInt(), anyInt())).thenReturn
                (auditDataForSignature);

        Whitebox.invokeMethod(auditDataUpload, "safetyManualUploadStatusAdjustments", auditData);
        verify(builder, times(2)).calculate(any(ContractorAudit.class), anyCollectionOf(OperatorAccount.class));
        verify(auditDataDao, times(1)).save(any(AuditData.class));
    }
    @Test
    public void testSafetyManualUploadStatusAdjustments_TwoCaosBothPending() throws Exception {
        makeUpASetOfCaos(2, 0);
        auditData = makeUpAnAnswer("MySafetyManual.doc",AuditQuestion.MANUAL_PQF);
        auditDataForSignature = makeUpAnAnswer("John Doe / Supervisor",AuditQuestion.MANUAL_PQF_SIGNATURE);
        when(questionDao.find(AuditQuestion.MANUAL_PQF)).thenReturn(auditData.getQuestion());
        when(questionDao.find(AuditQuestion.MANUAL_PQF_SIGNATURE)).thenReturn(auditDataForSignature.getQuestion());
        when(auditDataDao.findAnswerByAuditQuestion(anyInt(), anyInt())).thenReturn
                (auditDataForSignature);

        Whitebox.invokeMethod(auditDataUpload, "safetyManualUploadStatusAdjustments", auditData);
        verify(builder, times(2)).calculate(any(ContractorAudit.class), anyCollectionOf(OperatorAccount.class));
        verify(auditDataDao, never()).save(any(AuditData.class));
    }
    @Test
    public void testSafetyManualUploadStatusAdjustments_NotTheManualPqfQuestion() throws Exception {
        makeUpASetOfCaos(2, 1);
        auditData = makeUpAnAnswer("SomeOther.doc",9999);
        when(questionDao.find(AuditQuestion.MANUAL_PQF)).thenReturn(auditData.getQuestion());

        Whitebox.invokeMethod(auditDataUpload, "safetyManualUploadStatusAdjustments", auditData);
        verify(auditDataDao, never()).findAnswerByAuditQuestion(anyInt(), anyInt());
        verify(builder, never()).calculate(any(ContractorAudit.class), anyCollectionOf(OperatorAccount.class));
        verify(auditDataDao, never()).save(any(AuditData.class));
    }
}
