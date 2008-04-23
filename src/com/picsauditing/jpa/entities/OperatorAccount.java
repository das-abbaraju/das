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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "operators")
@PrimaryKeyJoinColumn(name = "id")
public class OperatorAccount extends Account implements java.io.Serializable {
	public static final String DEFAULT_NAME = "- Operator -";

	private String activationEmails;
	private String doSendActivationEmail;
	private String seesAllContractors;
	private YesNo canAddContractors;
	private String doContractorsPay;
	private YesNo canSeeInsurance;
	private YesNo isCorporate;
	private String emrHurdle;
	private String emrTime;
	private String lwcrTime;
	private String lwcrHurdle;
	private String trirHurdle;
	private String trirTime;
	private String fatalitiesHurdle;
	private YesNo flagEmr;
	private YesNo flagLwcr;
	private YesNo flagTrir;
	private YesNo flagFatalities;
	private YesNo flagQ318;
	private YesNo flagQ1385;
	private User insuranceAuditor;
	private YesNo isUserManualUploaded;
	private YesNo approvesRelationships;

	protected List<AuditQuestionOperatorAccount> auditQuestions = new ArrayList<AuditQuestionOperatorAccount>();
	protected List<FlagQuestionCriteria> flagQuestionCriteria = new ArrayList<FlagQuestionCriteria>();
	protected List<FlagOshaCriteria> flagOshaCriteria = new ArrayList<FlagOshaCriteria>();

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

	@Column(name = "seesAllContractors", nullable = false, length = 10)
	public String getSeesAllContractors() {
		return this.seesAllContractors;
	}

	public void setSeesAllContractors(String seesAllContractors) {
		this.seesAllContractors = seesAllContractors;
	}

	@Column(name = "canAddContractors", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanAddContractors() {
		return this.canAddContractors;
	}

	public void setCanAddContractors(YesNo canAddContractors) {
		this.canAddContractors = canAddContractors;
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

	@Column(name = "isCorporate", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getIsCorporate() {
		return this.isCorporate;
	}

	public void setIsCorporate(YesNo isCorporate) {
		this.isCorporate = isCorporate;
	}

	@Column(name = "emrHurdle", nullable = false, length = 20)
	public String getEmrHurdle() {
		return this.emrHurdle;
	}

	public void setEmrHurdle(String emrHurdle) {
		this.emrHurdle = emrHurdle;
	}

	@Column(name = "emrTime", nullable = false, length = 10)
	public String getEmrTime() {
		return this.emrTime;
	}

	public void setEmrTime(String emrTime) {
		this.emrTime = emrTime;
	}

	@Column(name = "lwcrTime", nullable = false, length = 20)
	public String getLwcrTime() {
		return this.lwcrTime;
	}

	public void setLwcrTime(String lwcrTime) {
		this.lwcrTime = lwcrTime;
	}

	@Column(name = "lwcrHurdle", nullable = false, length = 20)
	public String getLwcrHurdle() {
		return this.lwcrHurdle;
	}

	public void setLwcrHurdle(String lwcrHurdle) {
		this.lwcrHurdle = lwcrHurdle;
	}

	@Column(name = "trirHurdle", nullable = false, length = 20)
	public String getTrirHurdle() {
		return this.trirHurdle;
	}

	public void setTrirHurdle(String trirHurdle) {
		this.trirHurdle = trirHurdle;
	}

	@Column(name = "trirTime", nullable = false, length = 10)
	public String getTrirTime() {
		return this.trirTime;
	}

	public void setTrirTime(String trirTime) {
		this.trirTime = trirTime;
	}

	@Column(name = "fatalitiesHurdle", nullable = false, length = 10)
	public String getFatalitiesHurdle() {
		return this.fatalitiesHurdle;
	}

	public void setFatalitiesHurdle(String fatalitiesHurdle) {
		this.fatalitiesHurdle = fatalitiesHurdle;
	}

	@Column(name = "flagEmr", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagEmr() {
		return this.flagEmr;
	}

	public void setFlagEmr(YesNo flagEmr) {
		this.flagEmr = flagEmr;
	}

	@Column(name = "flagLwcr", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagLwcr() {
		return this.flagLwcr;
	}

	public void setFlagLwcr(YesNo flagLwcr) {
		this.flagLwcr = flagLwcr;
	}

	@Column(name = "flagTrir", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagTrir() {
		return this.flagTrir;
	}

	public void setFlagTrir(YesNo flagTrir) {
		this.flagTrir = flagTrir;
	}

	@Column(name = "flagFatalities", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagFatalities() {
		return this.flagFatalities;
	}

	public void setFlagFatalities(YesNo flagFatalities) {
		this.flagFatalities = flagFatalities;
	}

	@Column(name = "flagQ318", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagQ318() {
		return this.flagQ318;
	}

	public void setFlagQ318(YesNo flagQ318) {
		this.flagQ318 = flagQ318;
	}

	@Column(name = "flagQ1385", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getFlagQ1385() {
		return this.flagQ1385;
	}

	public void setFlagQ1385(YesNo flagQ1385) {
		this.flagQ1385 = flagQ1385;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "insuranceAuditor_id")
	public User getInsuranceAuditor() {
		return this.insuranceAuditor;
	}

	public void setInsuranceAuditor(User user) {
		this.insuranceAuditor = user;
	}

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

	public void setAuditQuestions(
			List<AuditQuestionOperatorAccount> auditQuestions) {
		this.auditQuestions = auditQuestions;
	}

	@OneToMany(mappedBy = "operatorAccount")
	public List<FlagQuestionCriteria> getFlagQuestionCriteria() {
		return flagQuestionCriteria;
	}

	public void setFlagQuestionCriteria(
			List<FlagQuestionCriteria> flagQuestionCriteria) {
		this.flagQuestionCriteria = flagQuestionCriteria;
	}

	@OneToMany(mappedBy = "operatorAccount")
	public List<FlagOshaCriteria> getFlagOshaCriteria() {
		return flagOshaCriteria;
	}

	public void setFlagOshaCriteria(List<FlagOshaCriteria> flagOshaCriteria) {
		this.flagOshaCriteria = flagOshaCriteria;
	}

}
