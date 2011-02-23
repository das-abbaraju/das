package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.google.common.base.Strings;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
@Entity
@Table(name = "operators")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorAccount extends Account {

	public static final int PicsConsortium = 4;

	private OperatorAccount parent;

	private OperatorAccount inheritFlagCriteria;
	private OperatorAccount inheritInsuranceCriteria;

	private String doContractorsPay = "Yes";
	private YesNo canSeeInsurance = YesNo.No;
	private User insuranceAuditor;
	private YesNo isUserManualUploaded = YesNo.No;
	private YesNo approvesRelationships = YesNo.No;
	private boolean verifiedByPics = true;
	private OshaType oshaType = OshaType.OSHA;
	private boolean primaryCorporate = false;
	private boolean autoApproveInsurance = false;
	private Integer activationFee = null;
	private String requiredTags;

	private List<Facility> corporateFacilities = new ArrayList<Facility>();
	private List<Facility> operatorFacilities = new ArrayList<Facility>();
	private List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
	private List<OperatorAccount> operatorChildren = new ArrayList<OperatorAccount>();
	private List<OperatorTag> tags = new ArrayList<OperatorTag>();
	private List<OperatorForm> operatorForms = new ArrayList<OperatorForm>();
	private List<FlagCriteriaOperator> flagCriteria = new ArrayList<FlagCriteriaOperator>();
	private List<JobSite> jobSites = new ArrayList<JobSite>();
	private Set<Integer> visibleAuditTypes = null;

	public OperatorAccount() {
		this.type = "Operator";
	}

	public OperatorAccount(String name) {
		this.name = name;
		this.type = "Operator";
	}
	
	@Transient
	public String getFullName() {
		if (Strings.isNullOrEmpty(dbaName))
			return name;
		return dbaName;
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

	public Integer getActivationFee() {
		return activationFee;
	}

	public void setActivationFee(Integer activationFee) {
		this.activationFee = activationFee;
	}
	
	@Transient
	public OperatorAccount getActivationFeeOperator(InvoiceFee activation) {
		// if Operator activation fee is reduced, then use Operator account activation fee
		if(this.getActivationFee() != null
				&&!this.getActivationFee().equals(activation.getAmount().intValue()))
			return this;

		// if Corporate activation fee is reduced, return Corporate account
		for (Facility f : this.getCorporateFacilities())
			if (f.getCorporate().getActivationFee() != null
					&& !f.getCorporate().getActivationFee().equals(activation.getAmount().intValue()))
				return f.getCorporate();
		
		// If neither activation fee is set, return self and use default
		return this;
	}

	public String getRequiredTags() {
		return requiredTags;
	}

	public void setRequiredTags(String requiredTags) {
		this.requiredTags = requiredTags;
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

		for (FlagCriteriaOperator c : inheritFlagCriteria.getFlagCriteria()) {
			if (c.getCriteria().getAuditType() != null) {
				if (!c.getCriteria().getAuditType().getClassType().isPolicy() || canSeeInsurance.equals(YesNo.Yes)) {
					criteriaList.add(c);
				}
			}
		}

		return criteriaList;
	}

	@Transient
	public List<FlagCriteriaOperator> getFlagQuestionCriteriaInherited() {
		List<FlagCriteriaOperator> criteriaList = new ArrayList<FlagCriteriaOperator>();

		for (FlagCriteriaOperator c : inheritFlagCriteria.getFlagCriteria()) {
			if (c.getCriteria().getQuestion() != null) {
				if (c.getCriteria().getQuestion().isCurrent()) {
					if (!c.getCriteria().getQuestion().getAuditType().getClassType().isPolicy()
							|| canSeeInsurance.equals(YesNo.Yes))
						criteriaList.add(c);
				}
			}
			if (c.getCriteria().getOshaType() != null) {
				if (c.getCriteria().getOshaType().equals(oshaType)) {
					criteriaList.add(c);
				}
			}
		}

		return criteriaList;
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

	@OneToMany(mappedBy = "operator", fetch = FetchType.EAGER)
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
	@Deprecated
	public List<OperatorAccount> getOperatorChildren() {
		return operatorChildren;
	}

	public void setOperatorChildren(List<OperatorAccount> operatorChildren) {
		this.operatorChildren = operatorChildren;
	}

	@Deprecated
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

	@OneToMany(mappedBy = "operator", cascade = { CascadeType.REMOVE })
	public List<JobSite> getJobSites() {
		return jobSites;
	}

	public void setJobSites(List<JobSite> jobSites) {
		this.jobSites = jobSites;
	}

	@Transient
	public OperatorAccount getTopAccount() {
		OperatorAccount topAccount = this;
		if (this.getParent() != null)
			topAccount = this.getParent();

		for (Facility facility : getCorporateFacilities()) {
			if (facility.getCorporate().isPrimaryCorporate()) {
				topAccount = facility.getCorporate();
				break;
			}
		}
		return topAccount;
	}

	@Transient
	public boolean isInsureguardSubscriber() {
		return canSeeInsurance.equals(YesNo.Yes);
	}

	@Transient
	public List<Integer> getOperatorHeirarchy() {
		List<Integer> list = new ArrayList<Integer>();
		// Add myself
		list.add(this.id);

		OperatorAccount topAccount = getTopAccount();
		for (Facility facility : getCorporateFacilities()) {
			if (!facility.getCorporate().equals(topAccount))
				// Add parent's that aren't my primary parent
				list.add(facility.getCorporate().getId());
		}
		if (!topAccount.equals(this))
			// Add my parent
			list.add(topAccount.getId());
		return list;
	}

	/**
	 * Please use sparingly!! This does a call to a Spring loaded DAO
	 * 
	 * @return Set of AuditTypeIDs
	 */
	@Transient
	public Set<Integer> getVisibleAuditTypes() {
		if (visibleAuditTypes == null) {
			// This isn't pretty but it works
			AuditDecisionTableDAO dao = (AuditDecisionTableDAO) SpringUtils.getBean("AuditDecisionTableDAO");
			visibleAuditTypes = dao.getAuditTypes(this);
		}
		return visibleAuditTypes;
	}
	
	@Transient
	public boolean isInPicsConsortium() {
		return this.getId() > 3 && this.getId() < 14;
	}
	
	@Override
	@Transient
	public String getAutocompleteValue() {
		return "(" + this.getId() + ") " + this.getName();
	}
}
