package com.picsauditing.jpa.entities;

import java.util.Date;

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
import com.picsauditing.util.Strings;

@Entity
@Table(name = "flagcriteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagQuestionCriteria extends BaseTable {
	protected OperatorAccount operatorAccount;
	protected AuditQuestion auditQuestion;
	protected FlagColor flagColor;
	protected String comparison;
	protected String value;
	protected boolean validationRequired;
	protected MultiYearScope multiYearScope;
	
	protected int amRatings;
	protected int amClass;
	
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

	/**
	 * Just an easy helper method to know if this criteria is for a policy or
	 * audit
	 * 
	 * @return 
	 *         auditQuestion.getSubCategory().getCategory().getAuditType().getClassType
	 *         ()
	 */
	@Transient
	public AuditTypeClass getClassType() {
		return auditQuestion.getSubCategory().getCategory().getAuditType().getClassType();
	}

	@Transient
	public int getAmRatings() {
		return amRatings;
	}

	public void setAmRatings(int amRatings) {
		this.amRatings = amRatings;
	}

	@Transient
	public int getAmClass() {
		return amClass;
	}

	public void setAmClass(int amClass) {
		this.amClass = amClass;
	}
	
	@Transient
	public int getAMBestRatings() {
		if(!Strings.isEmpty(value)) {
			return Integer.parseInt(value.substring(0, value.indexOf('|')));
		}
		return 0;
	}

	@Transient
	public int getAMBestClass() {
		if(!Strings.isEmpty(value)) {
			return Integer.parseInt(value.substring(value.indexOf('|')+ 1,value.length()));
		}
		return 0;
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
				Date valueDate;
				if ("Today".equalsIgnoreCase(value))
					valueDate = new Date();
				else
					valueDate = DateBean.parseDate(value);

				if (">".equals(comparison))
					return (answerDate.after(valueDate));
				if ("<".equals(comparison))
					return (answerDate.before(valueDate));
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
		if("AMBest".equals(questionType)) {
			boolean flag1 = false;
			boolean flag2 = false;
			int ratings = Integer.parseInt(answer.substring(0, answer.indexOf('|')));
			int bestClass = Integer.parseInt(answer.substring(value.indexOf('|')+ 1,answer.length()));
			if(getAMBestRatings() > 0)
				flag1 = ratings > getAMBestRatings();
			if(getAMBestClass() > 0)
				flag2 = bestClass < getAMBestClass();
			if(flag1 || flag2)
				return true;
						
			return false;
		}
		return false;
	}
	
	@Transient
	public boolean isChecked() {
		if(!Strings.isEmpty(comparison) 
				&& !Strings.isEmpty(value))
			return true;
		return false;
	}

	/**
	 * Print this out: value <
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if ("Check Box".equals(auditQuestion.getQuestionType())) {
			if ("X".equals(value))
				buf.append("<i>Checked</i>");
			else
				buf.append("<i>Not Checked</i>");
		} else if ("Date".equals(auditQuestion.getQuestionType())) {
			if ("<".equals(comparison))
				buf.append("<i>before</i> ");
			else
				buf.append("<i>after</i> ");
			buf.append(this.value);
		} else if ("AMBest".equals(auditQuestion.getQuestionType())) {
			buf.append("<i>Less than</i>");
			if(getAMBestRatings() > 0)
				buf.append(" Ratings:" + AmBest.ratingMap.get(getAMBestRatings()));
			if(getAMBestClass() > 0)
				buf.append(" Class:" + AmBest.financialMap.get(getAMBestClass()));
		} else {
			if (multiYearScope != null)
				buf.append(multiYearScope + " ");
			buf.append("<i>value</i> ").append(comparison).append(" ").append(this.value);
		}
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
