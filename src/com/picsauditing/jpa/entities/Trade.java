package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.google.common.base.Objects;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.search.IndexOverrideIgnore;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Hierarchical;
import com.picsauditing.util.IndexObject;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
@SqlResultSetMapping(name = "matchingTradeResults", entities = @EntityResult(entityClass = Trade.class), columns = @ColumnResult(name = "matching"))
@IndexableOverride(ignores = { @IndexOverrideIgnore(methodName = "getId") })
public class Trade extends AbstractIndexableTable implements Hierarchical<Trade> {

	static public final int TOP_ID = 5;
	static public final Trade TOP = new Trade(TOP_ID);

	private Trade parent = TOP;
	private Boolean product;
	private Boolean service;
	private Boolean transportation;
	private Boolean psmApplies;
	private LowMedHigh productRisk;
	private LowMedHigh safetyRisk;
	private LowMedHigh transportationRisk;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;
	private int contractorCount;
	private String imageExtension;
	private boolean needsIndexing;
	private boolean selectable = true;
	private Float naicsTRIR;
	private Float naicsLWCR;

	private TranslatableString name;
	private TranslatableString name2;
	private TranslatableString help;

	private List<TradeAlternate> alternates = new ArrayList<TradeAlternate>();
	private List<Trade> children = new ArrayList<Trade>();

	public Trade() {
	}

