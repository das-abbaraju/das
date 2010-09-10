package com.picsauditing.jpa.entities;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfdata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditData extends BaseTable implements java.io.Serializable, Comparable<AuditData> {

	private ContractorAudit audit;
	private AuditQuestion question;
	private String answer;
	private String comment;
	private YesNo wasChanged;
	private User auditor;
	private Date dateVerified;

	// Transient properties
	private List<AuditData> siblings;

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

	@Transient
	public String getHtmlDisplay(String value) {
		if (!Strings.isEmpty(value))
			return value.replaceAll("\\n", "<br />").replaceAll("  ", "&nbsp;&nbsp;");
		else
			return null;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	@Transient
	public boolean isUnverified() {
		return getDateVerified() == null;
	}

	@Transient
	public boolean isVerified() {
		return getDateVerified() != null;
	}

	public void setVerified(boolean inValue) {
		this.setDateVerified(inValue ? new java.util.Date() : null);
	}

	@Transient
	/**
	 * Is the answer filled in with data or not?
	 */
	public boolean isAnswered() {
		if (answer != null && answer.length() > 0 && !answer.equals(DateBean.NULL_DATE_DB)) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isOK() {
		if (!question.isHasRequirement())
			return true;

		if (answer == null || question.getOkAnswer() == null)
			return false;

		if (question.getOkAnswer().contains(answer))
			return true;

		return false;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getWasChanged() {
		return wasChanged;
	}

	@Transient
	public boolean isWasChangedB() {
		return YesNo.Yes.equals(wasChanged);
	}

	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}

	@Transient
	public boolean isHasRequirements() {
		if(audit.getAuditType().getWorkFlow().getId() != 2) // audits that have requirements
			return false;
		if (!getQuestion().isHasRequirement())
			return false;
		return (YesNo.Yes.equals(wasChanged) || isRequirementOpen());
	}

	@Transient
	public boolean isRequirementOpen() {
		if (Strings.isEmpty(question.getOkAnswer()))
			return false;
		return (question.getOkAnswer().indexOf(answer) == -1);
	}

	@Transient
	public boolean isCommentLength() {
		if (!Strings.isEmpty(comment))
			return true;
		return false;
	}

/*	@Transient
	public boolean isRequired() {
		String isRequired = question.getIsRequired();
		if (isRequired.equals("Yes"))
			return true;

		if (isRequired.equals("Depends")) {
			if (question.getr == null)
				return false;
			String dependsOnAnswer = question.getDependsOnAnswer();
			if (dependsOnAnswer == null)
				return false;

			// TODO BEFORE RELEASE! figure out some way to get the answer of a
			// dependent question
			// dependsOnQuestion.getAnswer();
			AuditData contractorAnswer = null;

			if (contractorAnswer == null)
				// The contractor hasn't answered this question yet
				return false;
			// Such as "Yes" and "Yes with Office" answers.
			if (dependsOnAnswer.equals("Yes*"))
				return contractorAnswer.getAnswer().startsWith("Yes");

			if (dependsOnAnswer.equals(contractorAnswer.getAnswer()))
				return true;
		}
		return false;
	}*/

	/**
	 * Get a unique ID for this answer regardless if it has been saved or not
	 * 
	 * @return
	 */
	@Transient
	public String getDivId() {
		return "" + getQuestion().getId();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	/**
	 * Take a map of numerical AuditData answers and add an additional AuditData
	 * containing an average. The new average will be verified only if all the
	 * others are verified
	 */
	public static AuditData addAverageData(Collection<AuditData> dataList) {
		if (dataList == null || dataList.size() == 0)
			return null;

		AuditData avg = new AuditData();
		avg.setVerified(true); // Assume it's all verified
		ContractorAudit audit = new ContractorAudit();
		audit.setAuditFor(OshaAudit.AVG);
		avg.setAudit(audit);

		float rateTotal = 0;
		int count = 0;
		for (AuditData data : dataList) {
			if (data != null) {
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
					// String error = "Failed to parse rate:" + data.getAnswer()
					// + " for audit " + data.getAudit().getId();
					// System.out.println(error);
				}
			}
		}
		if (count > 0) {
			Float avgRateFloat = rateTotal / count;
			avgRateFloat = (float) Math.round(1000 * avgRateFloat) / 1000;
			avg.setAnswer(avgRateFloat.toString());
		}
		return avg;
	}

	@Override
	public int compareTo(AuditData other) {
		if (other == null) {
			return 1;
		}

		//int cmp = getQuestion().compareTo(other.getQuestion());

//		if (cmp != 0)
//			return cmp;
//
//		return new Integer(getId()).compareTo(new Integer(other.getId()));
		return 0;
	}

	@Override
	public String toString() {
		return id + " '" + answer + "'";
	}

}
