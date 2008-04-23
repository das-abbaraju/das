package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "flagcriteria")
public class FlagQuestionCriteria {
	protected int id;
	protected OperatorAccount operatorAccount;
	protected AuditQuestion auditQuestion;
	protected FlagColor flagColor;
	protected YesNo checked;

	protected String questionType;
	protected String comparison;
	protected String value;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "criteriaID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="opID", nullable=false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}
	
	public AuditQuestion getAuditQuestion() {
		return auditQuestion;
	}

	public void setAuditQuestion(AuditQuestion auditQuestion) {
		this.auditQuestion = auditQuestion;
	}

	public YesNo getChecked() {
		return checked;
	}

	public void setChecked(YesNo checked) {
		this.checked = checked;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public boolean isFlagged(String answer) {
		if ("Check Box".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("-".equals(answer))
			return true;
		if ("Yes/No/NA".equals(questionType) || "Yes/No".equals(questionType) || "Manual".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("Decimal Number".equals(questionType)){
			float tempRate = 0;
			float tempCutoff = 0;
			try {
				tempRate = Float.parseFloat(answer);
				tempCutoff = Float.parseFloat(value);
			} catch (Exception e) {
				return true;
			}
			if (">".equals(comparison))
					return (tempRate > tempCutoff);
			if ("<".equals(comparison))
					return (tempRate < tempCutoff);
			return (tempRate == tempCutoff);
		}
		return false;
	}
	
}
