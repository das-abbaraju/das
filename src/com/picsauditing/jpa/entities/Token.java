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

@SuppressWarnings("serial")
@Entity
@Table(name = "token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Token {
	protected int id;
	protected String name;
	protected ListType listType;
	protected String velocityCode;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tokenID", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getVelocityCode() {
		return velocityCode;
	}

	public void setVelocityCode(String velocityCode) {
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
}
