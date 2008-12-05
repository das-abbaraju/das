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
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class OperatorAccount extends Account implements java.io.Serializable {
	public static final String DEFAULT_NAME = "- Operator -";

	private String activationEmails;
	private String doSendActivationEmail;
	private String doContractorsPay;
	private YesNo canSeeInsurance;
	private YesNo isCorporate;
	private User insuranceAuditor;
	private YesNo isUserManualUploaded;
	private YesNo approvesRelationships;

	protected List<AuditQuestionOperatorAccount> auditQuestions = new ArrayList<AuditQuestionOperatorAccount>();
	protected List<FlagQuestionCriteria> flagQuestionCriteria = new ArrayList<FlagQuestionCriteria>();
	protected List<FlagOshaCriteria> flagOshaCriteria = new ArrayList<FlagOshaCriteria>();
	protected List<AuditOperator> audits = new ArrayList<AuditOperator>();
	protected List<Facility> corporateFacilities = new ArrayList<Facility>();
	protected List<Facility> operatorFacilities = new ArrayList<Facility>();

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

	@Column(name = "doContractorsPay", nullable = false, length = 8)
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

	@OneToMany(mappedBy = "corporate")
	public List<Facility> getOperatorFacilities() {
		return operatorFacilities;
	}

	public void setOperatorFacilities(List<Facility> operatorFacilities) {
		this.operatorFacilities = operatorFacilities;
	}
}
