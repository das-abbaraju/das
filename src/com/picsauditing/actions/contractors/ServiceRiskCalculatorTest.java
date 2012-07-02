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

	// 12341. Can all the work you perform or the services you provide be
	// conducted from an office at all times?
	// Yes=Low, No=Medium
	@Test
	public void testSafety_Offices() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// 12342. Do any of your employees ever use hand, power, pneumatic or
	// hydraulic tools? i.e. drill, circular saw, jackhammer, grinders, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_HandTools() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_HAND_POWER_PNEUMATIC_TOOLS);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// 12343. Are your employees required to wear any personal protective
	// equipment (PPE)? i.e. safety shoes, safety glasses, fire retardant
	// clothing, hard hat, gloves, hearing protection, etc.
	// Yes=Medium, No=Low
	@Test
	public void testSafety_PPE() {
		auditData.getQuestion().setId(ServiceRiskCalculator.SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT);

		auditData.setAnswer(YES);
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getRiskLevel(auditData));

		auditData.setAnswer(NO);
		Assert.assertEquals(LowMedHigh.Low, serviceRiskCalculator.getRiskLevel(auditData));
	}

	// 12344. Are you required to complete or obtain a “permit to work” in order
	// to conduct ANY of your operations? i.e. confined space permit, hot work
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

	// 12345. Do you use any mobile equipment to carry out your operations? i.e.
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

	// 12346. Regardless of the client site you are registering for, does your
	// company at ANYTIME perform any of the following: abatement/remediation
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

	@Test
	public void testHighestRiskLevel() {
		List<AuditData> yesToEverything = createSafetyAuditDataAllAnswersYes();
		List<AuditData> noToEverything = createSafetyAuditDataAllAnswersNo();
		List<AuditData> mixedLowRisk = createSafetyAuditDataAnswersMixedLowRisk();
		List<AuditData> mixedHighRisk = createSafetyAuditDataAnswersMixedHighRisk();

		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getHighestRiskLevel(yesToEverything).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getHighestRiskLevel(noToEverything).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.Med, serviceRiskCalculator.getHighestRiskLevel(mixedLowRisk).get(ServiceRiskCalculator.SAFETY));
		Assert.assertEquals(LowMedHigh.High, serviceRiskCalculator.getHighestRiskLevel(mixedHighRisk).get(ServiceRiskCalculator.SAFETY));
	}

	private List<AuditData> createSafetyAuditDataAllAnswersYes() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (int questionID = ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE; questionID <= ServiceRiskCalculator.SAFETY_PERFORMS_HIGH_RISK; questionID++) {
			AuditData auditData = new AuditData();
			auditData.setQuestion(new AuditQuestion());
			auditData.getQuestion().setId(questionID);
			auditData.setAnswer(YES);

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createSafetyAuditDataAllAnswersNo() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (int questionID = ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE; questionID <= ServiceRiskCalculator.SAFETY_PERFORMS_HIGH_RISK; questionID++) {
			AuditData auditData = new AuditData();
			auditData.setQuestion(new AuditQuestion());
			auditData.getQuestion().setId(questionID);
			auditData.setAnswer(NO);

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createSafetyAuditDataAnswersMixedLowRisk() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (int questionID = ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE; questionID <= ServiceRiskCalculator.SAFETY_PERFORMS_HIGH_RISK; questionID++) {
			AuditData auditData = new AuditData();
			auditData.setQuestion(new AuditQuestion());
			auditData.getQuestion().setId(questionID);
			auditData.setAnswer(YES);

			if (questionID >= ServiceRiskCalculator.SAFETY_PERMIT_TO_WORK) {
				auditData.setAnswer(NO);
			}

			auditDatas.add(auditData);
		}

		return auditDatas;
	}

	private List<AuditData> createSafetyAuditDataAnswersMixedHighRisk() {
		List<AuditData> auditDatas = new ArrayList<AuditData>();

		for (int questionID = ServiceRiskCalculator.SAFETY_CONDUCTED_FROM_OFFICE; questionID <= ServiceRiskCalculator.SAFETY_PERFORMS_HIGH_RISK; questionID++) {
			AuditData auditData = new AuditData();
			auditData.setQuestion(new AuditQuestion());
			auditData.getQuestion().setId(questionID);
			auditData.setAnswer(NO);

			if (questionID >= ServiceRiskCalculator.SAFETY_PERMIT_TO_WORK) {
				auditData.setAnswer(YES);
			}

			auditDatas.add(auditData);
		}

		return auditDatas;
	}
}
