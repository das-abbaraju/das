package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.jboss.util.Strings;

import com.picsauditing.actions.auditType.AuditRuleColumn;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public class AuditRule extends BaseDecisionTreeRule implements AuditRuleTable {

	protected AuditType auditType;
	protected LowMedHigh risk;
	protected OperatorAccount operatorAccount;
	protected ContractorType contractorType;
	protected OperatorTag tag;
	protected AuditQuestion question;
	protected QuestionComparator questionComparator;
	protected String questionAnswer;
	protected Boolean acceptsBids = false; // Default to bid-only "No" (Needed to the increase the priority)

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

	/**
	 * Does this rule apply to the given operator?
	 * 
	 * @param operator
	 * @return
	 */
	@Transient
	public boolean isApplies(OperatorAccount operator) {
		if (this.operatorAccount == null)
			return true;
		if (this.operatorAccount.equals(operator))
			return true;
		for (Facility facility : operator.getCorporateFacilities()) {
			if (this.operatorAccount.equals(facility.getCorporate()))
				return true;
		}
		return false;
	}

	@Enumerated(EnumType.STRING)
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
		level = levelAdjustment; // usually 0
		priority = 100 * levelAdjustment; // usually 0
		
		if (include)
			priority += 1;

		// Order these by least unique to most unique
		if (contractorType != null) {
			// Only 2 or 3
			priority += 101;
			level++;
		}
		if (risk != null) {
			// Only 3
			priority += 102;
			level++;
		}
		if (acceptsBids != null) {
			priority += 103;
			level++;
		}

		if (auditType != null) {
			// Hundred
			priority += 105;
			level++;
		}

		if (question != null && questionComparator != null) {
			// Potentially thousands but probably only hundreds
			priority += 125;
			level++;
		}

		if (tag != null) {
			// Several per operator, potentially thousands
			priority += 130;
			level++;
		}

		if (operatorAccount != null) {
			if (operatorAccount.isPrimaryCorporate())
				priority += 135;
			else if (operatorAccount.isCorporate())
				// Dozens to a hundred
				priority += 137;
			else
				// Hundreds-thousand
				priority += 139;
			level++;
		}
	}

	@Transient
	public boolean isMatchingAnswer(AuditData data) {
		if (data == null) {
			// TODO why would questionComparator be null? Do we return false?
			if (questionComparator == null)
				return false;

			return questionComparator.equals(QuestionComparator.Empty);
		}

		if (questionComparator == null)
			return false;
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
		case StartsWith:
			return answer.startsWith(questionAnswer);
		default:
			return questionAnswer.equals(answer);
		}
	}

	public void merge(AuditRule source) {
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

	public void update(AuditRule source) {
		auditType = source.auditType;
		risk = source.risk;
		operatorAccount = source.operatorAccount;
		contractorType = source.contractorType;
		tag = source.tag;
		question = source.question;
		questionComparator = source.questionComparator;
		questionAnswer = source.questionAnswer;
		include = source.include;
		acceptsBids = source.acceptsBids;
		levelAdjustment = source.levelAdjustment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(include ? "Include" : "Exclude").append(" when");

		if (auditType != null)
			sb.append(" and Audit Type = ").append(auditType);
		if (risk != null)
			sb.append(" and Contractor Risk = ").append(risk);
		if (operatorAccount != null)
			sb.append(" and for Operator ").append(operatorAccount);
		if (contractorType != null)
			sb.append(" and Contractor is ").append(contractorType);
		if (tag != null)
			sb.append(" and has tag ").append(tag);
		if (question != null)
			sb.append(" and ").append(question.getColumnHeaderOrQuestion()).append(" ").append(questionComparator)
					.append(questionAnswer);
		if (acceptsBids != null)
			sb.append(" and Contactor is ").append(acceptsBids ? "bid-only" : "NOT bid-only");

		return sb.toString();
	}
	
	@Override
	@Transient
	public Map<AuditRuleColumn, List<String>> getMapping() {
		Map<AuditRuleColumn, List<String>> map = new HashMap<AuditRuleColumn, List<String>>();
		
		for (AuditRuleColumn c : AuditRuleColumn.values()) {
			map.put(c, new ArrayList<String>());
		}
		
		map.get(AuditRuleColumn.Include).add(isInclude() ? "Yes" : "No");
		map.get(AuditRuleColumn.Priority).add(getPriority() + "");
		
		if (getAuditType() != null)
			map.get(AuditRuleColumn.AuditType).add(getAuditTypeLabel());
		if (getContractorType() != null)
			map.get(AuditRuleColumn.ContractorType).add(getContractorTypeLabel());
		if (getOperatorAccount() != null)
			map.get(AuditRuleColumn.Operator).add(getOperatorAccountLabel());
		if (getRisk() != null)
			map.get(AuditRuleColumn.Risk).add(getRiskLabel());
		if (getTag() != null)
			map.get(AuditRuleColumn.Tag).add(getTagLabel());
		if (getAcceptsBids() != null)
			map.get(AuditRuleColumn.BidOnly).add(getAcceptsBidsLabel());
		if (getQuestion() != null) {
			map.get(AuditRuleColumn.Question).add(getQuestionLabel());
			map.get(AuditRuleColumn.Question).add(getQuestionComparatorLabel());
			map.get(AuditRuleColumn.Question).add(getQuestionAnswerLabel());
		}
		if (getCreatedBy() != null) {
			map.get(AuditRuleColumn.CreatedBy).add(getCreatedBy().getName());
			map.get(AuditRuleColumn.CreatedBy).add(getCreationDate().toString());
		}
		if (getUpdatedBy() != null) {
			map.get(AuditRuleColumn.UpdatedBy).add(getUpdatedBy().getName());
			map.get(AuditRuleColumn.UpdatedBy).add(getUpdateDate().toString());
		}
		
		return map;
	}
}