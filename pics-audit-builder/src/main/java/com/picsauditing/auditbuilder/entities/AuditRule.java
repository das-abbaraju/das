package com.picsauditing.auditbuilder.entities;

import com.picsauditing.auditbuilder.service.AccountService;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@SuppressWarnings("serial")
@MappedSuperclass
public class AuditRule extends BaseDecisionTreeRule {

	protected AuditType auditType;
    protected Boolean safetySensitive;
	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;
    protected LowMedHigh tradeSafetyRisk;
	protected OperatorAccount operatorAccount;
	protected ContractorType contractorType;
	protected OperatorTag tag;
	protected Trade trade;
	protected AuditQuestion question;
	protected QuestionComparator questionComparator;
	protected String questionAnswer;
	protected Boolean soleProprietor;
	protected AccountLevel accountLevel;
	protected AuditType dependentAuditType;
	protected AuditStatus dependentAuditStatus;
	private PastAuditYear yearToCheck;

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

    public Boolean getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(Boolean safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    @Enumerated(EnumType.STRING)
	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Enumerated(EnumType.STRING)
	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

    @Enumerated(EnumType.STRING)
    public LowMedHigh getTradeSafetyRisk() {
        return tradeSafetyRisk;
    }

    public void setTradeSafetyRisk(LowMedHigh tradeSafetyRisk) {
        this.tradeSafetyRisk = tradeSafetyRisk;
    }

    @ManyToOne
	@JoinColumn(name = "opID")
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Enumerated(EnumType.STRING)
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
	@JoinColumn(name = "tradeID")
	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
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

	@Enumerated(EnumType.STRING)
	public AccountLevel getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(AccountLevel accountLevel) {
		this.accountLevel = accountLevel;
	}

	public Boolean getSoleProprietor() {
		return soleProprietor;
	}

	public void setSoleProprietor(Boolean soleProprietor) {
		this.soleProprietor = soleProprietor;
	}

	public boolean isMoreSpecific(AuditRule o) {
		if (o == null)
			return true;
		if (this.equals(o))
			return false;

		int thisPriority = 1;
		int otherPriority = 1;
		if (operatorAccount != null)
			thisPriority = AccountService.getRulePriorityLevel(operatorAccount);
		if (o.getOperatorAccount() != null)
			otherPriority = AccountService.getRulePriorityLevel(o.getOperatorAccount());

		if (thisPriority == otherPriority)
			return compareTo(o) > 0;
		return thisPriority > otherPriority;
	}

	@ManyToOne
	@JoinColumn(name = "dependentAuditTypeID")
	public AuditType getDependentAuditType() {
		return dependentAuditType;
	}

	public void setDependentAuditType(AuditType dependentAuditType) {
		this.dependentAuditType = dependentAuditType;
	}

	@Enumerated(EnumType.STRING)
	public AuditStatus getDependentAuditStatus() {
		return dependentAuditStatus;
	}

	public void setDependentAuditStatus(AuditStatus dependentAuditStatus) {
		this.dependentAuditStatus = dependentAuditStatus;
	}

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.auditbuilder.entities.EnumMapperWithEmptyStrings", parameters = {
			@Parameter(name = "enumClass", value = "com.picsauditing.auditbuilder.entities.PastAuditYear"),
			@Parameter(name = "identifierMethod", value = "getDbValue"),
			@Parameter(name = "valueOfMethod", value = "fromDbValue")})
	public PastAuditYear getYearToCheck() {
		return yearToCheck;
	}

	public void setYearToCheck(PastAuditYear yearToCheck) {
		this.yearToCheck = yearToCheck;
	}

	public boolean appliesToASpecificYear() {
		return yearToCheck != PastAuditYear.Any;
	}
}