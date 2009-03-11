package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

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

@SuppressWarnings("serial")
@Entity
@Table(name = "operators")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorAccount extends Account implements java.io.Serializable {
	public static final String DEFAULT_NAME = "- Operator -";

	private OperatorAccount parent;
	
	private boolean inheritFlagCriteria;
	private boolean inheritInsuranceCriteria;
	private boolean inheritAudits;
	private boolean inheritLegalNames;

	private String activationEmails = "";
	private String doSendActivationEmail = "No";
	private String doContractorsPay = "Yes";
	private YesNo canSeeInsurance = YesNo.No;
	private YesNo isCorporate = YesNo.No;
	private User insuranceAuditor;
	private YesNo isUserManualUploaded = YesNo.No;
	private YesNo approvesRelationships = YesNo.No;
	private boolean verifiedByPics = false;

	protected List<AuditQuestionOperatorAccount> auditQuestions = new ArrayList<AuditQuestionOperatorAccount>();
	protected List<FlagQuestionCriteria> flagQuestionCriteria = new ArrayList<FlagQuestionCriteria>();
	protected List<FlagOshaCriteria> flagOshaCriteria = new ArrayList<FlagOshaCriteria>();
	protected List<AuditOperator> audits = new ArrayList<AuditOperator>();
	protected List<Facility> corporateFacilities = new ArrayList<Facility>();
	protected List<Facility> operatorFacilities = new ArrayList<Facility>();
	protected List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
	protected List<OperatorAccount> operatorAccounts = new ArrayList<OperatorAccount>();
	
	public OperatorAccount() {
		this.type = "Operator";
	}

	public OperatorAccount(String name) {
		this.name = name;
		this.type = "Operator";
	}

	@Column(name = "activationEmails", nullable = false)
	public String getActivationEmails() {
		return this.activationEmails;
	}

	public void setActivationEmails(String activationEmails) {
		this.activationEmails = activationEmails;
	}

	@Column(name = "doSendActivationEmail", nullable = false, length = 10)
	public String getDoSendActivationEmail() {
		return this.doSendActivationEmail;
	}

	public void setDoSendActivationEmail(String doSendActivationEmail) {
		this.doSendActivationEmail = doSendActivationEmail;
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

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Column(name = "isCorporate", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getIsCorporate() {
		return this.isCorporate;
	}

	public void setIsCorporate(YesNo isCorporate) {
		this.isCorporate = isCorporate;
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

	@OneToMany(mappedBy = "operatorAccount")
	public List<AuditQuestionOperatorAccount> getAuditQuestions() {
		return auditQuestions;
	}

	public void setAuditQuestions(List<AuditQuestionOperatorAccount> auditQuestions) {
		this.auditQuestions = auditQuestions;
	}

	@OneToMany(mappedBy = "operatorAccount")
	public List<FlagQuestionCriteria> getFlagQuestionCriteria() {
		return flagQuestionCriteria;
	}

	public void setFlagQuestionCriteria(List<FlagQuestionCriteria> flagQuestionCriteria) {
		this.flagQuestionCriteria = flagQuestionCriteria;
	}

	@OneToMany(mappedBy = "operatorAccount")
	public List<FlagOshaCriteria> getFlagOshaCriteria() {
		return flagOshaCriteria;
	}

	public void setFlagOshaCriteria(List<FlagOshaCriteria> flagOshaCriteria) {
		this.flagOshaCriteria = flagOshaCriteria;
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

	/**
	 * Get a list of QuestionIDs that are Verified or Checked as part of a Flag
	 * calculation
	 * 
	 * @return
	 */
	@Transient
	public List<Integer> getQuestionIDs() {
		List<Integer> questionIDs = new ArrayList<Integer>();
		for (AuditQuestionOperatorAccount question : getAuditQuestions()) {
			questionIDs.add(question.getAuditQuestion().getId());
		}
		for (FlagQuestionCriteria criteria : getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes))
				questionIDs.add(criteria.getAuditQuestion().getId());
		}
		return questionIDs;
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
	 * @return a list of all "associated" operator accounts associated via the facilities intersection table
	 * for example, BASF would contain BASF Port Arthur but not BASF Freeport Hub
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
	 * @return a list of all the "direct" child operators/corporates mapped through operator.parentID
	 * for example, BASF would contain BASF Freeport Hub, but not BASF Port Arthur
	 */
	@OneToMany(mappedBy = "parent")
	public List<OperatorAccount> getOperatorAccounts() {
		return operatorAccounts;
	}

	public void setOperatorAccounts(List<OperatorAccount> operatorAccounts) {
		this.operatorAccounts = operatorAccounts;
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
	
	public boolean isInheritFlagCriteria() {
		return inheritFlagCriteria;
	}

	public void setInheritFlagCriteria(boolean inheritFlagCriteria) {
		this.inheritFlagCriteria = inheritFlagCriteria;
	}

	public boolean isInheritInsuranceCriteria() {
		return inheritInsuranceCriteria;
	}

	public void setInheritInsuranceCriteria(boolean inheritInsuranceCriteria) {
		this.inheritInsuranceCriteria = inheritInsuranceCriteria;
	}

	public boolean isInheritAudits() {
		return inheritAudits;
	}

	public void setInheritAudits(boolean inheritAudits) {
		this.inheritAudits = inheritAudits;
	}

	public boolean isInheritLegalNames() {
		return inheritLegalNames;
	}

	public void setInheritLegalNames(boolean inheritLegalNames) {
		this.inheritLegalNames = inheritLegalNames;
	}

	@Transient
	public boolean isHasLegalName(String legalName) {
		if (legalName.equals("All"))
			return true;
		for (AccountName accountName : getNames()) {
			if (accountName.getName().equalsIgnoreCase(legalName)) {
				return true;
			}

		}
		return false;
	}
}
