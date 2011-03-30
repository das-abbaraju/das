package com.picsauditing.jpa.entities;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_product_service")
public class ProductService extends BaseTable {

	private ClassificationType classificationType = ClassificationType.Master;
	private ProductService parent;
	private Boolean product;
	private Boolean service;
	private LowMedHigh riskLevel;
	private Boolean psmApplies;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private ProductService bestMatch;
	private String classificationCode;
	private String description;

	private Map<ClassificationType, ProductService> mappedServices = new LinkedHashMap<ClassificationType, ProductService>();

	@Enumerated(EnumType.STRING)
	public ClassificationType getClassificationType() {
		return classificationType;
	}

	public void setClassificationType(ClassificationType classificationType) {
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

	public Boolean getProduct() {
		return product;
	}

	public void setProduct(Boolean product) {
		this.product = product;
	}

	public Boolean getService() {
		return service;
	}

	public void setService(Boolean service) {
		this.service = service;
	}

	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		this.riskLevel = riskLevel;
	}

	public Boolean getPsmApplies() {
		return psmApplies;
	}

	public void setPsmApplies(Boolean psmApplies) {
		this.psmApplies = psmApplies;
	}

	@Transient
	public boolean isProductI() {
		if (product == null) {
			if (parent == null)
				return false;
			else
				return parent.isProductI();
		}
		return product;
	}
	
	@Transient
	public boolean isServiceI() {
		if (service == null) {
			if (parent == null)
				return false;
			else
				return parent.isServiceI();
		}
		return service;
	}

	@Transient
	public LowMedHigh getRiskLevelI() {
		if (riskLevel == null) {
			if (parent == null)
				return LowMedHigh.Low;
			else
				return parent.getRiskLevelI();
		}
		return riskLevel;
	}

	@Transient
	public boolean isPsmAppliesI() {
		if (psmApplies == null) {
			if (parent == null)
				return false;
			else
				return parent.isPsmAppliesI();
		}
		return psmApplies;
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
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "bestMatch")
	@MapKey(name = "classificationType")
	public Map<ClassificationType, ProductService> getMappedServices() {
		return mappedServices;
	}

	public void setMappedServices(Map<ClassificationType, ProductService> suncorServices) {
		this.mappedServices = suncorServices;
	}

	@Transient
	public boolean isLeaf() {
		return indexEnd - indexStart == 1;
	}

	@Override
	public String toString() {
		if (parent == null)
			return this.description;
		return parent.getDescription() + ": " + description;
		// return parent.toString() + ": " + description;
	}

	@Override
	@Transient
	public String getAutocompleteValue() {
		return "(" + classificationCode + ") " + description;
	}
}