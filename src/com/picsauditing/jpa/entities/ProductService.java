package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_product_service")
public class ProductService extends BaseTable {
	private String classificationType = "Master";
	private ProductService parent;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private ProductService bestMatch;
	private String classificationCode;
	private String description;

	public String getClassificationType() {
		return classificationType;
	}

	public void setClassificationType(String classificationType) {
		this.classificationType = classificationType;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public ProductService getParent() {
		return parent;
	}

	public void setParent(ProductService parent) {
		this.parent = parent;
	}

	public int getIndexLevel() {
		return indexLevel;
	}

	public void setIndexLevel(int indexLevel) {
		this.indexLevel = indexLevel;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}

	@ManyToOne
	@JoinColumn(name = "bestMatchID")
	public ProductService getBestMatch() {
		return bestMatch;
	}

	public void setBestMatch(ProductService bestMatch) {
		this.bestMatch = bestMatch;
	}

	public String getClassificationCode() {
		return classificationCode;
	}

	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}

	/**
	 * Temporary field until we move this to app_translation
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}