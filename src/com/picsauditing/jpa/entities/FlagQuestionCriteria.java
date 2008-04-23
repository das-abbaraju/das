package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "flagcriteria")
public class FlagQuestionCriteria extends FlagCriteria {
	protected AuditQuestion auditQuestion;
	protected YesNo checked;

	protected String questionType;
	protected String comparison;
	protected String value;

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
