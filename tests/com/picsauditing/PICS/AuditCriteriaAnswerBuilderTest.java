package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;

public class AuditCriteriaAnswerBuilderTest extends TestCase {

	public void testPqfSingle() {
		ContractorAudit audit = newAudit();

		AuditQuestion question = newQuestion(audit.getAuditType());
		question.setQuestionType("Decimal Number");

		List<FlagQuestionCriteria> criteriaList = new ArrayList<FlagQuestionCriteria>();
		criteriaList.add(newCriteria(FlagColor.Red, question, null, ">", "1"));

		AuditData answer = newAnswer(audit, question, "1.5");

		List<AuditData> answerList = new ArrayList<AuditData>();
		answerList.add(answer);

		List<AuditCriteriaAnswer> acaList = runner(criteriaList, answerList);
		assertEquals(1, acaList.size());
		for (AuditCriteriaAnswer aca : acaList) {
			assertEquals(FlagColor.Red, aca.getResultColor(true));
		}
	}

	public void testAnnualAddendum() {
		ContractorAudit audit = newAudit();
		audit.setAuditFor("2008");
		audit.getAuditType().setHasMultiple(true);

		AuditQuestion question = newQuestion(audit.getAuditType());
		question.setQuestionType("Decimal Number");

		List<FlagQuestionCriteria> criteriaList = new ArrayList<FlagQuestionCriteria>();
		criteriaList.add(newCriteria(FlagColor.Red, question, MultiYearScope.LastYearOnly, ">", "1"));

		List<AuditData> answerList = new ArrayList<AuditData>();
		answerList.add(newAnswer(audit, question, "1.5"));

		List<AuditCriteriaAnswer> acaList = runner(criteriaList, answerList);
		assertEquals(1, acaList.size());
		for (AuditCriteriaAnswer aca : acaList) {
			assertEquals(FlagColor.Red, aca.getResultColor(true));
		}
	}

	// ////////////// HELPER METHODS /////////////////////

	private List<AuditCriteriaAnswer> runner(List<FlagQuestionCriteria> criteriaList, List<AuditData> answerList) {
		AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(AuditDataDAO
				.buildAnswerMapByAudits(answerList), criteriaList);

		return acaBuilder.getAuditCriteriaAnswers();
	}

	private ContractorAudit newAudit() {
		ContractorAudit audit = new ContractorAudit();
		audit.setAuditType(new AuditType());
		return audit;
	}

	private AuditData newAnswer(ContractorAudit audit, AuditQuestion question, String value) {
		AuditData answer = new AuditData();
		answer.setAudit(audit);
		answer.setQuestion(question);
		answer.setAnswer(value);
		return answer;
	}

	private FlagQuestionCriteria newCriteria(FlagColor flagColor, AuditQuestion question, MultiYearScope scope,
			String comparison, String value) {
		FlagQuestionCriteria criteria = new FlagQuestionCriteria();
		criteria.setFlagColor(flagColor);
		criteria.setAuditQuestion(question);
		criteria.setComparison(comparison);
		criteria.setValue(value);
		return criteria;
	}

	private AuditQuestion newQuestion(AuditType auditType) {
		AuditQuestion question = new AuditQuestion();
		question.setSubCategory(new AuditSubCategory());
		question.getSubCategory().setCategory(new AuditCategory());
		question.getSubCategory().getCategory().setAuditType(auditType);
		question.setDefaultQuestion("jUnit Question");
		return question;
	}

}
