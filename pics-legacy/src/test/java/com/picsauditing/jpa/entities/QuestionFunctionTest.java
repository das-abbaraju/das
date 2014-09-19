package com.picsauditing.jpa.entities;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.util.AnswerMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class QuestionFunctionTest extends PicsTest {
    private Collection<AuditQuestionFunctionWatcher> watchers = null;
    private QuestionFunction.FunctionInput input;
    private AnswerMap answerMap = null;
    private int curId = 0;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        watchers = new ArrayList<>();
        answerMap = new AnswerMap(new ArrayList<AuditData>());

        curId = 0;
        input = new QuestionFunction.FunctionInput.Builder().answerMap(answerMap).watchers(watchers).build();
    }

    @Test
    public void testEvaluateSimpleMath_Parens() throws Exception {
        input.setExpression("A/(B-C)");
        addWatcherParameters("A", "6.0");
        addWatcherParameters("B", "4.0");
        addWatcherParameters("C", "2.0");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("3.00", result.toString());
    }

    @Test
    public void testEvaluateSimpleMath_ManyParens() throws Exception {
        input.setExpression("((((A/(B-C) + 3)))*C)");
        addWatcherParameters("A", "6.0");
        addWatcherParameters("B", "4.0");
        addWatcherParameters("C", "2.0");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("12.00", result.toString());
    }

    @Test
    public void testEvaluateSimpleMath_GeneralFormGroupAddMultiplyDivision() throws Exception {
        input.setExpression("(A + B) * 2 / 5");
        addWatcherParameters("A", "6.0");
        addWatcherParameters("B", "4.0");
        addWatcherParameters("C", "2.0");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("4.00", result.toString());
    }

    @Test
    public void testEvaluateSimpleMath_MultiOverAddition() throws Exception {
        input.setExpression("A+B*C");
        addWatcherParameters("A", "6.0");
        addWatcherParameters("B", "4.0");
        addWatcherParameters("C", "2.0");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("14.00", result.toString());
    }

    @Test
    public void testEvaluateSimpleMath_AllContants() throws Exception {
        input.setExpression("2+3");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("5.00", result.toString());
    }

    @Test
    public void testEvaluateSimpleMath_MixedOperandTypes() throws Exception {
        input.setExpression("A+B*C * 2");
        addWatcherParameters("A", "6.0");
        addWatcherParameters("B", "4.0");
        addWatcherParameters("C", "2.0");
        Object result = QuestionFunction.SIMPLE_MATH.evaluateSimpleMath(input);
        assertNotNull(result);
        assertEquals("22.00", result.toString());
    }

    private void addWatcherParameters(String identifier, String answer) {
        AuditQuestionFunctionWatcher watcher = new AuditQuestionFunctionWatcher();
        AuditQuestion question = new AuditQuestion();
        question.setId(curId);
        question.setUniqueCode(identifier);
        watcher.setUniqueCode(identifier);

        AuditData data = new  AuditData();
        data.setAnswer(answer);
        data.setQuestion(question);

        watcher.setQuestion(question);
        watchers.add(watcher);
        answerMap.add(data);
        curId++;
    }
}
