package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfoptions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestionOption extends BaseTable implements
		java.io.Serializable {
	private AuditQuestion auditQuestion;
	private String optionName;
	private YesNo visible;
	private int number;

	public AuditQuestionOption() {

	}

	public AuditQuestionOption(AuditQuestionOption a, AuditQuestion aq) {
		a.auditQuestion = aq;
		this.number = a.getNumber();
		this.optionName = a.getOptionName();
		this.visible = a.getVisible();
	}

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false)
	public AuditQuestion getAuditQuestion() {
		return auditQuestion;
	}

	public void setAuditQuestion(AuditQuestion auditQuestion) {
		this.auditQuestion = auditQuestion;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public YesNo getVisible() {
		return visible;
	}

	@Transient
	public boolean isVisibleB() {
		return YesNo.Yes.equals(visible);
	}

	public void setVisible(YesNo visible) {
		this.visible = visible;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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
		final AuditQuestionOption other = (AuditQuestionOption) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
