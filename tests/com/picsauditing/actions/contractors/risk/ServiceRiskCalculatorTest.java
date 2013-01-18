package com.picsauditing.actions.contractors.risk;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator.RiskCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.LowMedHigh;

/**
 * Testing mostly business logic here
 * 
 * @author Lani Aung &lt;uaung@picsauditing.com&gt;
 * 
 */
public class ServiceRiskCalculatorTest {
	private ServiceRiskCalculator serviceRiskCalculator;
	private final String YES = "Yes";
	private final String NO = "No";

	@Mock
	private AuditData auditData;
	@Mock
	private AuditQuestion question;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		serviceRiskCalculator = new ServiceRiskCalculator();

		when(auditData.getQuestion()).thenReturn(question);
	}

	// Can all the work you perform or the services you provide be conducted
	// from an office at all times?
	// Yes=Low, No=Medium
	@Test
	public void testSafety_Offices() {
		when(question.getId()).thenReturn(SafetyAssessment.CONDUCTED_FROM_OFFICE.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do any of your employees ever use hand, power, pneumatic or hydraulic
	// tools? i.e. drill, circular saw, jackhammer, grinders, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_HandTools() {
		when(question.getId()).thenReturn(SafetyAssessment.HAND_POWER_PNEUMATIC_TOOLS.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Are your employees required to wear any personal protective equipment
	// (PPE)? i.e. safety shoes, safety glasses, fire retardant clothing, hard
	// hat, gloves, hearing protection, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_PPE() {
		when(question.getId()).thenReturn(SafetyAssessment.PERSONAL_PROTECTIVE_EQUIPMENT.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Are you required to complete or obtain a "permit to work" in order to
	// conduct ANY of your operations? i.e. confined space permit, hot work
	// permit, etc.
	// Yes=High, No=Medium
	@Test
	public void testSafety_PermitToWork() {
		when(question.getId()).thenReturn(SafetyAssessment.PERMIT_TO_WORK.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do you use any mobile equipment to carry out your operations? i.e.
	// cranes, forklifts, aerial lifts, bulldozers, bobcats, front end loaders,
	// etc.
	// Yes=High, No=Medium
	@Test
	public void testSafety_MobileEquipment() {
		when(question.getId()).thenReturn(SafetyAssessment.MOBILE_EQUIPMENT.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Regardless of the client site you are registering for, does your company
	// at ANYTIME perform any of the following: abatement/remediation
	// (asbestos/lead), confined space, demolition, lockout/tagout, work at
	// heights, mobile crane operation, electrical work, hazardous waste
	// operations, hot work, work on, over or under water, rigging or trenching
	// and excavations?
	// Yes=High, Medium=No
	@Test
	public void testSafety_PerformsHighRisk() {
		when(question.getId()).thenReturn(SafetyAssessment.PERFORMS_HIGH_RISK.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Can failures in your products result in a work stoppage or major business
	// interruption for your customer?
	// Yes=High, No=Low
	@Test
	public void testProduct_FailureWorkStoppage() {
		when(question.getId()).thenReturn(ProductAssessment.FAILURE_WORK_STOPPAGE.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// If you fail to deliver your products on-time, can it result in a work
	// stoppage or major business interruption for your customer?
	// Yes=High, No=Low
	@Test
	public void testProduct_DeliveryWorkStoppage() {
		when(question.getId()).thenReturn(ProductAssessment.DELIVERY_WORK_STOPPAGE.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do your drivers only deliver to a warehouse or administrative offices at
	// a Client Site?
	// Yes=Low, No=Medium
	@Test
	public void testTransportation_DeliverToWarehouseOrAdministrativeOffice() {
		when(question.getId()).thenReturn(
				TransportationAssessment.DELIVER_TO_WAREHOUSE_OR_ADMINISTRATIVE_OFFICE.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do you transport over-sized/permit-required loads?
	// Yes=Medium, No=Low
	@Test
	public void testTransportation_OversizedPermitRequiredLoads() {
		when(question.getId()).thenReturn(TransportationAssessment.OVERSIZED_OR_PERMIT_REQUIRED_LOADS.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do your Drivers load or offload material or operate equipment to load or
	// offload at any Client facility? (i.e., de-attaching straps, connecting
	// hoses, valves, piping, drilling equipment, Cranes, Excavators,
	// Bulldozers, etc.)
	// Yes=High, No=Low
	@Test
	public void testTransportation_LoadOffloadAtClientFacility() {
		when(question.getId()).thenReturn(TransportationAssessment.LOAD_OFFLOAD_AT_CLIENT_FACILITY.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do your drivers transport AND offload Hazardous and potentially Hazardous
	// materials that would require an MSDS (Material Safety Data Sheet)?
	// Yes=High, No=Low
	@Test
	public void testTransportation_TransportAndOffloadHazardousMaterials() {
		when(question.getId()).thenReturn(
				TransportationAssessment.TRANSPORT_AND_OFFLOAD_HAZARDOUS_MATERIALS.getQuestionID());

		when(auditData.getAnswer()).thenReturn(YES);
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		when(auditData.getAnswer()).thenReturn(NO);
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	@Test
	public void testSafetyHighestRiskLevel() {
		List<AuditData> yesToEverything = createAuditDataAllAnswers(RiskCategory.SAFETY, YES);
		List<AuditData> noToEverything = createAuditDataAllAnswers(RiskCategory.SAFETY, NO);

		assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevelMap(yesToEverything).get(RiskCategory.SAFETY));
		assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevelMap(noToEverything).get(RiskCategory.SAFETY));
	}

	@Test
	public void testProductHighestRiskLevel() {
		List<AuditData> yesToEverything = createAuditDataAllAnswers(RiskCategory.PRODUCT, YES);
		List<AuditData> noToEverything = createAuditDataAllAnswers(RiskCategory.PRODUCT, NO);

		assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevelMap(yesToEverything).get(RiskCategory.PRODUCT));
		assertEquals(LowMedHigh.Low,
				serviceRiskCalculator.getHighestRiskLevelMap(noToEverything).get(RiskCategory.PRODUCT));
	}

	@Test
	public void testTransportationHighestRiskLevel() {
		List<AuditData> yesToEverything = createAuditDataAllAnswers(RiskCategory.TRANSPORTATION, YES);
		List<AuditData> noToEverything = createAuditDataAllAnswers(RiskCategory.TRANSPORTATION, NO);

		assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevelMap(yesToEverything).get(RiskCategory.TRANSPORTATION));
		assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevelMap(noToEverything).get(RiskCategory.TRANSPORTATION));
	}

	@Test
	public void testSelfEvaluations() {
		int safety = SafetyAssessment.LEVEL_OF_HAZARD_EXPOSURE.getQuestionID();
		AuditData safetyLow = createAuditData(safety);
		when(safetyLow.getAnswer()).thenReturn("Low");
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(safetyLow));

		AuditData safetyMedium = createAuditData(safety);
		when(safetyMedium.getAnswer()).thenReturn("Medium");
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(safetyMedium));

		AuditData safetyHigh = createAuditData(safety);
		when(safetyHigh.getAnswer()).thenReturn("High");
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(safetyHigh));

		int product = ProductAssessment.RISK_ON_HEALTH_SAFETY.getQuestionID();
		AuditData productLow = createAuditData(product);
		when(productLow.getAnswer()).thenReturn("Low");
		assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(productLow));

		AuditData productMedium = createAuditData(product);
		when(productMedium.getAnswer()).thenReturn("Medium");
		assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(productMedium));

		AuditData productHigh = createAuditData(product);
		when(productHigh.getAnswer()).thenReturn("High");
		assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(productHigh));
	}

	@Test
	public void testNonAssessmentQuestion() {
		AuditData nonAssessmentQuestion = createAuditData(1);

		when(nonAssessmentQuestion.getAnswer()).thenReturn("High");

		assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(nonAssessmentQuestion));
	}

	@Test
	public void testNullAnswer() {
		AuditData noAnswer = createAuditData(12345);
		assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(noAnswer));
	}

	@Test
	public void testBadAnswer() {
		AuditData badAnswer = createAuditData(12345);

		when(badAnswer.getAnswer()).thenReturn("Hello");
		assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(badAnswer));
	}

	@Test
	public void testGetCategory() throws Exception {
		SafetyAssessment selfSafety = SafetyAssessment.LEVEL_OF_HAZARD_EXPOSURE;
		SafetyAssessment safety = SafetyAssessment.CONDUCTED_FROM_OFFICE;

		ProductAssessment selfProduct = ProductAssessment.RISK_ON_HEALTH_SAFETY;
		ProductAssessment product = ProductAssessment.DELIVERY_WORK_STOPPAGE;

		TransportationAssessment transportation = TransportationAssessment.DELIVER_TO_WAREHOUSE_OR_ADMINISTRATIVE_OFFICE;

		assertEquals(RiskCategory.SELF_SAFETY, Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", selfSafety));
		assertEquals(RiskCategory.SAFETY, Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", safety));
		assertEquals(RiskCategory.SELF_PRODUCT,
				Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", selfProduct));
		assertEquals(RiskCategory.PRODUCT, Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", product));
		assertEquals(RiskCategory.TRANSPORTATION,
				Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", transportation));
	}

	@Test(expected = Exception.class)
	public void testGetCategory_Null() throws Exception {
		Whitebox.invokeMethod(serviceRiskCalculator, "getCategory", (RiskAssessment) null);
	}

	private List<AuditData> createAuditDataAllAnswers(RiskCategory category, String answer) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		RiskAssessment[] assessments = SafetyAssessment.values();
		if (RiskCategory.PRODUCT == category) {
			assessments = ProductAssessment.values();
		} else if (RiskCategory.TRANSPORTATION == category) {
			assessments = TransportationAssessment.values();
		}

		for (RiskAssessment assessment : assessments) {
			if (!assessment.isQuestionSelfEvaluation()) {
				AuditData auditData = createAuditData(assessment.getQuestionID());
				when(auditData.getAnswer()).thenReturn(answer);
				auditDatas.add(auditData);
			}
		}

		return auditDatas;
	}

	private AuditData createAuditData(int questionID) {
		AuditData auditData = mock(AuditData.class);
		AuditQuestion question = mock(AuditQuestion.class);

		when(auditData.getQuestion()).thenReturn(question);
		when(question.getId()).thenReturn(questionID);

		return auditData;
	}
}
