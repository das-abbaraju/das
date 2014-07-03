package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_option_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditOptionValue extends BaseTable {
//
//	private String name;
	private AuditOptionGroup group;
//	private boolean visible = true;
//	private int number = 0;
	private String uniqueCode;
	private int score = 0;
//
//	@Transient
//	public String getName() {
//		if (name != null) {
//			return name;
//		}
//
//		return new TranslatableString(getI18nKey("name")).toTranslatedString();
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
	@ManyToOne
	@JoinColumn(name = "typeID", nullable = false)
	public AuditOptionGroup getGroup() {
		return group;
	}

	public void setGroup(AuditOptionGroup group) {
		this.group = group;
	}

//	public boolean isVisible() {
//		return visible;
//	}
//
//	public void setVisible(boolean visible) {
//		this.visible = visible;
//	}
//
//	@Column(nullable = false)
//	public int getNumber() {
//		return number;
//	}
//
//	public void setNumber(int number) {
//		this.number = number;
//	}
//
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(nullable = false)
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

//	@Override
//	@Transient
//	public String getI18nKey() {
//		if (!Strings.isEmpty(group.getUniqueCode())) {
//			if (group.getUniqueCode().equals("Country") || group.getUniqueCode().equals("CountrySubdivision")) {
//				return group.getUniqueCode() + "." + getIdentifier();
//			}
//		}
//
//		return group.getI18nKey() + "." + getIdentifier();
//	}
//
//	@Override
//	@Transient
//	public String getI18nKey(String property) {
//		if (property != null && !property.isEmpty() && property.equals("name")) {
//			return getI18nKey();
//		}
//
//		return super.getI18nKey(property);
//	}
//
//	@Override
//	@Transient
//	public String getAutocompleteResult() {
//		return getName();
//	}
}
