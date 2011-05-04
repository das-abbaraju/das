package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question_option")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestionOption extends BaseTable {
	private TranslatableString name;
	private AuditOptionType type;
	private boolean visible = true;
	private int number = 0;
	private String uniqueCode;
	private int score = 0;

	@Transient
	public TranslatableString getName() {
		return name;
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "typeID", nullable = false)
	public AuditOptionType getType() {
		return type;
	}

	public void setType(AuditOptionType type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	/**
	 * @return The score of this question to be used when scoring an audit
	 */
	@JoinColumn(nullable = false)
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	@Transient
	public String getI18nKey() {
		if (uniqueCode != null && !uniqueCode.isEmpty())
			return type.getI18nKey() + "." + uniqueCode.replaceAll(" ", "");

		return type.getI18nKey() + "." + id;
	}

	@Override
	@Transient
	public String getI18nKey(String property) {
		if (property != null && !property.isEmpty() && property.equals("name"))
			return getI18nKey();
		
		return super.getI18nKey(property);
	}
	
	@Override
	public String toString() {
		return "(" + id + ") " + getI18nKey();
	}
}
