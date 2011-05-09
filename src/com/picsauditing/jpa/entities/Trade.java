package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.util.Hierarchical;
import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Node;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Tree;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Trade extends AbstractIndexableTable implements Hierarchical<Trade> {

	private Trade parent;
	private Boolean product;
	private Boolean service;
	private Boolean psmApplies;
	private LowMedHigh productRisk;
	private LowMedHigh safetyRisk;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private int contractorCount;
	private boolean needsIndexing;

	private TranslatableString name;
	private TranslatableString name2;
	private TranslatableString help;

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

	@Transient
	public Boolean getProductI() {
		if (product == null) {
			if (parent != null)
				return parent.getProductI();
			else
				return false;
		}
		return product;
	}

	public void setProductI(Boolean product) {
		if (parent != null && product == parent.getProductI())
			this.product = null;
		else
			this.product = product;
	}

	public Boolean getService() {
		return service;
	}

	public void setService(Boolean service) {
		this.service = service;
	}

	@Transient
	public boolean getServiceI() {
		if (service == null) {
			if (parent == null)
				return false;
			else
				return parent.getServiceI();
		}
		return service;
	}

	public void setServiceI(Boolean service) {
		if (parent != null && service == parent.getServiceI())
			this.service = null;
		else
			this.service = service;
	}

	public Boolean getPsmApplies() {
		return psmApplies;
	}

	public void setPsmApplies(Boolean psmApplies) {
		this.psmApplies = psmApplies;
	}

	@Transient
	public boolean getPsmAppliesI() {
		if (psmApplies == null) {
			if (parent != null)
				return parent.getPsmAppliesI();
			else
				return false;
		}
		return psmApplies;
	}

	public void setPsmAppliesI(Boolean psmApplies) {
		if (parent != null && psmApplies == parent.getPsmAppliesI())
			this.psmApplies = null;
		else
			this.psmApplies = psmApplies;
	}

	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	@Transient
	public LowMedHigh getProductRiskI() {
		if (productRisk == null) {
			if (parent == null)
				return LowMedHigh.Low;
			else
				return parent.getProductRiskI();
		}
		return productRisk;
	}

	public void setProductRiskI(LowMedHigh productRisk) {
		if (parent != null && productRisk == parent.getProductRiskI())
			this.productRisk = null;
		else
			this.productRisk = productRisk;
	}

	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Transient
	public LowMedHigh getSafetyRiskI() {
		if (safetyRisk == null) {
			if (parent == null)
				return LowMedHigh.Low;
			else
				return parent.getSafetyRiskI();
		}
		return safetyRisk;
	}

	public void setSafetyRiskI(LowMedHigh safetyRisk) {
		if (parent != null && safetyRisk == parent.getSafetyRiskI())
			this.safetyRisk = null;
		else
			this.safetyRisk = safetyRisk;
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

	@OneToMany(mappedBy = "trade", cascade = CascadeType.ALL)
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

	@Override
	@Transient
	public List<IndexObject> getIndexValues() {
		List<IndexObject> indexValues = super.getIndexValues();
		for (TradeAlternate ta : getAlternates()) {
			String[] strArray = ta.getName().toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
			for (String str : strArray) {
				if (!Strings.isEmpty(str))
					indexValues.add(new IndexObject(str, 4));
			}
		}
		return indexValues;
	}

	/**
	 * The name of this trade that's commonly used to describe it. Does not need
	 * the parent trade to make sense. Can stand alone in a list and be
	 * understood.
	 */
	@Transient
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 8)
	public TranslatableString getName() {
		return name;
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	/**
	 * The short version of the trade, typically only included when in context
	 * with its parent trades
	 */
	@Transient
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 6)
	public TranslatableString getName2() {
		return name2;
	}

	public void setName2(TranslatableString name2) {
		this.name2 = name2;
	}

	@Transient
	public TranslatableString getHelp() {
		return help;
	}

	public void setHelp(TranslatableString help) {
		this.help = help;
	}

	@Transient
	public boolean isLeaf() {
		return indexEnd - indexStart == 1;
	}

	@Transient
	public String getAutocompleteValue() {
		return name.toString();
	}

	@Transient
	public String getIndexType() {
		return "T";
	}

	@Transient
	public String getReturnType() {
		return "trade";
	}

	@Transient
	public String getSearchText() {
		List<String> st = new ArrayList<String>();
		List<String> tas = new ArrayList<String>();
		st.add(this.name.toString());
		st.add(this.name2.toString());

		for (TradeAlternate ta : getAlternates())
			tas.add(ta.getName());

		String alternate = Strings.implode(tas, ", ");
		st.add(alternate);

		return Strings.implode(st, "|");
	}

	@Transient
	public String getViewLink() {
		return "TradeTaxonomy.action";
	}

	@Transient
	public Tree<Trade> getHierarchy() {
		Node<Trade> root = getHierarchy(this);
		Trade parent = this.parent;
		while (parent != null) {
			root = new Node<Trade>(parent, root);

			parent = parent.parent;
		}

		return new Tree<Trade>(root);
	}

	private Node<Trade> getHierarchy(Trade t) {
		Node<Trade> node = new Node<Trade>(t);
		for (Trade child : t.getChildren()) {
			node.addChild(getHierarchy(child));
		}
		return node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("data", name.toString() + " (" + contractorCount + ")");

		if (!isLeaf()) {
			if (full) {
				json.put("state", "open");
			} else {
				json.put("state", "closed");
			}
		}

		JSONObject attr = new JSONObject();
		attr.put("id", id);
		attr.put("class", "trade-" + id);
		if (getProductI() && getServiceI())
			attr.put("rel", "product-service");
		else if (getServiceI())
			attr.put("rel", "service");
		else if (getProductI())
			attr.put("rel", "product");

		json.put("attr", attr);

		return json;
	}

	@Override
	public String toString() {
		return String.valueOf(name);
	}

	public int getContractorCount() {
		return contractorCount;
	}

	public void setContractorCount(int contractorCount) {
		this.contractorCount = contractorCount;
	}
}