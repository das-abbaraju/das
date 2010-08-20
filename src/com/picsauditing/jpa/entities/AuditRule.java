package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public class AuditRule extends BaseDecisionTreeRule {

	protected AuditType auditType;
	protected LowMedHigh risk;
	protected OperatorAccount operatorAccount;
	protected ContractorType contractorType;
	protected OperatorTag tag;
	protected AuditQuestion question;
	protected QuestionComparator questionComparator = QuestionComparator.Equals;
	protected String questionAnswer;

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getRisk() {
		return risk;
	}

	public void setRisk(LowMedHigh risk) {
		this.risk = risk;
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@JoinColumn(name = "accountType")
	@Enumerated(EnumType.ORDINAL)
	public ContractorType getContractorType() {
		return contractorType;
	}

	public void setContractorType(ContractorType contractorType) {
		this.contractorType = contractorType;
	}

	@ManyToOne
	@JoinColumn(name = "tagID")
	public OperatorTag getTag() {
		return tag;
	}

	public void setTag(OperatorTag tag) {
		this.tag = tag;
	}

	@ManyToOne
	@JoinColumn(name = "questionID")
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	@Enumerated(EnumType.STRING)
	public QuestionComparator getQuestionComparator() {
		return questionComparator;
	}

	public void setQuestionComparator(QuestionComparator questionComparator) {
		this.questionComparator = questionComparator;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	@Override
	public void calculatePriority() {
		priority = 0;
		// Order these by least unique to most unique
		if (contractorType != null)
			// Only 2 or 3
			priority += 101;
		if (risk != null)
			// Only 3
			priority += 102;
		if (auditType != null)
			// Hundred
			priority += 105;
		if (operatorAccount != null) {
			if (operatorAccount.isCorporate())
				// Dozens to a hundred
				priority += 104;
			else
				// Hundreds-thousand
				priority += 110;
		}
		
		if (question != null && questionComparator != null)
			// Potentially thousands but probably only hundreds
			priority += 125;
		if (tag != null)
			// Several per operator, potentially thousands
			priority += 130;
	}

}
