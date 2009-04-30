package com.picsauditing.jpa.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.PICS.DateBean;

@Entity
@Table(name = "flagcriteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagQuestionCriteria extends BaseTable {
	protected OperatorAccount operatorAccount;
	protected AuditQuestion auditQuestion;
	protected FlagColor flagColor;
	protected YesNo checked = YesNo.Yes;

	protected String comparison;
	protected String value;
	protected boolean validationRequired;
	protected MultiYearScope multiYearScope;

	@ManyToOne(optional = false)
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "flagStatus", nullable = false)
	@Enumerated(EnumType.STRING)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "questionID", nullable = false)
	public AuditQuestion getAuditQuestion() {
		return auditQuestion;
	}

	public void setAuditQuestion(AuditQuestion auditQuestion) {
		this.auditQuestion = auditQuestion;
	}

	@Column(name = "isChecked", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getChecked() {
		return checked;
	}

	public void setChecked(YesNo checked) {
		this.checked = checked;
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

	public boolean isValidationRequired() {
		return validationRequired;
	}

	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.MultiYearScope") })
	@Column(name = "multiYearScope", nullable = true)
	public MultiYearScope getMultiYearScope() {
		return multiYearScope;
	}

	public void setMultiYearScope(MultiYearScope multiYearScope) {
		this.multiYearScope = multiYearScope;
	}

	@Transient
	public boolean isFlagged(String answer) {
		String questionType = auditQuestion.getQuestionType();
		if ("Check Box".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("Date".equals(questionType)) {
			try {
				Date answerDate = DateBean.parseDate(answer);
				if (">".equals(comparison))
					return (answerDate.after(new Date()));
				if ("<".equals(comparison))
					return (answerDate.before(new Date()));
			} catch (Exception e) {
				System.out.println("failed to parse date: " + answer);
				return true;
			}
		}
		if ("".equals(answer))
			return true;
		if ("Yes/No/NA".equals(questionType) || "Yes/No".equals(questionType) || "Manual".equals(questionType))
			if ("=".equals(comparison))
				return value.equals(answer);
			else
				return !value.equals(answer);
		if ("Decimal Number".equals(questionType) || "Money".equals(questionType)) {
			float tempRate = 0;
			float tempCutoff = 0;
			try {
				tempRate = Float.parseFloat(answer.replace(",", ""));
				tempCutoff = Float.parseFloat(value.replace(",", ""));
			} catch (Exception e) {
				return true;
			}
			if (">".equals(comparison))
				return (tempRate > tempCutoff);
			if ("<".equals(comparison))
				return (tempRate < tempCutoff);
			return (tempRate == tempCutoff);
		}
		if ("Additional Insured".equals(questionType)) {
			if (operatorAccount != null) {
				List<AccountName> names = getOperatorAccount().getNames();

				if (names != null && names.size() > 0) {
					for (AccountName name : names) {
						if (name.getName().equalsIgnoreCase(answer.trim())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Print this out: value <
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<i>value</i> ").append(comparison).append(" ").append(this.value);
		return buf.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

}
