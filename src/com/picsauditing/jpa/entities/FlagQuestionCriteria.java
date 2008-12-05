package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.PICS.DateBean;

@SuppressWarnings("serial")
@Entity
@Table(name = "flagcriteria")
// We can't use this yet because hibernate doesn't save this 
// data yet, see op_editFlagCriteria.jsp
//@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class FlagQuestionCriteria {
	protected int id;
	protected OperatorAccount operatorAccount;
	protected AuditQuestion auditQuestion;
	protected FlagColor flagColor;
	protected YesNo checked;

	protected String comparison;
	protected String value;
	protected boolean validationRequired;
	protected MultiYearScope multiYearScope; 

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

	@Column(name = "flagStatus", nullable = false)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}
	
	@ManyToOne(optional=false)
	@JoinColumn(name="questionID", nullable=false)
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

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FlagQuestionCriteria other = (FlagQuestionCriteria) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
