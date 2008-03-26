package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "operators")
public class Operator implements java.io.Serializable {
	private int id;
	private String activationEmails;
	private String doSendActivationEmail;
	private String seesAllContractors;
	private String canAddContractors;
	private String doContractorsPay;
	private String canSeeInsurance;
	private String isCorporate;
	private String emrHurdle;
	private String emrTime;
	private String lwcrTime;
	private String lwcrHurdle;
	private String trirHurdle;
	private String trirTime;
	private String fatalitiesHurdle;
	private String flagEmr;
	private String flagLwcr;
	private String flagTrir;
	private String flagFatalities;
	private String flagQ318;
	private String flagQ1385;
	private int insuranceAuditorId;
	private String isUserManualUploaded;

	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Column(name = "canAddContractors", nullable = false, length = 3)
	public String getCanAddContractors() {
		return this.canAddContractors;
	}

	public void setCanAddContractors(String canAddContractors) {
		this.canAddContractors = canAddContractors;
	}

	@Column(name = "doContractorsPay", nullable = false, length = 8)
	public String getDoContractorsPay() {
		return this.doContractorsPay;
	}

	public void setDoContractorsPay(String doContractorsPay) {
		this.doContractorsPay = doContractorsPay;
	}

	@Column(name = "canSeeInsurance", nullable = false, length = 3)
	public String getCanSeeInsurance() {
		return this.canSeeInsurance;
	}

	public void setCanSeeInsurance(String canSeeInsurance) {
		this.canSeeInsurance = canSeeInsurance;
	}

	@Column(name = "isCorporate", nullable = false, length = 3)
	public String getIsCorporate() {
		return this.isCorporate;
	}

	public void setIsCorporate(String isCorporate) {
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

	@Column(name = "flagEmr", nullable = false, length = 3)
	public String getFlagEmr() {
		return this.flagEmr;
	}

	public void setFlagEmr(String flagEmr) {
		this.flagEmr = flagEmr;
	}

	@Column(name = "flagLwcr", nullable = false, length = 3)
	public String getFlagLwcr() {
		return this.flagLwcr;
	}

	public void setFlagLwcr(String flagLwcr) {
		this.flagLwcr = flagLwcr;
	}

	@Column(name = "flagTrir", nullable = false, length = 3)
	public String getFlagTrir() {
		return this.flagTrir;
	}

	public void setFlagTrir(String flagTrir) {
		this.flagTrir = flagTrir;
	}

	@Column(name = "flagFatalities", nullable = false, length = 3)
	public String getFlagFatalities() {
		return this.flagFatalities;
	}

	public void setFlagFatalities(String flagFatalities) {
		this.flagFatalities = flagFatalities;
	}

	@Column(name = "flagQ318", nullable = false, length = 3)
	public String getFlagQ318() {
		return this.flagQ318;
	}

	public void setFlagQ318(String flagQ318) {
		this.flagQ318 = flagQ318;
	}

	@Column(name = "flagQ1385", nullable = false, length = 3)
	public String getFlagQ1385() {
		return this.flagQ1385;
	}

	public void setFlagQ1385(String flagQ1385) {
		this.flagQ1385 = flagQ1385;
	}

	@Column(name = "insuranceAuditor_id", nullable = false)
	public int getInsuranceAuditorId() {
		return this.insuranceAuditorId;
	}

	public void setInsuranceAuditorId(int insuranceAuditorId) {
		this.insuranceAuditorId = insuranceAuditorId;
	}

	@Column(name = "isUserManualUploaded", nullable = false, length = 3)
	public String getIsUserManualUploaded() {
		return this.isUserManualUploaded;
	}

	public void setIsUserManualUploaded(String isUserManualUploaded) {
		this.isUserManualUploaded = isUserManualUploaded;
	}

}
