package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfcategories")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="global")
public class AuditCategory implements java.io.Serializable, Comparable<AuditCategory> {
	public static final int WORK_HISTORY = 6;
	public static final int OSHA_AUDIT = 151;
	public static final int TRAINING = 25;
	public static final int SAFETY_POLICIES = 7;
	public static final int SERVICES_PERFORMED = 28;
	
	private int id;
	private AuditType auditType;
	private String category;
	private int number;
	private int numRequired;
	private int numQuestions;
	
	List<AuditSubCategory> subCategories;

	/**
	 * This is a transient field that allows us to figure out which categories, subcategories and questions should be displayed
	 */
	private Date validDate = null;


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "catID", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditTypeID", nullable = false)
	public AuditType getAuditType() {
		return this.auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Column(name = "category", nullable = false)
	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(nullable = false)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Column(nullable = false)
	public int getNumRequired() {
		return this.numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	@Column(nullable = false)
	public int getNumQuestions() {
		return this.numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}
	
	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<AuditSubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<AuditSubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@Transient
	public List<AuditSubCategory> getValidSubCategories() {
		List<AuditSubCategory> list = new ArrayList<AuditSubCategory>();
		for(AuditSubCategory subCategory : getSubCategories())
			if (subCategory.getValidQuestions().size() > 0)
				list.add(subCategory);
		return list;
	}
	
	@Transient
	public Date getValidDate() {
		if (validDate == null)
			return new Date();
		return validDate;
	}
	
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
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
		final AuditCategory other = (AuditCategory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	@Override
	public int compareTo(AuditCategory other) {
		if( other == null ) {
			return 1;
		}
		
		int cmp = getAuditType().compareTo(other.getAuditType());
		
		if( cmp != 0 ) 
			return cmp;

		return new Integer( getNumber() ).compareTo(new Integer(other.getNumber()));
	}
	
	
}
