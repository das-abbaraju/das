package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.model.i18n.LlewellynTranslatableString;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_option_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditOptionValue extends BaseTable {

	// private TranslatableString name;
	private String name;

	private AuditOptionGroup group;
	private boolean visible = true;
	private int number = 0;
	private String uniqueCode;
	private int score = 0;

	@Transient
	public String getName() {
		return new LlewellynTranslatableString(getI18nKey("name")).toTranslatedString();
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "typeID", nullable = false)
	public AuditOptionGroup getGroup() {
		return group;
	}

	public void setGroup(AuditOptionGroup group) {
		this.group = group;
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
	@Column(nullable = false)
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Transient
	public float getScorePercent() {
		if (group.getMaxScore() == 0) {
			return 0;
		}
		return (((float) score) / group.getMaxScore());
	}

	@Transient
	public String getIdentifier() {
		if (!Strings.isEmpty(uniqueCode)) {
			return uniqueCode;
		}
		return id + "";
	}

	@Override
	@Transient
	public String getI18nKey() {
		if (!Strings.isEmpty(group.getUniqueCode())) {
			if (group.getUniqueCode().equals("Country") || group.getUniqueCode().equals("CountrySubdivision")) {
				return group.getUniqueCode() + "." + getIdentifier();
			}
		}

		return group.getI18nKey() + "." + getIdentifier();
	}

	@Override
	@Transient
	public String getI18nKey(String property) {
		if (property != null && !property.isEmpty() && property.equals("name")) {
			return getI18nKey();
		}

		return super.getI18nKey(property);
	}

	@Override
	@Transient
	public String getAutocompleteResult() {
		return name.toString();
	}
}
