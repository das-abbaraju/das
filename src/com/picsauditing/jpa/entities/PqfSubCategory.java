package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pqfsubcategories")
public class PqfSubCategory implements java.io.Serializable {

	private int id;
	private String subCategory;
	private PqfCategory category;
	private int number;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "subCatID", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public PqfCategory getCategory() {
		return category;
	}

	public void setCategory(PqfCategory category) {
		this.category = category;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
