package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.builders.AuditTypeBuilder;
import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditType extends BaseTableRequiringLanguages implements Comparable<AuditType>, java.io.Serializable {

	public static final int PQF = 1;
	public static final int MANUAL_AUDIT = 2;
	public static final int IMPLEMENTATION_AUDIT = 3;
	public static final int FIELD = 5;
	public static final int DA = 6;
	public static final int WELCOME = 9;
	public static final int ANNUALADDENDUM = 11;

	// AKA Competency Review
	public static final int INTEGRITYMANAGEMENT = 17;

	// AKA Training Verification
	public static final int IMPLEMENTATIONAUDITPLUS = 29;

	public static final int HUNTSMAN_EBIX = 31;
	public static final int COR = 72;
	public static final int SUPPLEMENTCOR = 84;
	public static final int BPIISNSPECIFIC = 87;
	public static final int BPIISNCASEMGMT = 96;
	public static final int HSE_COMPETENCY = 99;
	public static final int HSE_COMPETENCY_REVIEW = 100;
	public static final int SHELL_COMPETENCY_REVIEW = 100;
	public static final int WA_STATE_VERIFICATION = 176;
	public static final int PQF_SUNCOR = 195;
	public static final int IMPORT_PQF = 232;
	public static final int CAN_QUAL_PQF = 269;
	public static final int ISN_CAN_QUAL_PQF = 270;
	public static final int COMPLYWORKS_PQF = 271;
	public static final int ISN_US_PQF = 281;
	public static final int IEC_AUDIT = 313;
	public static final int IHG_INSURANCE_QUESTIONAIRE = 509;
	public static final int SSIP = 514;

	public static final int ANNUAL_ADDENDUM_RETENSION_PERIOD_IN_YEARS = 3;

	protected String name;
    private String slug;
	protected AuditTypeClass classType = AuditTypeClass.Audit;
	protected int displayOrder = 100;
	protected String description;
	protected boolean hasMultiple;
	protected boolean isScheduled;
	protected boolean hasAuditor;
	protected boolean canContractorView;
	protected boolean canContractorEdit;
	protected boolean canOperatorView;
	protected boolean renewable = true;
	protected boolean scoreable = false;
	protected Integer monthsToExpire;
	protected Account account;
	protected OpPerms editPermission;
	protected Workflow workFlow;
	protected ScoreType scoreType;
	protected User assignAudit;
	protected User editAudit;
	protected String assigneeLabel;
    protected AuditTypePeriod period = AuditTypePeriod.None;
    protected int anchorDay = 1;
    protected int anchorMonth = 1;
    protected int advanceDays = 0;
    protected int maximumActive = 1;
    protected AuditType parent = null;
    protected AuditStatus rollbackStatus = null;

	protected List<AuditCategory> categories = new ArrayList<AuditCategory>();

	protected List<AuditCategory> topCategories;

	public static final Set<Integer> CANADIAN_PROVINCES = new HashSet<Integer>(Arrays.asList(new Integer[] { 145, 146,
			143, 170, 261, 168, 148, 147, 169, 166, 167, 144 }));

	public AuditType() {
	}

	public AuditType(int id) {
		this.id = id;
	}

	public AuditType(AuditType a) {
		this.account = a.getAccount();
		this.name = a.getName();
        this.slug = a.getSlug();
		this.canContractorEdit = a.isCanContractorEdit();
		this.canContractorView = a.isCanContractorView();
		this.classType = a.getClassType();
		this.description = a.getDescription();
		this.displayOrder = a.getDisplayOrder();
		this.hasAuditor = a.isHasAuditor();
		this.hasMultiple = a.isHasMultiple();
		this.isScheduled = a.isScheduled();
		this.monthsToExpire = a.getMonthsToExpire();
		this.renewable = a.isRenewable();
        this.period = a.getPeriod();
        this.maximumActive = a.getMaximumActive();
        this.advanceDays = a.getAdvanceDays();
        this.anchorDay = a.getAnchorDay();
        this.anchorMonth = a.getAnchorMonth();
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.AuditTypeClass, i18nKeyPrefix = "AuditTypeClass", importance = FieldImportance.Required)
	public AuditTypeClass getClassType() {
		return classType;
	}

	public void setClassType(AuditTypeClass classType) {
		this.classType = classType;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Low, width = 400)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * More than one audit of this type can be active for a contractor at a time
	 * 
	 * @return
	 */
	public boolean isHasMultiple() {
		return hasMultiple;
	}

	public void setHasMultiple(boolean hasMultiple) {
		this.hasMultiple = hasMultiple;
	}

	@Column(name = "isScheduled")
	@ReportField(type = FieldType.Boolean)
	public boolean isScheduled() {
		return isScheduled;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	@ReportField(type = FieldType.Boolean)
	public boolean isHasAuditor() {
		return hasAuditor;
	}

	public void setHasAuditor(boolean hasAuditor) {
		this.hasAuditor = hasAuditor;
	}

	public boolean isCanContractorView() {
		return canContractorView;
	}

	public void setCanContractorView(boolean canContractorView) {
		this.canContractorView = canContractorView;
	}

	public boolean isCanContractorEdit() {
		return canContractorEdit;
	}

	public void setCanContractorEdit(boolean canContractorEdit) {
		this.canContractorEdit = canContractorEdit;
	}

	public boolean isCanOperatorView() {
		return canOperatorView;
	}

	public void setCanOperatorView(boolean canOperatorView) {
		this.canOperatorView = canOperatorView;
	}

	@ReportField(type = FieldType.Integer, importance = FieldImportance.Average)
	public Integer getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(Integer monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	/**
	 * Can the existing audit be renewed (aka extended aka resubmitted)? <br>
	 * Examples of true: PQF, Site Specific Audits <br>
	 * Examples of false: Desktop, Office, Field, Policies
	 * 
	 * @return
	 */
	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Average)
	public boolean isRenewable() {
		return renewable;
	}

	public void setRenewable(boolean renewable) {
		this.renewable = renewable;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = true)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@ManyToOne
	@JoinColumn(name = "assignAudit", nullable = true)
	public User getAssignAudit() {
		return assignAudit;
	}

	public void setAssignAudit(User assignAudit) {
		this.assignAudit = assignAudit;
	}

	@ManyToOne
	@JoinColumn(name = "editAudit", nullable = true)
	public User getEditAudit() {
		return editAudit;
	}

	public void setEditAudit(User editAudit) {
		this.editAudit = editAudit;
	}

	public String getAssigneeLabel() {
		return assigneeLabel;
	}

	public void setAssigneeLabel(String assigneeLabel) {
		this.assigneeLabel = assigneeLabel;
	}

	@OneToMany(mappedBy = "auditType")
	@OrderBy("number")
	public List<AuditCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategory> categories) {
		this.categories = categories;
	}

	@Transient
	public List<AuditCategory> getTopCategories() {
		if (topCategories == null) {
			topCategories = new ArrayList<AuditCategory>();
			for (AuditCategory cat : categories) {
				if (cat.getParent() == null) {
					topCategories.add(cat);
				}
			}
		}

		return topCategories;
	}

	@Enumerated(EnumType.STRING)
	public OpPerms getEditPermission() {
		return editPermission;
	}

	public void setEditPermission(OpPerms editPermission) {
		this.editPermission = editPermission;
	}

	/**
	 * Return the name of the icon we use on reports for each audit type
	 * 
	 * @param auditTypeID
	 * @return
	 */
	public static String getIcon(int auditTypeID) {
		String auditType = "";
		if (auditTypeID == AuditType.PQF) {
			auditType = "PQF";
		}
		if (auditTypeID == AuditType.MANUAL_AUDIT) {
			auditType = "Desktop";
		}
		if (auditTypeID == AuditType.IMPLEMENTATION_AUDIT) {
			auditType = "Office";
		}
		if (auditTypeID == AuditType.DA) {
			auditType = "DA";
		}
		return "icon_" + auditType + ".gif";
	}

    @Transient
    public boolean isRollback() {
        if (isAnnualAddendum() || getClassType().isPolicy() || getRollbackStatus() != null)
            return true;

        return false;
    }

	@Transient
	public boolean isShowManual() {
		if (this.id == IMPLEMENTATION_AUDIT) {
			return true;
		}
		if (this.id == MANUAL_AUDIT) {
			return true;
		}
		if (this.id == BPIISNCASEMGMT) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isPicsPqf() {
		return (id == PQF);
	}

	@Transient
	public boolean isPqf() {
		return this.getClassType().isPqf();
	}

	@Transient
	public boolean isSsip() {
		return (id == SSIP);
	}

	@Transient
	public boolean isDesktop() {
		return (id == MANUAL_AUDIT);
	}

	@Transient
	public boolean isImplementation() {
		return id == IMPLEMENTATION_AUDIT;
	}

	@Transient
	public boolean isAnnualAddendum() {
		return (id == ANNUALADDENDUM);
	}

	@Transient
	public boolean isWCB() {
		return CANADIAN_PROVINCES.contains(id);
	}

    @Transient
    public boolean isCorIec() {
        return (id == COR || id == IEC_AUDIT);
    }

    @Transient
	public boolean isCorIecWaState() {
		return (isCorIec() || id == WA_STATE_VERIFICATION);
	}

	@Transient
	public boolean isExtractable() {
		return (id == CAN_QUAL_PQF || id == COMPLYWORKS_PQF || id == ISN_CAN_QUAL_PQF || id == ISN_US_PQF);
	}

	@Transient
	public boolean isEmployeeSpecificAudit() {
		return classType.isImEmployee() && (id != HSE_COMPETENCY && id != HSE_COMPETENCY_REVIEW);
	}

	@Override
	public int compareTo(AuditType o) {
		return this.getName().compareTo(o.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject j = super.toJSON(full);
		j.put("name", getName());
		j.put("displayOrder", displayOrder);

		if (full) {
			JSONArray categoriesArray = new JSONArray();
			for (AuditCategory c : categories) {
				categoriesArray.add(c.toJSON());
			}
			j.put("categories", categoriesArray);
		}

		return j;
	}

	@Override
	public String toString() {
		return getName() + "(" + id + ")";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workflowID")
	public Workflow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(Workflow workFlow) {
		this.workFlow = workFlow;
	}

	@Enumerated(EnumType.STRING)
	public ScoreType getScoreType() {
		return scoreType;
	}

	public void setScoreType(ScoreType scoreType) {
		this.scoreType = scoreType;
	}

	/**
	 * @return boolean indicating whether or not this Audit Type can be scored.
	 *         Used for implementing audit score.
	 */
	@Transient
	@ReportField(type = FieldType.Boolean)
	public boolean isScoreable() {
		return scoreType != null;
	}

	@Override
	@Transient
	public String getAutocompleteItem() {
		return getName();
	}

	public void cascadeRequiredLanguages(List<String> add, List<String> remove) {
		for (AuditCategory category : categories) {
			if (category.getParent() == null) {
				category.addAndRemoveRequiredLanguages(add, remove);
			}
		}
	}

	public boolean hasMissingChildRequiredLanguages() {
		boolean hasMissingChild = false;

		for (AuditCategory category : categories) {
			if (category.getParent() == null) {
				hasMissingChild = hasMissingChild || category.hasMissingChildRequiredLanguages();
			}
		}

		return hasMissingChild || getLanguages().isEmpty();
	}

	public static AuditTypeBuilder builder() {
		return new AuditTypeBuilder();
	}

    public void setTopCategories(ArrayList<AuditCategory> topCategories) {
        this.topCategories = topCategories;
    }

    public int getAdvanceDays() {
        return advanceDays;
    }

    public void setAdvanceDays(int advanceDays) {
        this.advanceDays = advanceDays;
    }

    public int getAnchorMonth() {
        return anchorMonth;
    }

    public void setAnchorMonth(int anchorMonth) {
        this.anchorMonth = anchorMonth;
    }

    public int getAnchorDay() {
        return anchorDay;
    }

    public void setAnchorDay(int anchorDay) {
        this.anchorDay = anchorDay;
    }

    @Enumerated(EnumType.STRING)
    public AuditTypePeriod getPeriod() {
        return period;
    }

    public void setPeriod(AuditTypePeriod period) {
        this.period = period;
    }

    public int getMaximumActive() {
        return maximumActive;
    }

    public void setMaximumActive(int maximumActive) {
        this.maximumActive = maximumActive;
    }

    @ManyToOne
    @JoinColumn(name = "parentID", nullable = true)
    public AuditType getParent() {
        return parent;
    }

    public void setParent(AuditType parent) {
        this.parent = parent;
    }

    @Column(name = "rollbackStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    public AuditStatus getRollbackStatus() {
        return rollbackStatus;
    }

    public void setRollbackStatus(AuditStatus rollbackStatus) {
        this.rollbackStatus = rollbackStatus;
    }

    @Override
    public int hashCode() {
        int result = 31 * getId();
        result = 31 * result + (getSlug() != null ? getSlug().hashCode() : 0);
        return result;
    }
}