package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@MappedSuperclass
public class AuditRule extends BaseDecisionTreeRule {

	protected AuditType auditType;
	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;
	protected OperatorAccount operatorAccount;
	protected ContractorType contractorType;
	protected OperatorTag tag;
	protected Trade trade;
	protected AuditQuestion question;
	protected QuestionComparator questionComparator;
	protected String questionAnswer;
	protected Boolean soleProprietor;
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
		return auditType.getName().toString();
	}

	@Enumerated(EnumType.STRING)
	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Transient
	public String getSafetyRiskLabel() {
		if (safetyRisk == null)
			return "*";
		return safetyRisk.toString();
	}

	@Enumerated(EnumType.STRING)
	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	@Transient
	public String getProductRiskLabel() {
		if (productRisk == null)
			return "*";
		return productRisk.toString();
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
	@JoinColumn(name = "tradeID")
	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	@Transient
	public String getTradeLabel() {
		if (trade == null)
			return "*";
		return trade.getName().toString();
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
		return question.getName().toString();
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

	/**
	 * aka List Only or Bid Only
	 * @return
	 */
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
		return acceptsBids ? "List" : "Full";
	}

	public Boolean getSoleProprietor() {
		return soleProprietor;
	}

	public void setSoleProprietor(Boolean soleProprietor) {
		this.soleProprietor = soleProprietor;
	}

	@Transient
	public String getSoleProprietorLabel() {
		if (soleProprietor == null)
			return "*";
		return soleProprietor ? "Sole" : "Full";
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
		if (safetyRisk != null) {
			// Only 3
			priority += 102;
			level++;
		}
		if (productRisk != null) {
			// Only 3
			priority += 102;
			level++;
		}
		if (acceptsBids != null) {
			priority += 103;
			level++;
			if (acceptsBids) {
				// Always consider list Only rules to be more specific
				priority += 400;
				level = level + 4;
			}
		}
		if (soleProprietor != null) {
			priority += 107;
			level++;
			if (soleProprietor) {
				// Always consider sole proprietor rules to be more specific
				priority += 400;
				level = level + 4;
			}
		}
		if (auditType != null) {
			// Hundred
			priority += 105;
			level++;
		}
		if (trade != null) {
			priority += 121;
			priority += trade.getIndexLevel();
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
		}

		if (questionComparator == QuestionComparator.LessThan || questionComparator == QuestionComparator.LessThanEqual
				|| questionComparator == QuestionComparator.GreaterThan
				|| questionComparator == QuestionComparator.GreaterThanEqual) {
			if ("Decimal Number".equals(question.getQuestionType()) || "Money".equals(question.getQuestionType())
					|| "Number".equals(question.getQuestionType())) {
				try {
					BigDecimal parsedAnswer = new BigDecimal(answer.replace(",", ""));
					BigDecimal parsedQuestionAnswer = new BigDecimal(questionAnswer.replace(",", ""));
					if (questionComparator == QuestionComparator.LessThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) < 0;
					else if (questionComparator == QuestionComparator.LessThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) <= 0;
					else if (questionComparator == QuestionComparator.GreaterThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) > 0;
					else if (questionComparator == QuestionComparator.GreaterThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) >= 0;
				} catch (NumberFormatException nfe) {
					return false;
				}
			} else
				return false;
		}

		return questionAnswer.equals(answer);
	}

	// TODO check to see if we still use this??
	public void merge(AuditRule source) {
		if (auditType == null)
			auditType = source.auditType;
		if (safetyRisk == null)
			safetyRisk = source.safetyRisk;
		if (productRisk == null)
			productRisk = source.productRisk;
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
		if (trade == null)
			trade = source.trade;
		if (soleProprietor == null)
			soleProprietor = source.soleProprietor;
	}

	// TODO check to see if we still use this??
	public void update(AuditRule source) {
		auditType = source.auditType;
		safetyRisk = source.safetyRisk;
		productRisk = source.productRisk;
		operatorAccount = source.operatorAccount;
		contractorType = source.contractorType;
		tag = source.tag;
		question = source.question;
		questionComparator = source.questionComparator;
		questionAnswer = source.questionAnswer;
		include = source.include;
		acceptsBids = source.acceptsBids;
		levelAdjustment = source.levelAdjustment;
		trade = source.trade;
		soleProprietor = source.soleProprietor;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<String> identifiers = new ArrayList<String>();

		sb.append(include ? "Include" : "Exclude");

		if (safetyRisk != null)
			identifiers.add("Contractor is [" + safetyRisk + " in safety risk]");
		if (productRisk != null)
			identifiers.add("Contractor is [" + productRisk + " in product risk]");
		if (operatorAccount != null)
			identifiers.add("Operator is [" + operatorAccount.getName() + "]");
		if (contractorType != null)
			identifiers.add("Contractor is " + contractorType);
		if (tag != null)
			identifiers.add("has tag [" + tag.getTag() + "]");
		if (question != null)
			identifiers.add(question.getColumnHeaderOrQuestion() + " " + questionComparator + " " + questionAnswer);
		if (acceptsBids != null)
			identifiers.add("Contactor " + (acceptsBids ? "can [bid-only]" : "has [full account]"));
		if (trade != null)
			identifiers.add("Trade is [" + trade.getName() + "]");
		if (auditType != null)
			identifiers.add("Audit Type is [" + auditType.getName().toString() + "]");
		if (soleProprietor != null)
			identifiers.add("Sole Proprietor is [" + soleProprietor.toString() + "]");

		if (!identifiers.isEmpty()) {
			sb.append(" when ").append(identifiers.get(0));
			for (int i = 1; i < identifiers.size(); i++) {
				sb.append(" and ").append(identifiers.get(i));
			}
		}

		return sb.toString();
	}
}