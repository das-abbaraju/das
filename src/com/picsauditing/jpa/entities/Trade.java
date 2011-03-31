package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
public class Trade extends BaseTable {

	private Trade parent;
	private Boolean product;
	private Boolean service;
	private LowMedHigh riskLevel;
	private Boolean psmApplies;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;

	private String name;
	private String name2;
	private String help;

	private List<TradeAlternate> alternates = new ArrayList<TradeAlternate>();

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

	@OneToMany(mappedBy = "trade")
	public List<TradeAlternate> getAlternates() {
		return alternates;
	}

	public void setAlternates(List<TradeAlternate> alternates) {
		this.alternates = alternates;
	}

	@Transient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	@Transient
	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	@Transient
	public boolean isLeaf() {
		return indexEnd - indexStart == 1;
	}

	@Override
	@Transient
	public String getAutocompleteValue() {
		return name2;
	}
}