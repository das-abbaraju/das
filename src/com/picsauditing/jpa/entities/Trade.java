package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
public class Trade extends BaseTable {

	private ClassificationType classificationType = ClassificationType.Master;
	private Trade parent;
	private Boolean product;
	private Boolean service;
	private LowMedHigh riskLevel;
	private Boolean psmApplies;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private Trade bestMatch;
	private String classificationCode;
	private String description;

	private List<Trade> matches = new ArrayList<Trade>();

	@Enumerated(EnumType.STRING)
	public ClassificationType getClassificationType() {
		return classificationType;
	}

	public void setClassificationType(ClassificationType classificationType) {
		this.classificationType = classificationType;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public Trade getParent() {
		return parent;
	}

	public void setParent(Trade parent) {
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
	public Trade getBestMatch() {
		return bestMatch;
	}

	public void setBestMatch(Trade bestMatch) {
		this.bestMatch = bestMatch;
	}

	@OneToMany(mappedBy = "bestMatch")
	public List<Trade> getMatches() {
		return matches;
	}

	public void setMatches(List<Trade> matches) {
		this.matches = matches;
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