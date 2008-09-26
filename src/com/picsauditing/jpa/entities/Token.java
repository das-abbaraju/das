package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "token")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="global")
public class Token implements java.io.Serializable {
	protected int id;
	protected String tokenName;
	protected String velocityName;	
	protected String type;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tokenID", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "tokenName", nullable = false, length = 50)
	public String getTokenName() {
		return this.tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	
	@Column(name = "velocityName", nullable = true, length = 50)
	public String getVelocityName() {
		return this.velocityName;
	}

	public void setVelocityName(String velocityName) {
		this.velocityName = velocityName;
	}	

	@Column(name = "type", unique = true, nullable = false, length = 50)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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