	public Trade(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public Trade getParent() {
		return parent;
	}

	public void setParent(Trade parent) {
		this.parent = parent;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.Boolean)
	public Boolean getProduct() {
		return product;
	}

	public void setProduct(Boolean product) {
		this.product = product;
	}

	@Transient
	public Boolean getProductI() {
		if (product == null) {
			if (Objects.equal(parent, TOP))
				return false;
			else
				return parent.getProductI();
		}
		return product;
	}

	public void setProductI(Boolean product) {
		if (!Objects.equal(parent, TOP) && product == parent.getProductI())
			this.product = null;
		else
			this.product = product;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.Boolean)
	public Boolean getService() {
		return service;
	}

	public void setService(Boolean service) {
		this.service = service;
	}

	@Transient
	public boolean getServiceI() {
		if (service == null) {
			if (Objects.equal(parent, TOP))
				return false;
			else
				return parent.getServiceI();
		}
		return service;
	}

	public void setServiceI(Boolean service) {
		if (!Objects.equal(parent, TOP) && service == parent.getServiceI())
			this.service = null;
		else
			this.service = service;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.Boolean)
	public Boolean getTransportation() {
		return transportation;
	}

	public void setTransportation(Boolean transportation) {
		this.transportation = transportation;
	}

	@Transient
	public boolean getTransportationI() {
		if (transportation == null) {
			if (Objects.equal(parent, TOP))
				return false;
			else
				return parent.getTransportationI();
		}
		return transportation;
	}

	public void setTransportationI(Boolean transportation) {
		if (!Objects.equal(parent, TOP) && transportation == parent.getTransportationI())
			this.service = null;
		else
			this.transportation = transportation;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.Boolean)
	public Boolean getPsmApplies() {
		return psmApplies;
	}

	public void setPsmApplies(Boolean psmApplies) {
		this.psmApplies = psmApplies;
	}

	@Transient
	public boolean getPsmAppliesI() {
		if (psmApplies == null) {
			if (Objects.equal(parent, TOP))
				return false;
			else
				return parent.getPsmAppliesI();
		}
		return psmApplies;
	}

	public void setPsmAppliesI(Boolean psmApplies) {
		if (!Objects.equal(parent, TOP) && psmApplies == parent.getPsmAppliesI())
			this.psmApplies = null;
		else
			this.psmApplies = psmApplies;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.LowMedHigh)
	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	@Transient
	public LowMedHigh getProductRiskI() {
		if (productRisk == null) {
			if (Objects.equal(parent, TOP))
				return LowMedHigh.Low;
			else
				return parent.getProductRiskI();
		}
		return productRisk;
	}

	public void setProductRiskI(LowMedHigh productRisk) {
		if (!Objects.equal(parent, TOP) && productRisk == parent.getProductRiskI())
			this.productRisk = null;
		else
			this.productRisk = productRisk;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.LowMedHigh)
	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Transient
	public LowMedHigh getSafetyRiskI() {
		if (safetyRisk == null) {
			if (Objects.equal(parent, TOP))
				return LowMedHigh.Low;
			else
				return parent.getSafetyRiskI();
		}
		return safetyRisk;
	}

	public void setSafetyRiskI(LowMedHigh safetyRisk) {
		if (!Objects.equal(parent, TOP) && safetyRisk == parent.getSafetyRiskI())
			this.safetyRisk = null;
		else
			this.safetyRisk = safetyRisk;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.LowMedHigh)
	public LowMedHigh getTransportationRisk() {
		return transportationRisk;
	}

	public void setTransportationRisk(LowMedHigh transportationRisk) {
		this.transportationRisk = transportationRisk;
	}

	@Transient
	public LowMedHigh getTransportationRiskI() {
		if (transportationRisk == null) {
			if (Objects.equal(parent, TOP))
				return LowMedHigh.Low;
			else
				return parent.getTransportationRiskI();
		}
		return transportationRisk;
	}

	public void setTransportationRiskI(LowMedHigh transportationRisk) {
		if (!Objects.equal(parent, TOP) && transportationRisk == parent.getTransportationRiskI())
			this.transportationRisk = null;
		else
			this.transportationRisk = transportationRisk;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Low, type = FieldType.Integer)
	public int getIndexLevel() {
		return indexLevel;
	}

	public void setIndexLevel(int indexLevel) {
		this.indexLevel = indexLevel;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Integer)
	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Integer)
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

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Integer)
	public int getContractorCount() {
		return contractorCount;
	}

	public void setContractorCount(int contractorCount) {
		this.contractorCount = contractorCount;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	@Transient
	public String getImageLocationI() {
		if (Strings.isEmpty(imageExtension)) {
			if (Objects.equal(parent, TOP))
				return "";
			else
				return parent.getImageLocationI();
		}
		return getImageLocation();
	}

	@Transient
	public String getImageLocation() {
		if (Strings.isEmpty(imageExtension))
			return "";
		return "/files/" + FileUtils.thousandize(id) + "trade_" + id + "." + imageExtension;
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
			String[] strArray = ta.getName().toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", " ").split("\\s+");
			for (String str : strArray) {
				if (!Strings.isEmpty(str))
					indexValues.add(new IndexObject(str, 6));
			}
		}

		if (parent != null && parent.getId() != TOP_ID) {
			for (IndexObject parentIndex : parent.getIndexValues()) {
				IndexObject inheritedIndex = new IndexObject(parentIndex.getValue(), parentIndex.getWeight() - 2);
				indexValues.add(inheritedIndex);
				if (inheritedIndex.getWeight() < 1)
					inheritedIndex.setWeight(1);
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
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 6)
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
	@IndexableField(type = IndexValueType.MULTISTRINGTYPE, weight = 8)
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

	/**
	 * Is this trade equal to t or a child of t? For example, Residential
	 * Construction is a childOf Residential Construction and Construction.
	 * 
	 * @param candidateParent
	 * @return
	 */
	@Transient
	public boolean childOf(Trade candidateParent) {
		if (this.parent == null)
			// Gone all the way up and didn't find our parent
			return false;

		if (candidateParent == null)
			return false;

		if (this.equals(candidateParent))
			// We don't consider you to be a child of yourself
			return false;

		// Parent Level,Start,End = 1,1,12
		// Child Level,Start,End = 2,6,9
		return candidateParent.indexLevel < this.indexLevel && candidateParent.indexStart < this.indexStart
				&& candidateParent.indexEnd > this.indexEnd;
	}

	@Transient
	public boolean parentOf(Trade t) {
		return t.childOf(this);
	}

	@Transient
	public boolean isLeaf() {
		return indexEnd - indexStart == 1;
	}

	@Override
	public boolean showChildren() {
		return contractorCount > 0;
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
	public String getAutocompleteItem() {
		return "[" + id + "]" + name.toString();
	}

	@Transient
	public String getAutocompleteValue() {
		return name.toString();
	}

	@Transient
	public String getNodeDisplay() {
		if (name2 == null || !name2.isExists()) {
			return name.toString();
		} else {
			return name2.toString();
		}
	}

	@SuppressWarnings("unchecked")
	@Transient
	@Override
	public Map<String, String> getNodeAttributes() {
		Map<String, String> attr = new JSONObject();
		attr.put("id", "" + id);
		attr.put("class", "trade-" + id);
		if (getProductI() && getServiceI())
			attr.put("rel", "product-service");
		else if (getServiceI())
			attr.put("rel", "service");
		else if (getProductI())
			attr.put("rel", "product");
		return attr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		if (name != null && name.isExists())
			json.put("name", name.toString());
		if (name2 != null && name2.isExists())
			json.put("name2", name2.toString());
		if (help != null && help.isExists())
			json.put("name2", help.toString());

		if (getProductI() && getServiceI())
			json.put("type", "product-service");
		else if (getServiceI())
			json.put("type", "service");
		else if (getProductI())
			json.put("type", "product");

		return json;
	}

	@Override
	public String toString() {
		return String.valueOf(name);
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Boolean)
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Float)
	public Float getNaicsTRIR() {
		return naicsTRIR;
	}

	public void setNaicsTRIR(Float naicsTRIR) {
		this.naicsTRIR = naicsTRIR;
	}

	@Transient
	public Float getNaicsTRIRI() {
		if (naicsTRIR != null && naicsTRIR != 0.0)
			return naicsTRIR;
		else if (parent != null)
			return parent.getNaicsTRIRI();
		else
			return Float.valueOf(4);
	}

	@ReportField(category = FieldCategory.Classification, importance = FieldImportance.Average, type = FieldType.Float)
	public Float getNaicsLWCR() {
		return naicsLWCR;
	}

	public void setNaicsLWCR(Float naicsLWCR) {
		this.naicsLWCR = naicsLWCR;
	}

	@Transient
	public Float getNaicsLWCRI() {
		if (naicsLWCR != null && naicsLWCR != 0.0)
			return naicsLWCR;
		else if (parent != null)
			return parent.getNaicsLWCRI();
		else
			return Float.valueOf(4);
	}

	@Transient
	public boolean isRemoved() {
		return false;
	}
}