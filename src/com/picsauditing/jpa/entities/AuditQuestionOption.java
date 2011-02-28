package com.picsauditing.jpa.entities;

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

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfoptions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestionOption extends BaseTable {

	private AuditQuestion auditQuestion;
	private String optionName;
	private YesNo visible = YesNo.Yes;
	private int number;
	private int score;

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
	
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
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

	/**
	 * @return The score of this question to be used when scoring an audit
	 */
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
