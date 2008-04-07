package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pqfsubcategories")
public class AuditSubCategory implements java.io.Serializable {

	private int id;
	private String subCategory;
	private AuditCategory category;
	private int number;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "subCatID", nullable = false, insertable = false, updatable = false, unique = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
/*
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public PqfCategory getCategory() {
		return category;
	}

	public void setCategory(PqfCategory category) {
		this.category = category;
	}
*/
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
