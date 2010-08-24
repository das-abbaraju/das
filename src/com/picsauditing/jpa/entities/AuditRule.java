package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.jboss.util.Strings;

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
	protected QuestionComparator questionComparator;
	protected String questionAnswer;
	protected Boolean acceptsBids;

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Transient
	public String getAuditTypeLabel() {
		if (auditType == null)
			return "*";
		return auditType.getAuditName();
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getRisk() {
		return risk;
	}

	public void setRisk(LowMedHigh risk) {
		this.risk = risk;
	}

	@Transient
	public String getRiskLabel() {
		if (risk == null)
			return "*";
		return risk.toString();
	}

	@ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Transient
	public String getOperatorAccountLabel() {
		if (operatorAccount == null)
			return "*";
		return operatorAccount.getName();
	}

	@JoinColumn(name = "accountType")
	@Enumerated(EnumType.ORDINAL)
	public ContractorType getContractorType() {
		return contractorType;
	}

	public void setContractorType(ContractorType contractorType) {
		this.contractorType = contractorType;
	}

	@Transient
	public String getContractorTypeLabel() {
		if (contractorType == null)
			return "*";
		return contractorType.toString();
	}

	@ManyToOne
	@JoinColumn(name = "tagID")
	public OperatorTag getTag() {
		return tag;
	}

	public void setTag(OperatorTag tag) {
		this.tag = tag;
	}

	@Transient
	public String getTagLabel() {
		if (tag == null)
			return "*";
		return tag.getTag();
	}

	@ManyToOne
	@JoinColumn(name = "questionID")
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	@Transient
	public String getQuestionLabel() {
		if (question == null)
			return "*";
		return question.getName();
	}

	@Enumerated(EnumType.STRING)
	public QuestionComparator getQuestionComparator() {
		return questionComparator;
	}

	public void setQuestionComparator(QuestionComparator questionComparator) {
		this.questionComparator = questionComparator;
	}

	@Transient
	public String getQuestionComparatorLabel() {
		if (questionComparator == null)
			return "*";
		return questionComparator.toString();
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	@Transient
	public String getQuestionAnswerLabel() {
		if (questionAnswer == null)
			return "*";
		return questionAnswer;
	}

	public Boolean getAcceptsBids() {
		return acceptsBids;
	}

	public void setAcceptsBids(Boolean acceptsBids) {
		this.acceptsBids = acceptsBids;
	}

	@Transient
	public String getAcceptsBidsLabel() {
		if (acceptsBids == null)
			return "*";
		return acceptsBids ? "Yes" : "No";
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

	@Transient
	public boolean isMatchingAnswer(AuditData data) {
		if (data == null)
			return questionComparator.equals(QuestionComparator.Empty);

		String answer = data.getAnswer();
		switch (questionComparator) {
		case Empty:
			return Strings.isEmpty(answer);
		case NotEmpty:
			return !Strings.isEmpty(answer);
		case NotEquals:
			return !questionAnswer.equals(answer);
		case Verified:
			return data.isVerified();
		default:
			return questionAnswer.equals(answer);
		}
	}

	public void merge(AuditRule source) {
		super.merge(source);
		if (auditType == null)
			auditType = source.auditType;
		if (risk == null)
			risk = source.risk;
		if (operatorAccount == null)
			operatorAccount = source.operatorAccount;
		if (contractorType == null)
			contractorType = source.contractorType;
		if (tag == null)
			tag = source.tag;
		if (question == null)
			question = source.question;
		if (questionComparator == null)
			questionComparator = source.questionComparator;
		if (questionAnswer == null)
			questionAnswer = source.questionAnswer;
	}
}
