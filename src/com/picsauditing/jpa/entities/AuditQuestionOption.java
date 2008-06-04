package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pqfoptions")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="global")
public class AuditQuestionOption implements java.io.Serializable {
	private int id;
	private AuditQuestion auditQuestion;
	private String optionName;
	private YesNo visible;
	private int number;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "optionID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setVisible(YesNo visible) {
		this.visible = visible;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
