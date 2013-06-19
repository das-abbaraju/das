package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditCategory extends BaseTableRequiringLanguages implements Comparable<AuditCategory> {

	public static final int COMPANY_INFORMATION = 2;
	public static final int GENERAL_SAFETY_INFORMATION = 8;
	public static final int WORK_HISTORY = 6;
	public static final int SAFETY_POLICIES = 7;
	public static final int TRAINING = 25;
	public static final int EMR = 152;
	public static final int GENERAL_INFORMATION = 155;
	public static final int LOSS_RUN = 159;
	public static final int SUPPLIER_DIVERSITY = 184;
	public static final int FINANCIAL_HISTORY = 200;
	public static final int WCB = 210;
	public static final int CITATIONS = 278;
	public static final int BUSINESS_INTERRUPTION_EVAL = 1682;
	public static final int PRODUCT_SAFETY_EVAL = 1683;
	public static final int SERVICE_SAFETY_EVAL = 1721;
	public static final int TRANSPORTATION_SAFETY_EVAL = 3251;
	public static final int SSIP_EVALUATION = 3707;
	public static final int SSIP = 3732;

	private AuditType auditType;
	private AuditCategory parent;
	private String name;
	private int number;
	private int numRequired;
	private int numQuestions;
	private String helpText;
	private boolean hasHelpText;
	private String uniqueCode;
	private float scoreWeight = 0f;
	private int columns = 1;
	protected Date effectiveDate;
	protected Date expirationDate;

	private List<AuditCategory> subCategories = new ArrayList<AuditCategory>();
	private List<AuditQuestion> questions = new ArrayList<AuditQuestion>();

	public AuditCategory() {

	}

	public AuditCategory(AuditCategory a, AuditType at) {
		this.name = a.name;
		this.number = a.getNumber();
		this.numQuestions = a.getNumQuestions();
		this.numRequired = a.getNumRequired();
		this.helpText = a.getHelpText();
		this.auditType = at;
	}

	public AuditCategory(AuditCategory a) {
		this.auditType = a.auditType;
		this.parent = a.parent;
		this.name = a.name;
		this.number = a.getNumber();
		this.numRequired = a.numRequired;
		this.numQuestions = a.numQuestions;
		this.helpText = a.getHelpText();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return this.auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public AuditCategory getParent() {
		return parent;
	}

	public void setParent(AuditCategory parent) {
		this.parent = parent;
	}

	@Transient
	public AuditCategory getTopParent() {
		if (parent != null) {
			return parent.getTopParent();
		}

		return this;
	}

	@Transient
	public AuditType getParentAuditType() {
		if (auditType == null) {
			return parent.getParentAuditType();
		}

		return auditType;
	}

	@Transient
	public String getName() {
		if (name != null) {
			return name;
		}

		return new TranslatableString(getI18nKey("name")).toTranslatedString();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	@Override
	public String getI18nKey() {
		if (Strings.isEmpty(uniqueCode)) {
			return super.getI18nKey();
		} else {
			return "AuditCategory." + uniqueCode;
		}
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Integer)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Transient
	public String getFullNumber() {
		if (parent == null) {
			return "" + number;
		}

		return parent.getFullNumber() + "." + number;
	}

	@Transient
	public String getFullyQualifiedName() {
		if (parent == null) {
			return getAuditType().getName().toString() + " - " + name;
		}

		return parent.getFullyQualifiedName() + " : " + name;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Integer)
	public int getNumRequired() {
		return this.numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	@Column(length = 50)
	@ReportField(type = FieldType.String)
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@ReportField(type = FieldType.Float)
	public float getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(float scoreWeight) {
		this.scoreWeight = scoreWeight;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.Integer)
	public int getNumQuestions() {
		return this.numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	@Transient
	public void recalculateQuestions() {
		numQuestions = 0;
		numRequired = 0;
		for (AuditQuestion question : questions) {
			if (question.isCurrent()) {
				numQuestions++;
				if (question.isRequired()) {
					numRequired++;
				}
			}
		}
	}

	@Transient
	public String getHelpText() {
		if (helpText != null) {
			return helpText;
		}

		return new TranslatableString(getI18nKey("helpText")).toTranslatedString();
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@Temporal(TemporalType.DATE)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Transient
	public boolean isCurrent() {
		Date now = new Date();
		return isCurrent(now);
	}

	@Transient
	public boolean isCurrent(Date now) {
		if (effectiveDate != null && effectiveDate.after(now)) {
			return false;
		}
		if (expirationDate != null && expirationDate.before(now)) {
			return false;
		}
		return true;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy("number")
	public List<AuditCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<AuditCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	@Transient
	public List<AuditQuestion> getEffectiveQuestions(Date date) {
		final Date comparison;
		if (date == null) {
			comparison = new Date();
		} else {
			comparison = date;
		}
		return new Grepper<AuditQuestion>() {
			@Override
			public boolean check(AuditQuestion t) {
				return t.isCurrent(comparison);
			}
		}.grep(questions);
	}

	public boolean isHasHelpText() {
		return hasHelpText;
	}

	public void setHasHelpText(boolean hasHelpText) {
		this.hasHelpText = hasHelpText;
	}

	@Transient
	public Set<AuditCategory> getChildren() {
		Set<AuditCategory> children = new HashSet<AuditCategory>();
		addChildren(children, this);

		return children;
	}

	@Transient
	private void addChildren(Set<AuditCategory> children, AuditCategory category) {
		children.add(category);
		if (category.getSubCategories().size() > 0) {
			for (AuditCategory auditSubCategory : category.getSubCategories()) {
				addChildren(children, auditSubCategory);
			}
		}
	}

	@Transient
	public List<AuditCategory> getAncestors() {
		List<AuditCategory> ancestors = new ArrayList<AuditCategory>();
		addAncestors(ancestors, this);

		return ancestors;
	}

	@Transient
	private void addAncestors(List<AuditCategory> ancestors, AuditCategory category) {
		if (category.getParent() != null) {
			addAncestors(ancestors, category.getParent());
		}

		ancestors.add(category);
	}

	@Transient
	@Override
	public String getAutocompleteItem() {
		return "[" + id + "] " + name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject j = super.toJSON(full);
		j.put("auditType", auditType == null ? null : auditType.toJSON());
		j.put("parent", parent == null ? null : parent.toJSON());
		j.put("name", name);

		if (full) {
			JSONArray qArray = new JSONArray();
			for (AuditQuestion q : questions) {
				qArray.add(q.toJSON());
			}
			j.put("questions", qArray);

			JSONArray subArray = new JSONArray();
			for (AuditCategory sub : subCategories) {
				subArray.add(sub.toJSON());
			}

			j.put("subCategories", subArray);
		}

		return j;
	}

	@Override
	public int compareTo(AuditCategory other) {
		if (other == null) {
			return 1;
		}

		int cmp = getAuditType().compareTo(other.getAuditType());

		if (cmp != 0) {
			return cmp;
		}

		String[] thisFullNumber = getFullNumber().split("\\.");
		String[] otherFullNumber = other.getFullNumber().split("\\.");

		for (int i = 0; i < Math.max(thisFullNumber.length, otherFullNumber.length); i++) {
			if (thisFullNumber.length - 1 < i) {
				return -1;
			}
			if (otherFullNumber.length - 1 < i) {
				return 1;
			}

			int thisNumber = Integer.parseInt(thisFullNumber[i]);
			int otherNumber = Integer.parseInt(otherFullNumber[i]);

			int numCompare = thisNumber - otherNumber;

			if (numCompare != 0) {
				return numCompare;
			}
		}

		return getFullNumber().compareTo(other.getFullNumber());
	}

	@Override
	public String toString() {
		return getFullNumber() + " " + name;
	}

	@Transient
	public boolean isPolicyLimitsCategory() {
		return "limits".equals(this.getUniqueCode());
	}

	@Transient
	public boolean isPolicyInformationCategory() {
		return "policyInformation".equals(this.getUniqueCode());
	}

	@Transient
	public List<String> getAvailableRequiredLanguages() {
		if (parent != null) {
			return parent.getLanguages();
		} else {
			return auditType.getLanguages();
		}
	}

	public void cascadeRequiredLanguages(List<String> add, List<String> remove) {
		for (AuditCategory category : subCategories) {
			category.addAndRemoveRequiredLanguages(add, remove);
		}

		for (AuditQuestion question : questions) {
			question.addAndRemoveRequiredLanguages(add, remove);
		}
	}

	public boolean hasMissingChildRequiredLanguages() {
		boolean hasMissingChild = false;

		for (AuditCategory category : subCategories) {
			hasMissingChild = hasMissingChild || category.hasMissingChildRequiredLanguages();
		}
		for (AuditQuestion question : questions) {
			hasMissingChild = hasMissingChild || question.getLanguages().isEmpty();
		}

		return hasMissingChild || getLanguages().isEmpty();
	}

	@Transient
	public boolean requiresViewFullPQFPermission() {
		if (id == WORK_HISTORY) {
			return true;
		}
		if (id == FINANCIAL_HISTORY) {
			return true;
		}
		return false;
	}
}