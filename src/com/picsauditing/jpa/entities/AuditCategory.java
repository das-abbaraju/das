package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pqfcategories")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="global")
public class AuditCategory implements java.io.Serializable {
	public static final int WORK_HISTORY = 6;
	public static final int OSHA = 29;
	
	private int id;
	private AuditType auditType;
	private String category;
	private int number;
	private int numRequired;
	private int numQuestions;
	
	List<AuditSubCategory> subCategories;
	
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

}
