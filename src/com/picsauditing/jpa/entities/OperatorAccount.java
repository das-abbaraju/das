package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
@Entity
@Table(name = "operators")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorAccount extends Account {
	public static final String DEFAULT_NAME = "- Operator -";

	private OperatorAccount parent;

	private OperatorAccount inheritFlagCriteria;
	private OperatorAccount inheritInsuranceCriteria;
	private OperatorAccount inheritInsurance;
	private OperatorAccount inheritAudits;
	private OperatorAccount inheritAuditCategories;

	private String doContractorsPay = "Yes";
	private YesNo canSeeInsurance = YesNo.No;
	private User insuranceAuditor;
	private YesNo isUserManualUploaded = YesNo.No;
	private YesNo approvesRelationships = YesNo.No;
	private boolean verifiedByPics = true;
	private OshaType oshaType = OshaType.OSHA;
	private boolean primaryCorporate = false;
	private boolean autoApproveInsurance = false;

	private List<AuditQuestionOperatorAccount> auditQuestions = new ArrayList<AuditQuestionOperatorAccount>();
	private List<Facility> corporateFacilities = new ArrayList<Facility>();
	private List<Facility> operatorFacilities = new ArrayList<Facility>();
	private List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
	private List<OperatorAccount> operatorChildren = new ArrayList<OperatorAccount>();
	private List<OperatorTag> tags = new ArrayList<OperatorTag>();
	private List<AuditOperator> audits = new ArrayList<AuditOperator>();
	private List<OperatorForm> operatorForms = new ArrayList<OperatorForm>();
	private Map<Integer, AuditOperator> auditMap = null;
	private List<FlagCriteriaOperator> flagCriteria = new ArrayList<FlagCriteriaOperator>();

	public OperatorAccount() {
		this.type = "Operator";
	}

	public OperatorAccount(String name) {
		this.name = name;
		this.type = "Operator";
	}

	/**
	 * Yes, No, Multiple
	 */
	@Column(nullable = false, length = 8)
	public String getDoContractorsPay() {
		return this.doContractorsPay;
	}

	public void setDoContractorsPay(String doContractorsPay) {
		this.doContractorsPay = doContractorsPay;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeeInsurance() {
		return this.canSeeInsurance;
	}

	public void setCanSeeInsurance(YesNo canSeeInsurance) {
		this.canSeeInsurance = canSeeInsurance;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insuranceAuditor_id")
	public User getInsuranceAuditor() {
		return this.insuranceAuditor;
	}

	public void setInsuranceAuditor(User user) {
		this.insuranceAuditor = user;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Column(name = "isUserManualUploaded", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getIsUserManualUploaded() {
		return this.isUserManualUploaded;
	}

	public void setIsUserManualUploaded(YesNo isUserManualUploaded) {
		this.isUserManualUploaded = isUserManualUploaded;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getApprovesRelationships() {
		return approvesRelationships;
	}

	public void setApprovesRelationships(YesNo approvesRelationships) {
		this.approvesRelationships = approvesRelationships;
	}

	@Column(name = "verifiedByPics", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	public boolean isVerifiedByPics() {
		return verifiedByPics;
	}

	public void setVerifiedByPics(boolean verifiedByPics) {
		this.verifiedByPics = verifiedByPics;
	}

	@Enumerated(EnumType.STRING)
	public OshaType getOshaType() {
		return oshaType;
	}

	public void setOshaType(OshaType oshaType) {
		this.oshaType = oshaType;
	}

	public boolean isPrimaryCorporate() {
		return primaryCorporate;
	}

	public void setPrimaryCorporate(boolean primaryCorporate) {
		this.primaryCorporate = primaryCorporate;
	}

	public boolean isAutoApproveInsurance() {
		return autoApproveInsurance;
	}

	public void setAutoApproveInsurance(boolean autoApproveInsurance) {
		this.autoApproveInsurance = autoApproveInsurance;
	}

	@ManyToOne
	@JoinColumn(name = "inheritFlagCriteria")
	public OperatorAccount getInheritFlagCriteria() {
		return inheritFlagCriteria;
	}

	public void setInheritFlagCriteria(OperatorAccount inheritFlagCriteria) {
		this.inheritFlagCriteria = inheritFlagCriteria;
	}

	@ManyToOne
	@JoinColumn(name = "inheritInsuranceCriteria")
	public OperatorAccount getInheritInsuranceCriteria() {
		return inheritInsuranceCriteria;
	}

	@ManyToOne
	@JoinColumn(name = "inheritInsuranceCriteria")
	public void setInheritInsuranceCriteria(OperatorAccount inheritInsuranceCriteria) {
		this.inheritInsuranceCriteria = inheritInsuranceCriteria;
	}

	@ManyToOne
	@JoinColumn(name = "inheritInsurance")
	public OperatorAccount getInheritInsurance() {
		return inheritInsurance;
	}

	public void setInheritInsurance(OperatorAccount inheritInsurance) {
		this.inheritInsurance = inheritInsurance;
	}

	@ManyToOne
	@JoinColumn(name = "inheritAudits")
	public OperatorAccount getInheritAudits() {
		return inheritAudits;
	}

	public void setInheritAudits(OperatorAccount inheritAudits) {
		this.inheritAudits = inheritAudits;
	}

	@ManyToOne
	@JoinColumn(name = "inheritAuditCategories")
	public OperatorAccount getInheritAuditCategories() {
		return inheritAuditCategories;
	}

	public void setInheritAuditCategories(OperatorAccount inheritAuditCategories) {
		this.inheritAuditCategories = inheritAuditCategories;
	}

	@OneToMany(mappedBy = "operatorAccount")
	public List<AuditQuestionOperatorAccount> getAuditQuestions() {
		return auditQuestions;
	}

	public void setAuditQuestions(List<AuditQuestionOperatorAccount> auditQuestions) {
		this.auditQuestions = auditQuestions;
	}

	@Transient
	public List<FlagCriteriaOperator> getFlagCriteriaInherited() {
		List<FlagCriteriaOperator> criteriaList = new ArrayList<FlagCriteriaOperator>();

		criteriaList.addAll(getFlagAuditCriteriaInherited());
		criteriaList.addAll(getFlagQuestionCriteriaInherited());

		return criteriaList;
	}

	@Transient
	public List<FlagCriteriaOperator> getFlagAuditCriteriaInherited() {
		List<FlagCriteriaOperator> criteriaList = new ArrayList<FlagCriteriaOperator>();

		for (FlagCriteriaOperator c : getInheritAudits().getFlagCriteria()) {
			if (c.getCriteria().getAuditType() != null) {
				if (!c.getCriteria().getAuditType().getClassType().isPolicy()) {
					criteriaList.add(c);
				}
			}
		}

		if (canSeeInsurance.equals(YesNo.Yes)) {
			for (FlagCriteriaOperator c : getInheritInsurance().getFlagCriteria()) {
				if (c.getCriteria().getAuditType() != null) {
					if (c.getCriteria().getAuditType().getClassType().isPolicy()) {
						criteriaList.add(c);
					}
				}
			}
		}

		return criteriaList;
	}

	@Transient
	public List<FlagCriteriaOperator> getFlagQuestionCriteriaInherited() {
		List<FlagCriteriaOperator> criteriaList = new ArrayList<FlagCriteriaOperator>();

		if (canSeeInsurance.equals(YesNo.Yes)) {
			for (FlagCriteriaOperator c : getInheritInsuranceCriteria().getFlagCriteria()) {
				if (c.getCriteria().getQuestion() != null) {
					if (c.getCriteria().getQuestion().getAuditType().getClassType().isPolicy()
							&& c.getCriteria().getQuestion().isVisible())
						criteriaList.add(c);
				}
			}
		}

		for (FlagCriteriaOperator c : getInheritFlagCriteria().getFlagCriteria()) {
			if (c.getCriteria().getQuestion() != null) {
				if (!c.getCriteria().getQuestion().getAuditType().getClassType().isPolicy()
						&& c.getCriteria().getQuestion().isVisible())
					criteriaList.add(c);
			}
			if (c.getCriteria().getOshaType() != null) {
				if (c.getCriteria().getOshaType().equals(oshaType)) {
					criteriaList.add(c);
				}
			}
		}

		return criteriaList;
	}

	// TODO: get these to cache too
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
	@OneToMany(mappedBy = "operatorAccount")
	public List<AuditOperator> getAudits() {
		return audits;
	}

	public void setAudits(List<AuditOperator> audits) {
		this.audits = audits;
	}

	@OneToMany(mappedBy = "account")
	public List<OperatorForm> getOperatorForms() {
		return operatorForms;
	}

	public void setOperatorForms(List<OperatorForm> operatorForms) {
		this.operatorForms = operatorForms;
	}

	@Transient
	public List<OperatorForm> getInsuranceForms() {
		ArrayList<OperatorForm> forms = new ArrayList<OperatorForm>();

		for (OperatorForm f : operatorForms) {
			if ("Insurance".equals(f.getFormType()))
				forms.add(f);
		}

		return forms;
	}

	@Transient
	public List<AuditOperator> getVisibleAudits() {
		List<AuditOperator> requiredAudits = new ArrayList<AuditOperator>();
		PicsLogger.log("getting visible pqf/audits from " + inheritAudits.getName());
		for (AuditOperator ao : inheritAudits.getAudits())
			if (ao.isCanSee() && !ao.getAuditType().getClassType().isPolicy())
				requiredAudits.add(ao);
		if (canSeeInsurance.isTrue()) {
			PicsLogger.log("getting visible policies from " + inheritInsurance.getName());
			for (AuditOperator ao : inheritInsurance.getAudits())
				if (ao.isCanSee() && ao.getAuditType().getClassType().isPolicy())
					requiredAudits.add(ao);
		}
		return requiredAudits;
	}

	@Transient
	public Map<Integer, AuditOperator> getAuditMap() {
		if (auditMap == null) {
			auditMap = new HashMap<Integer, AuditOperator>();
			for (AuditOperator auditOperator : getAudits()) {
				auditMap.put(auditOperator.getAuditType().getId(), auditOperator);
			}
		}
		return auditMap;
	}

	@OneToMany(mappedBy = "operator")
	public List<Facility> getCorporateFacilities() {
		return corporateFacilities;
	}

	public void setCorporateFacilities(List<Facility> corporateFacilities) {
		this.corporateFacilities = corporateFacilities;
	}

	/**
	 * @see getOperatorAccounts()
	 * @return a list of all "associated" operator accounts associated via the
	 *         facilities intersection table for example, BASF would contain
	 *         BASF Port Arthur but not BASF Freeport Hub
	 */
	@OneToMany(mappedBy = "corporate")
	public List<Facility> getOperatorFacilities() {
		return operatorFacilities;
	}

	public void setOperatorFacilities(List<Facility> operatorFacilities) {
		this.operatorFacilities = operatorFacilities;
	}

	/**
	 * @see getOperatorFacilities()
	 * @return a list of all the "direct" child operators/corporates mapped
	 *         through operator.parentID for example, BASF would contain BASF
	 *         Freeport Hub, but not BASF Port Arthur
	 */
	@OneToMany(mappedBy = "parent")
	public List<OperatorAccount> getOperatorChildren() {
		return operatorChildren;
	}

	public void setOperatorChildren(List<OperatorAccount> operatorChildren) {
		this.operatorChildren = operatorChildren;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentID", nullable = true)
	public OperatorAccount getParent() {
		return parent;
	}

	public void setParent(OperatorAccount parent) {
		this.parent = parent;
	}

	public boolean isDescendantOf(int id) {
		if (getParent() == null)
			// No parent exists
			return false;
		if (getParent().getId() == id)
			// Yes, the parent matches
			return true;
		// Maybe the grandparent is a descendant of id
		return getParent().isDescendantOf(id);
	}

	/**
	 * @return a list of contractors linked to this operator
	 */
	@OneToMany(mappedBy = "operatorAccount")
	public List<ContractorOperator> getContractorOperators() {
		return contractorOperators;
	}

	public void setContractorOperators(List<ContractorOperator> contractorOperators) {
		this.contractorOperators = contractorOperators;
	}

	@OneToMany(mappedBy = "operator")
	public List<OperatorTag> getTags() {
		return tags;
	}

	public void setTags(List<OperatorTag> value) {
		this.tags = value;
	}

	@OneToMany(mappedBy = "operator", cascade = { CascadeType.ALL })
	public List<FlagCriteriaOperator> getFlagCriteria() {
		return flagCriteria;
	}

	public void setFlagCriteria(List<FlagCriteriaOperator> flagCriteria) {
		this.flagCriteria = flagCriteria;
	}

}
