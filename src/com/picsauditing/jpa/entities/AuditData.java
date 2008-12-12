package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfdata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditData implements java.io.Serializable {

	private int dataID;
	private ContractorAudit audit;
	private AuditQuestion question;
	private int num;
	private String answer;
	private String comment;
	private YesNo wasChanged;
	private User auditor;
	private Date dateVerified;
	private User createdBy;
	private Date creationDate;
	private User updatedBy;
	private Date updateDate;

	private FlagColor flagColor;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "dataID", nullable = false, insertable = false, updatable = false)
	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	@Transient
	public boolean isVerified() {
		return getDateVerified() != null;
	}

	public void setVerified(boolean inValue) {
		this.setDateVerified(inValue ? new java.util.Date() : null);
	}

	@Transient
	public boolean isUnverified() {
		return getDateVerified() == null;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getWasChanged() {
		return wasChanged;
	}

	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy")
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedBy")
	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Transient
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	@Transient
	public boolean isHasRequirements() {
		// This may not be the best solution. We may want to make pqf not have
		// open requirements.
		if (audit.getAuditType().isPqf())
			return false;
		if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM)
			return false;
		return (YesNo.Yes.equals(wasChanged) || isRequirementOpen());
	}

	@Transient
	public boolean isRequirementOpen() {
		return (question.getOkAnswer().indexOf(answer) == -1);
	}

	@Transient
	public boolean isCommentLength() {
		if (!isVerified() && comment != null && comment.length() > 0)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + dataID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditData other = (AuditData) obj;
		if (dataID != other.dataID)
			return false;
		return true;
	}

	/**
	 * Take a map of numerical AuditData answers and add an additional AuditData
	 * containing an average. The new average will be verified only if all the
	 * others are verified
	 */
	public static void addAverageData(Map<String, AuditData> dataMap) {
		if (dataMap == null || dataMap.size() == 0)
			return;

		if (dataMap.get(OshaAudit.AVG) != null)
			// We already have the average
			return;
		
		AuditData avg = new AuditData();
		AuditQuestion avgQuestion = new AuditQuestion();
		avg.setVerified(true); // Assume it's all verified
		ContractorAudit audit = new ContractorAudit();
		audit.setAuditFor(OshaAudit.AVG);
		avg.setAudit(audit);

		float rateTotal = 0;
		int count = 0;
		for (AuditData data : dataMap.values()) {
			avg.setQuestion(data.getQuestion());
			avg.getAudit().setContractorAccount(data.getAudit().getContractorAccount());
			avg.getAudit().setAuditType(data.getAudit().getAuditType());
			
			if (data.isUnverified())
				avg.setVerified(false);

			try {
				float rate = Float.parseFloat(data.getAnswer());
				rateTotal += rate;
				count++;
			} catch (Exception e) {
				String error = "Failed to parse rate:" + data.getAnswer() + " for audit " + data.getAudit().getId();
				System.out.println(error);
			}
		}
		if (count > 0) {
			Float avgRateFloat = rateTotal / count;
			avgRateFloat = (float)Math.round(1000 * avgRateFloat) / 1000;
			avg.setAnswer(avgRateFloat.toString());
		}
		dataMap.put(OshaAudit.AVG, avg);
	}

}
