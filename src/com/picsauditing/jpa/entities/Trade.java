package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
public class Trade extends BaseTable implements Indexable {

	private Trade parent;
	private Boolean product;
	private Boolean service;
	private LowMedHigh riskLevel;
	private Boolean psmApplies;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private boolean needsIndexing;

	private String name;
	private String name2;
	private String help;

	private List<TradeAlternate> alternates = new ArrayList<TradeAlternate>();
	private List<Trade> children = new ArrayList<Trade>();

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

	public boolean isNeedsIndexing() {
		return needsIndexing;
	}

	public void setNeedsIndexing(boolean needsIndexing) {
		this.needsIndexing = needsIndexing;
	}

	@OneToMany(mappedBy = "trade")
	public List<TradeAlternate> getAlternates() {
		return alternates;
	}

	public void setAlternates(List<TradeAlternate> alternates) {
		this.alternates = alternates;
	}

	@OneToMany(mappedBy = "parent")
	public List<Trade> getChildren() {
		return children;
	}

	public void setChildren(List<Trade> children) {
		this.children = children;
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

	@Transient
	public String getAutocompleteValue() {
		return name2;
	}

	// Indexing
	@Transient
	public int getId() {
		return this.id;
	}

	@Transient
	public String getIndexType() {
		return "T";
	}

	@Transient
	public List<IndexObject> getIndexValues() {
		List<IndexObject> l = new ArrayList<IndexObject>();
		// id
		l.add(new IndexObject(String.valueOf(this.id), 10));
		// name
		String[] sA = this.name.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
		for (String s : sA) {
			if (s != null && !s.isEmpty())
				l.add(new IndexObject(s, 8));
		}
		sA = this.name2.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
		for (String s : sA) {
			if (s != null && !s.isEmpty())
				l.add(new IndexObject(s, 6));
		}
		// Alternates
		for (TradeAlternate ta : getAlternates()) {
			sA = ta.getName().toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
			for (String s : sA) {
				if (s != null && !s.isEmpty())
					l.add(new IndexObject(s, 4));
			}
		}
		return l;
	}

	@Transient
	public String getReturnType() {
		return "trade";
	}

	@Transient
	public String getSearchText() {
		List<String> st = new ArrayList<String>();
		List<String> tas = new ArrayList<String>();
		st.add(this.name);
		st.add(this.name2);

		for (TradeAlternate ta : getAlternates())
			tas.add(ta.getName());

		String alternate = Strings.implode(tas, ", ");
		st.add(alternate);

		return Strings.implode(st, "|");
	}

	@Transient
	public String getViewLink() {
		return "TradeTaxonomy.action";
	};
}