package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
	private int insuranceAuditorId;
	private YesNo isUserManualUploaded;
	private YesNo canSeePQF;
	private YesNo canSeeDesktop;
	private YesNo canSeeDA;
	private YesNo canSeeoffice;
	private YesNo canSeeField;
	private YesNo approvesRelationships;
	protected List<ContractorOperator> contractors;

	public OperatorAccount() {
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

	@Column(name = "canSeeInsurance", nullable = false)
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

	@Column(name = "insuranceAuditor_id", nullable = false)
	public int getInsuranceAuditorId() {
		return this.insuranceAuditorId;
	}

	public void setInsuranceAuditorId(int insuranceAuditorId) {
		this.insuranceAuditorId = insuranceAuditorId;
	}

	@Column(name = "isUserManualUploaded", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getIsUserManualUploaded() {
		return this.isUserManualUploaded;
	}

	public void setIsUserManualUploaded(YesNo isUserManualUploaded) {
		this.isUserManualUploaded = isUserManualUploaded;
	}

	@Column(name = "canSeePQF", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeePQF() {
		return canSeePQF;
	}

	public void setCanSeePQF(YesNo canSeePQF) {
		this.canSeePQF = canSeePQF;
	}

	@Column(name = "canSeeDesktop", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeeDesktop() {
		return canSeeDesktop;
	}

	public void setCanSeeDesktop(YesNo canSeeDesktop) {
		this.canSeeDesktop = canSeeDesktop;
	}

	@Column(name = "canSeeDA", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeeDA() {
		return canSeeDA;
	}

	public void setCanSeeDA(YesNo canSeeDA) {
		this.canSeeDA = canSeeDA;
	}

	@Column(name = "canSeeOffice", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeeoffice() {
		return canSeeoffice;
	}

	public void setCanSeeoffice(YesNo canSeeoffice) {
		this.canSeeoffice = canSeeoffice;
	}

	@Column(name = "canSeeField", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getCanSeeField() {
		return canSeeField;
	}

	public void setCanSeeField(YesNo canSeeField) {
		this.canSeeField = canSeeField;
	}

	@Column(name = "approvesRelationships", nullable = false)
	@Enumerated(EnumType.STRING)
	public YesNo getApprovesRelationships() {
		return approvesRelationships;
	}

	public void setApprovesRelationships(YesNo approvesRelationships) {
		this.approvesRelationships = approvesRelationships;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "operatorAccount")
	public List<ContractorOperator> getContractors() {
		return this.contractors;
	}

	public void setContractors(List<ContractorOperator> contractors) {
		this.contractors = contractors;
	}

}
