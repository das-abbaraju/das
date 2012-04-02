package com.picsauditing.PICS.flags;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;

public class MultiYearValueCalculatorTest {

	@Test
	public void addValues_EmptyList() {
		assertNull(MultiYearValueCalculator.addValues(null));
		assertNull(MultiYearValueCalculator.addValues(new ArrayList<Double>()));
	}
	
	@Test
	public void addValues() {
		List<Double> values = new ArrayList<Double>();
		values.add(1.0);
		values.add(2.0);
		assertEquals(new Double(3.0), MultiYearValueCalculator.addValues(values));		
	}
	
	@Test
	public void addValues_WithSomeNullsInList() {
		List<Double> values = new ArrayList<Double>();
		values.add(2.0);
		values.add(null);
		values.add(3.0);
		assertEquals(new Double(5.0), MultiYearValueCalculator.addValues(values));		
	}
	
	@Test
	public void addValues_AllNullsInList() {
		List<Double> values = new ArrayList<Double>();
		values.add(null);
		values.add(null);
		values.add(null);
		values.add(null);
		assertEquals(null, MultiYearValueCalculator.addValues(values));		
	}
	
	@Test
	public void getValueForSpecificYear_NullValueInList() {
		List<ContractorAudit> audits = Arrays.asList((ContractorAudit) null);
		assertNull(MultiYearValueCalculator.findValueForSpecificYear(audits, new FlagCriteria(), 1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getValueForSpecificYear_IllegalArgumentException() {
		MultiYearValueCalculator.findValueForSpecificYear(null, new FlagCriteria(), 0);
	}
	
	@Test
	public void getValueForSpecificYear_FailsValidationCheckAtBeginningOfMethod() {		
		List<ContractorAudit> audits = Arrays.asList((ContractorAudit) null);
		assertNull(MultiYearValueCalculator.findValueForSpecificYear(audits, new FlagCriteria(), 1));
		assertNull(MultiYearValueCalculator.findValueForSpecificYear(null, new FlagCriteria(), 1));
		assertNull(MultiYearValueCalculator.findValueForSpecificYear(new ArrayList<ContractorAudit>(), new FlagCriteria(), 1));
	}
	
	@Test
	public void calculateMultiYearSum() {
		
	}
	
	@Test
	public void totalNonNullValues_OnlyNullValues() {
		List<Double> values = Arrays.asList((Double) null, (Double) null);
		assertEquals(0, MultiYearValueCalculator.totalNonNullValues(values));
	}
	
	@Test
	public void totalNonNullValues_MixNullAndDoubleValues() {
		List<Double> values = Arrays.asList((Double) null, Double.valueOf(123), (Double) null, Double.valueOf(456));
		assertEquals(2, MultiYearValueCalculator.totalNonNullValues(values));
	}
	
	@Test
	public void totalNonNullValues_DoubleValuesOnly() {
		List<Double> values = Arrays.asList(Double.valueOf(123), Double.valueOf(456), Double.valueOf(5));
		assertEquals(3, MultiYearValueCalculator.totalNonNullValues(values));
	}
	
	@Test
	public void findValuesForMathematicalFunction_NoAudits() {
		assertNull(MultiYearValueCalculator.countSelectedCheckBoxes(new FlagCriteria(), null));
		assertNull(MultiYearValueCalculator.countSelectedCheckBoxes(new FlagCriteria(), new ArrayList<ContractorAudit>()));
	}
	
	@Test
	public void countSelectedCheckBoxes_NoAudits() {
		assertNull(MultiYearValueCalculator.countSelectedCheckBoxes(new FlagCriteria(), null));
		assertNull(MultiYearValueCalculator.countSelectedCheckBoxes(new FlagCriteria(), new ArrayList<ContractorAudit>()));
	}
	
	@Test
	public void getTotalCheckBoxCount_Valid_Checked_Answers_For_2_Questions() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(11);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		ContractorAudit audit = new ContractorAudit();
		audit.setData(setupAuditDataList());
		audits.add(audit);
		
		audit = new ContractorAudit();
		audit.setData(setupAuditDataList());
		audits.add(audit);
		
		assertEquals(Integer.valueOf(2), MultiYearValueCalculator.countSelectedCheckBoxes(criteria, audits));
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_FoundOne() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(11);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		int result = MultiYearValueCalculator.totalCheckBoxSelectedForQuestion(criteria, setupAuditDataList());
		assertEquals(1, result);
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_Not_Found() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(8);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		int result = MultiYearValueCalculator.totalCheckBoxSelectedForQuestion(criteria, setupAuditDataList());
		assertEquals(0, result);
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_Invalid_Answer() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(10);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		int result = MultiYearValueCalculator.totalCheckBoxSelectedForQuestion(criteria, setupAuditDataList());
		assertEquals(0, result);
	}

	@Test
	public void getValueFromAuditData_ValidNumericAnswer() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(9);
		criteria.setQuestion(question);
		criteria.setDataType(FlagCriteria.NUMBER);
		assertEquals(Double.valueOf(123), MultiYearValueCalculator.findValueInAuditData(criteria, setupAuditDataList()));
	}
	
	@Test
	public void getValueFromAuditData_InvalidNumericAnswer() {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(10);
		criteria.setQuestion(question);
		criteria.setDataType(FlagCriteria.NUMBER);
		assertEquals(Double.valueOf(0.0), MultiYearValueCalculator.findValueInAuditData(criteria, setupAuditDataList()));
	}
	
	private List<AuditData> setupAuditDataList() {
		List<AuditData> auditDataList = new ArrayList<AuditData>();
		
		AuditData auditData = EntityFactory.makeAuditData("123");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(9);
		auditData.setQuestion(question);
		auditDataList.add(auditData);
		
		auditData = EntityFactory.makeAuditData("ABC");
		question = EntityFactory.makeAuditQuestion();
		question.setId(10);
		auditData.setQuestion(question);
		auditDataList.add(auditData);
		
		auditData = EntityFactory.makeAuditData("X");
		question = EntityFactory.makeAuditQuestion();
		question.setId(11);
		auditData.setQuestion(question);
		auditDataList.add(auditData);
		
		return auditDataList;
	}

}
