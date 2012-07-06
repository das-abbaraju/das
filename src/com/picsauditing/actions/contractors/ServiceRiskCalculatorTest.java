package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.actions.contractors.ServiceRiskCalculator.RiskCategory;
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
		auditData.getQuestion().setId(SafetyAssessment.CONDUCTED_FROM_OFFICE.getQuestionID());

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
		auditData.getQuestion().setId(SafetyAssessment.HAND_POWER_PNEUMATIC_TOOLS.getQuestionID());

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
		auditData.getQuestion().setId(SafetyAssessment.PERSONAL_PROTECTIVE_EQUIPMENT.getQuestionID());

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
		auditData.getQuestion().setId(SafetyAssessment.PERMIT_TO_WORK.getQuestionID());

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
		auditData.getQuestion().setId(SafetyAssessment.MOBILE_EQUIPMENT.getQuestionID());

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
		auditData.getQuestion().setId(SafetyAssessment.PERFORMS_HIGH_RISK.getQuestionID());

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
		auditData.getQuestion().setId(ProductAssessment.FAILURE_WORK_STOPPAGE.getQuestionID());

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
		auditData.getQuestion().setId(ProductAssessment.DELIVERY_WORK_STOPPAGE.getQuestionID());

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
				serviceRiskCalculator.getHighestRiskLevel(yesToEverything).get(RiskCategory.SAFETY));
		Assert.assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevel(noToEverything).get(RiskCategory.SAFETY));
		Assert.assertEquals(LowMedHigh.Med,
				serviceRiskCalculator.getHighestRiskLevel(mixedLowRisk).get(RiskCategory.SAFETY));
		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(mixedHighRisk).get(RiskCategory.SAFETY));
	}

	@Test
	public void testProductHighestRiskLevel() {
		List<AuditData> yesToEverything = createProductAuditDataAllAnswers(YES);
		List<AuditData> noToEverything = createProductAuditDataAllAnswers(NO);
		List<AuditData> mixed1 = createProductAuditDataAnswersMixedEvensYes(true);
		List<AuditData> mixed2 = createProductAuditDataAnswersMixedEvensYes(false);

		Assert.assertEquals(LowMedHigh.High,
				serviceRiskCalculator.getHighestRiskLevel(yesToEverything).get(RiskCategory.PRODUCT));
		Assert.assertEquals(LowMedHigh.Low,
				serviceRiskCalculator.getHighestRiskLevel(noToEverything).get(RiskCategory.PRODUCT));
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getHighestRiskLevel(mixed1)
				.get(RiskCategory.PRODUCT));
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getHighestRiskLevel(mixed2)
				.get(RiskCategory.PRODUCT));
	}

	@Test
	public void testSelfEvaluations() {
		int safety = SafetyAssessment.LEVEL_OF_HAZARD_EXPOSURE.getQuestionID();
		AuditData safetyLow = createAuditData(safety);
		safetyLow.setAnswer("Low");
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(safetyLow));

		AuditData safetyMedium = createAuditData(safety);
		safetyMedium.setAnswer("Medium");
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(safetyMedium));

		AuditData safetyHigh = createAuditData(safety);
		safetyHigh.setAnswer("High");
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(safetyHigh));

		int product = ProductAssessment.RISK_ON_HEALTH_SAFETY.getQuestionID();
		AuditData productLow = createAuditData(product);
		productLow.setAnswer("Low");
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(productLow));

		AuditData productMedium = createAuditData(product);
		productMedium.setAnswer("Medium");
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(productMedium));

		AuditData productHigh = createAuditData(product);
		productHigh.setAnswer("High");
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getRiskLevel(productHigh));
	}

	@Test
	public void testNonAssessmentQuestion() {
		AuditData nonAssessmentQuestion = createAuditData(1);
		nonAssessmentQuestion.setAnswer("High");
		Assert.assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(nonAssessmentQuestion));
	}

	@Test
	public void testNullAnswer() {
		AuditData noAnswer = createAuditData(12345);
		Assert.assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(noAnswer));
	}

	@Test
	public void testBadAnswer() {
		AuditData badAnswer = createAuditData(12345);
		badAnswer.setAnswer("Hello");
		Assert.assertEquals(LowMedHigh.None, serviceRiskCalculator.getRiskLevel(badAnswer));
	}

	private List<AuditData> createSafetyAuditDataAllAnswers(String answer) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (SafetyAssessment safetyAssessment : SafetyAssessment.values()) {
			if (!safetyAssessment.isSelfEvaluation()) {
				AuditData auditData = createAuditData(safetyAssessment.getQuestionID());
				auditData.setAnswer(answer);

				auditDatas.add(auditData);
			}
		}

		return auditDatas;
	}

	private List<AuditData> createSafetyAuditDataAnswersMixed(boolean highRisk) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (SafetyAssessment safetyAssessment : SafetyAssessment.values()) {
			if (!safetyAssessment.isSelfEvaluation()) {
				AuditData auditData = createAuditData(safetyAssessment.getQuestionID());
				// Lower risk questions
				auditData.setAnswer(highRisk ? NO : YES);

				// Higher risk questions
				if (safetyAssessment.ordinal() >= SafetyAssessment.PERMIT_TO_WORK.ordinal()) {
					auditData.setAnswer(highRisk ? YES : NO);
				}

				auditDatas.add(auditData);
			}
		}

		return auditDatas;
	}

	private List<AuditData> createProductAuditDataAllAnswers(String answer) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (ProductAssessment productAssessment : ProductAssessment.values()) {
			if (!productAssessment.isSelfEvaluation()) {
				AuditData auditData = createAuditData(productAssessment.getQuestionID());
				auditData.setAnswer(answer);

				auditDatas.add(auditData);
			}
		}

		return auditDatas;
	}

	private List<AuditData> createProductAuditDataAnswersMixedEvensYes(boolean even) {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (ProductAssessment productAssessment : ProductAssessment.values()) {
			if (!productAssessment.isSelfEvaluation()) {
				AuditData auditData = createAuditData(productAssessment.getQuestionID());
				auditData.setAnswer(productAssessment.getQuestionID() % 2 == (even ? 0 : 1) ? YES : NO);

				auditDatas.add(auditData);
			}
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
