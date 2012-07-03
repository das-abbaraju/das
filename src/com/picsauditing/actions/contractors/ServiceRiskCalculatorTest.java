package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.LowMedHigh;

public class ServiceRiskCalculatorTest {
	private final String YES = "Yes";
	private final String NO = "No";

	private ServiceRiskCalculator serviceRiskCalculator;
	private AuditData auditData;

	@Before
	public void setUp() throws Exception {
		serviceRiskCalculator = new ServiceRiskCalculator();

		auditData = new AuditData();
		auditData.setQuestion(new AuditQuestion());
	}

	// Can all the work you perform or the services you provide be conducted
	// from an office at all times?
	// Yes=Low, No=Medium
	@Test
	public void testSafety_Offices() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do any of your employees ever use hand, power, pneumatic or hydraulic
	// tools? i.e. drill, circular saw, jackhammer, grinders, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_HandTools() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_HAND_POWER_PNEUMATIC_TOOLS);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Are your employees required to wear any personal protective equipment
	// (PPE)? i.e. safety shoes, safety glasses, fire retardant clothing, hard
	// hat, gloves, hearing protection, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_PPE() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Are you required to complete or obtain a "permit to work" in order to
	// conduct ANY of your operations? i.e. confined space permit, hot work
	// permit, etc.
	// Yes=High, No=Medium
	@Test
	public void testSafety_PermitToWork() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_PERMIT_TO_WORK);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Do you use any mobile equipment to carry out your operations? i.e.
	// cranes, forklifts, aerial lifts, bulldozers, bobcats, front end loaders,
	// etc.
	// Yes=High, No=Medium
	@Test
	public void testSafety_MobileEquipment() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_MOBILE_EQUIPMENT);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
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
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_PERFORMS_HIGH_RISK);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// Can failures in your products result in a work stoppage or major business
	// interruption for your customer?
	// Yes=High, No=Low
	@Test
	public void testProduct_FailureWorkStoppage() {
		auditData.getQuestion().setId(ServiceRiskCalculator.PRODUCT_FAILURE_WORK_STOPPAGE);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// If you fail to deliver your products on-time, can it result in a work
	// stoppage or major business interruption for your customer?
	// Yes=High, No=Low
	@Test
	public void testProduct_DeliveryWorkStoppage() {
		auditData.getQuestion().setId(ServiceRiskCalculator.PRODUCT_DELIVERY_WORK_STOPPAGE);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	@Test
	public void testSafetyHighestRiskLevel() {
		List<AuditData> yesToEverything = createSafetyAuditDataAllAnswers(YES);
		List<AuditData> noToEverything = createSafetyAuditDataAllAnswers(NO);
		List<AuditData> mixedLowRisk = createSafetyAuditDataAnswersMixed(false);
		List<AuditData> mixedHighRisk = createSafetyAuditDataAnswersMixed(true);

		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(yesToEverything).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevel(noToEverything).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevel(mixedLowRisk).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(mixedHighRisk).get(ServiceRiskCalculator.SAFETY));
	}

	@Test
	public void testProductHighestRiskLevel() {
		List<AuditData> yesToEverything = createProductAuditDataAllAnswers(YES);
		List<AuditData> noToEverything = createProductAuditDataAllAnswers(NO);
		List<AuditData> mixed1 = createProductAuditDataAnswersMixedEvenYes();
		List<AuditData> mixed2 = createProductAuditDataAnswersMixedOddYes();

		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(yesToEverything).get(ServiceRiskCalculator.PRODUCT));
		Assert.assertEquals(LowMedHigh.Low,
				serviceRiskCalculator.getHighestRiskLevel(noToEverything).get(ServiceRiskCalculator.PRODUCT));
		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(mixed1).get(ServiceRiskCalculator.PRODUCT));
		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(mixed2).get(ServiceRiskCalculator.PRODUCT));
	}
	
	@Test
	public void testSelfEvaluations() {
		AuditData safetyLow = createAuditData(ServiceRiskCalculator.SAFETY_SELF_EVALUATION);
		safetyLow.setAnswer("Low");
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(safetyLow));
		
		AuditData safetyMedium = createAuditData(ServiceRiskCalculator.SAFETY_SELF_EVALUATION);
		safetyMedium.setAnswer("Medium");
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(safetyMedium));
		
		AuditData safetyHigh = createAuditData(ServiceRiskCalculator.SAFETY_SELF_EVALUATION);
		safetyHigh.setAnswer("High");
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(safetyHigh));
		
		AuditData productLow = createAuditData(ServiceRiskCalculator.PRODUCT_SELF_EVALUATION);
		productLow.setAnswer("Low");
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(productLow));
		
		AuditData productMedium = createAuditData(ServiceRiskCalculator.PRODUCT_SELF_EVALUATION);
		productMedium.setAnswer("Medium");
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(productMedium));
		
		AuditData productHigh = createAuditData(ServiceRiskCalculator.PRODUCT_SELF_EVALUATION);
		productHigh.setAnswer("High");
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(productHigh));
	}

	private List<AuditData> createSafetyAuditDataAllAnswers(String answer) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (Integer questionID : ServiceRiskCalculator.SAFETY_QUESTIONS) {
			AuditData auditData = createAuditData(questionID);
			auditData.setAnswer(answer);

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createSafetyAuditDataAnswersMixed(boolean highRisk) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (Integer questionID : ServiceRiskCalculator.SAFETY_QUESTIONS) {
			AuditData auditData = createAuditData(questionID);
			// Lower risk questions
			auditData.setAnswer(highRisk ? NO : YES);

			// Higher risk questions
			if (questionID >= ServiceRiskCalculator.SAFETY_PERMIT_TO_WORK) {
				auditData.setAnswer(highRisk ? YES : NO);
			}

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createProductAuditDataAllAnswers(String answer) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (Integer questionID : ServiceRiskCalculator.PRODUCT_QUESTIONS) {
			AuditData auditData = createAuditData(questionID);
			auditData.setAnswer(answer);

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createProductAuditDataAnswersMixedEvenYes() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (Integer questionID : ServiceRiskCalculator.PRODUCT_QUESTIONS) {
			AuditData auditData = createAuditData(questionID);
			auditData.setAnswer(questionID % 2 == 0 ? YES : NO);

			auditDatas.add(auditData);
		}

		return auditDatas;

	}

	private List<AuditData> createProductAuditDataAnswersMixedOddYes() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (Integer questionID : ServiceRiskCalculator.PRODUCT_QUESTIONS) {
			AuditData auditData = createAuditData(questionID);
			auditData.setAnswer(questionID % 2 == 1 ? YES : NO);

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private AuditData createAuditData(int questionID) {
		AuditData auditData = new AuditData();
		auditData.setQuestion(new AuditQuestion());
		auditData.getQuestion().setId(questionID);
		return auditData;
	}
}
