package com.picsauditing.PICS.flags;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.google.common.collect.Lists;
import com.picsauditing.jpa.entities.*;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;

@PrepareForTest(MultiYearValueCalculator.class)
public class MultiYearValueCalculatorTest {

	@Test
	public void addValues_EmptyList() throws Exception {
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "addValues", (List<Double>) null));
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "addValues", new ArrayList<Double>()));
	}
	
	@Test
	public void addValues() throws Exception {
		List<Double> values = new ArrayList<Double>();
		values.add(1.0);
		values.add(2.0);
		assertEquals(new Double(3.0), Whitebox.invokeMethod(MultiYearValueCalculator.class, "addValues", values));		
	}
	
	@Test
	public void addValues_WithSomeNullsInList() throws Exception {
		List<Double> values = new ArrayList<Double>();
		values.add(2.0);
		values.add(null);
		values.add(3.0);
		assertEquals(new Double(5.0), Whitebox.invokeMethod(MultiYearValueCalculator.class, "addValues", values));		
	}
	
	@Test
	public void addValues_AllNullsInList() throws Exception {
		List<Double> values = new ArrayList<Double>();
		values.add(null);
		values.add(null);
		values.add(null);
		values.add(null);
		assertEquals(null, Whitebox.invokeMethod(MultiYearValueCalculator.class, "addValues", values));		
	}
	
	@Test
	public void getValueForSpecificYear_NullValueInList() throws Exception {
		List<ContractorAudit> audits = Arrays.asList((ContractorAudit) null);
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueForSpecificYear", audits, new FlagCriteria(), 1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getValueForSpecificYear_IllegalArgumentException() throws Exception {
		Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueForSpecificYear", (ContractorAudit) null, new FlagCriteria(), 0);
	}
	
	@Test
	public void getValueForSpecificYear_FailsValidationCheckAtBeginningOfMethod() throws Exception {		
		List<ContractorAudit> audits = Arrays.asList((ContractorAudit) null);
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueForSpecificYear", audits, new FlagCriteria(), 1));
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueForSpecificYear", (ContractorAudit) null, new FlagCriteria(), 1));
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueForSpecificYear", new ArrayList<ContractorAudit>(), new FlagCriteria(), 1));
	}
	
	@Test
	public void totalNonNullValues_OnlyNullValues() throws Exception {
		List<Double> values = Arrays.asList((Double) null, (Double) null);
		assertEquals(0, Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalNonNullValues", values));
	}
	
	@Test
	public void totalNonNullValues_MixNullAndDoubleValues() throws Exception {
		List<Double> values = Arrays.asList((Double) null, Double.valueOf(123), (Double) null, Double.valueOf(456));
		assertEquals(2, Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalNonNullValues", values));
	}
	
	@Test
	public void totalNonNullValues_DoubleValuesOnly() throws Exception {
		List<Double> values = Arrays.asList(Double.valueOf(123), Double.valueOf(456), Double.valueOf(5));
		assertEquals(3, Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalNonNullValues", values));
	}
	
	@Test
	public void findValuesForMathematicalFunction_NoAudits() throws Exception {
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "countSelectedCheckBoxes", new FlagCriteria(), null));
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "countSelectedCheckBoxes", new FlagCriteria(), new ArrayList<ContractorAudit>()));
	}
	
	@Test
	public void countSelectedCheckBoxes_NoAudits() throws Exception {
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "countSelectedCheckBoxes", new FlagCriteria(), null));
		assertNull(Whitebox.invokeMethod(MultiYearValueCalculator.class, "countSelectedCheckBoxes", new FlagCriteria(), new ArrayList<ContractorAudit>()));
	}
	
	@Test
	public void getTotalCheckBoxCount_Valid_Checked_Answers_For_2_Questions() throws Exception {
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
		
		assertEquals(Integer.valueOf(2), Whitebox.invokeMethod(MultiYearValueCalculator.class, "countSelectedCheckBoxes", criteria, audits));
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_FoundOne() throws Exception {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(11);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		Integer result = Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalCheckBoxSelectedForQuestion", criteria, setupAuditDataList());
		assertEquals(1, result.intValue());
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_Not_Found() throws Exception {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(8);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		Integer result = Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalCheckBoxSelectedForQuestion", criteria, setupAuditDataList());
		assertEquals(0, result.intValue());
	}
	
	@Test
	public void totalCheckBoxSelectedForQuestion_Invalid_Answer() throws Exception {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(10);
		question.setQuestionType("Check Box");
		criteria.setQuestion(question);
		Integer result = Whitebox.invokeMethod(MultiYearValueCalculator.class, "totalCheckBoxSelectedForQuestion", criteria, setupAuditDataList());
		assertEquals(0, result.intValue());
	}

	@Test
	public void testGetValueFromAuditData_ValidNumericAnswer() throws Exception {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(9);
		criteria.setQuestion(question);
		criteria.setDataType(FlagCriteria.NUMBER);
		assertEquals(Double.valueOf(123), Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueInAuditData", criteria, setupAuditDataList()));
	}
	
	@Test
	public void testGetValueFromAuditData_InvalidNumericAnswer() throws Exception {
		FlagCriteria criteria = new FlagCriteria();
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(10);
		criteria.setQuestion(question);
		criteria.setDataType(FlagCriteria.NUMBER);
		assertEquals(Double.valueOf(0.0), Whitebox.invokeMethod(MultiYearValueCalculator.class, "findValueInAuditData", criteria, setupAuditDataList()));
	}
	
	@Test
	public void testCalculateAverageEMR_EmptyList() {
		OshaResult result = MultiYearValueCalculator.calculateAverageEMR(new ArrayList<OshaResult>());
		assertTrue(result == null);
	}
	
	@Test
	public void testCalculateAverageEMR() {
		OshaResult value1 = new OshaResult.Builder().year("2010").answer("3").build();
		OshaResult value2 = new OshaResult.Builder().year("2011").verified(false).answer("4").build();
		OshaResult value3 = new OshaResult.Builder().year("2012").answer("5.0").build();
		
		List<OshaResult> oshaResults = new ArrayList<OshaResult>(Arrays.asList(value1, value2, value3));
		OshaResult oshaResult = MultiYearValueCalculator.calculateAverageEMR(oshaResults);
		
		assertFalse(oshaResult.isVerified());
		assertEquals("2010, 2011, 2012", oshaResult.getYear());
		assertEquals("4.0", oshaResult.getAnswer());
	}

    @Test
    public void testBuildOshaResult() throws Exception {
        List<ContractorAudit> annualUpdates = Arrays.asList(
                mockAnnualUpdateWithEmrData("2010", "1.03"),
                mockAnnualUpdateWithEmrData("2011", "1"),
                mockAnnualUpdateWithEmrData("2012", ".94")
         );

        Map<String, OshaResult> results = Whitebox.invokeMethod(MultiYearValueCalculator.class
        , "buildOshaResultsList", annualUpdates);

        assertEquals(3, results.size());
    }

    @Test
    public void testBuildOshaResult_TwoYearsOfEmrData() throws Exception {
        List<ContractorAudit> annualUpdates = Arrays.asList(
                mockAnnualUpdateWithEmrData("2010", "1.03"),
                mockAnnualUpdateWithEmrData("2011", "1")
         );

        Map<String, OshaResult> results = Whitebox.invokeMethod(MultiYearValueCalculator.class
        , "buildOshaResultsList", annualUpdates);

        assertEquals(2, results.size());
    }

    @Test
    public void testBuildOshaResult_TwoYearsOfEmrDataWithSkippedYear() throws Exception {
        List<ContractorAudit> annualUpdates = Arrays.asList(
                mockAnnualUpdateWithEmrData("2010", "1.03"),
                mockAnnualUpdateWithEmrData("2012", ".94")
         );

        Map<String, OshaResult> results = Whitebox.invokeMethod(MultiYearValueCalculator.class
        , "buildOshaResultsList", annualUpdates);

        assertEquals(2, results.size());
    }

    @Test
    public void testBuildOshaResult_FourYearsOfEmrData() throws Exception {
        List<ContractorAudit> annualUpdates = Arrays.asList(
                mockAnnualUpdateWithEmrData("2009", "1.07"),
                mockAnnualUpdateWithEmrData("2010", "1.03"),
                mockAnnualUpdateWithEmrData("2011", "1"),
                mockAnnualUpdateWithEmrData("2012", ".94")
         );

        Map<String, OshaResult> results = Whitebox.invokeMethod(MultiYearValueCalculator.class
        , "buildOshaResultsList", annualUpdates);

        assertEquals(3, results.size());
    }

    @Test
    public void testBuildOshaResult_NoYearsOfData() throws Exception {
        Map<String, OshaResult> results = Whitebox.invokeMethod(MultiYearValueCalculator.class
                , "buildOshaResultsList", Collections.emptyList());

        assertEquals(0, results.size());
    }

    private ContractorAudit mockAnnualUpdateWithEmrData(String year, String emrAnswer) {
        ContractorAudit annualUpdate = Mockito.mock(ContractorAudit.class);
        when(annualUpdate.hasCaoStatus(AuditStatus.Complete)).thenReturn(true);
        when(annualUpdate.getAuditFor()).thenReturn(year);

        AuditQuestion emrQuestion = Mockito.mock(AuditQuestion.class);

        when(emrQuestion.getId()).thenReturn(2034);
        when(emrQuestion.isVisibleInAudit(annualUpdate)).thenReturn(true);

        AuditData emr = Mockito.mock(AuditData.class);
        when(emr.getQuestion()).thenReturn(emrQuestion);
        when(emr.getAnswer()).thenReturn(emrAnswer);

        when(annualUpdate.getData()).thenReturn((Arrays.asList(emr)));

        return annualUpdate;
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
