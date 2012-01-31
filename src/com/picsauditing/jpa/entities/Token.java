package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Token extends BaseTranslatable {
	private int id;
	private String name;
	private ListType listType;
	private TranslatableString velocityCode;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tokenID", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "tokenName", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public TranslatableString getVelocityCode() {
		return velocityCode;
	}

	public void setVelocityCode(TranslatableString velocityCode) {
		this.velocityCode = velocityCode;
	}

	@Enumerated(EnumType.STRING)
	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
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

		try {
			// Try to cast this to an account
			final Token other = (Token) obj;
			if (id == other.getId().intValue())
				return true;
			return false;
		} catch (Exception e) {
			// something went wrong so these must not be equal
			return false;
		}
	}

	@Override
	@Transient
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + getId();
	}

	@Override
	@Transient
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
