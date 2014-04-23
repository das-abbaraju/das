package com.picsauditing.flagcalculator.util;

import com.picsauditing.flagcalculator.entities.AuditData;
import com.picsauditing.flagcalculator.entities.AuditOptionGroup;
import com.picsauditing.flagcalculator.entities.AuditQuestion;
import com.picsauditing.flagcalculator.entities.FlagCriteria;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class FlagAnswerParserTest {

	@Mock private FlagCriteria flagCriteria;
	@Mock private AuditData auditData;
	@Mock private AuditQuestion question;
	@Mock private AuditOptionGroup option;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testParseAnswer_CheckBox() {
		setupMocks("Check Box", "boolean", "X");
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("true", result);
	}
	
	@Test
	public void testParseAnswer_CheckBox_Not_Boolean_DataType() {
		setupMocks("Check Box", "bool", "X");
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("true", result);
	}
	
	@Test
	public void testParseAnswer_CheckBox_Not_Boolean_DataType_Null_Answer() {
		setupMocks("Check Box", "bool", null);
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("false", result);
	}
	
	@Test
	public void testParseAnswer_Manual_Null_Answer() {
		setupMocks("Manual", "boolean", null);
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertNull(result);
	}
	
	@Test
	public void testParseAnswer_Manual() {
		setupMocks("Manual", "string", "My Answer");
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("My Answer", result);
	}
	
	@Test
	public void testParseAnswer_MultipleChoice_YesNo() {		
		setupMocks("", "string", "My Answer");
		setupMocksMultipleChoice("YesNo", true);
		
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("My Answer", result);
	}
	
	@Test
	public void testParseAnswer_MultipleChoice_YesNoNA() {
		setupMocks("", "string", "Another Answer");
		setupMocksMultipleChoice("YesNoNA", true);
		
		String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
		assertEquals("Another Answer", result);
	}

    @Test
	public void testParseAnswer_Number() {
        setupMocks("Number", "number", "3");
        String result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
        assertEquals("3.0", result);

        setupMocks("Number", "number", "3.15");
        result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
        assertEquals("3.15", result);

        setupMocks("Number", "number", "any text");
        result = FlagAnswerParser.parseAnswer(flagCriteria, auditData);
        assertEquals("", result);
    }
	
	private void setupMocksMultipleChoice(String uniqueCode, boolean isMultipleChoice) {
		when(option.getUniqueCode()).thenReturn(uniqueCode);
		when(question.getOption()).thenReturn(option);
		when(auditData.getQuestion()).thenReturn(question);
//		when(auditData.isMultipleChoice()).thenReturn(isMultipleChoice);
	}
	
	private void setupMocks(String questionType, String criteriaType, String answer) {
		when(question.getQuestionType()).thenReturn(questionType);
		when(auditData.getQuestion()).thenReturn(question);
		when(auditData.getAnswer()).thenReturn(answer);
		when(flagCriteria.getDataType()).thenReturn(criteriaType);		
		
	}

}
