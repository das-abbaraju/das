package com.picsauditing.jpa.entities;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfdata")
public class AuditData extends BaseTable implements java.io.Serializable, Comparable<AuditData> {

	private ContractorAudit audit;
	private AuditQuestion question;
	private String answer;
	private String comment;
	private YesNo wasChanged;
	private User auditor;
	private Date dateVerified;

	private List<AuditDataHistory> dataHistory = new ArrayList<AuditDataHistory>();

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

	@ReportField(filterType = FilterType.String)
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

	@ReportField(filterType = FilterType.String)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(mappedBy = "currentAuditData", cascade = { CascadeType.ALL })
	public List<AuditDataHistory> getDataHistory() {
		return dataHistory;
	}

	public void setDataHistory(List<AuditDataHistory> dataHistory) {
		this.dataHistory = dataHistory;
	}

	@Transient
	public String getHtmlDisplay(String value) {
		return Utilities.escapeHTML(value);
	}

	@Temporal(TemporalType.DATE)
	@ReportField(filterType = FilterType.Date)
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
		// Audits that have requirements
		if (!audit.getAuditType().getWorkFlow().isHasRequirements())
			return false;
		if (!getQuestion().isHasRequirement())
			return false;
		return (YesNo.Yes.equals(wasChanged) || isRequirementOpen());
	}

	@Transient
	public boolean isRequirementOpen() {
		if (Strings.isEmpty(question.getOkAnswer()))
			return false;
		if (Strings.isEmpty(answer))
			return false;
		return (question.getOkAnswer().indexOf(answer) == -1);
	}

	@Transient
	public boolean isCommentLength() {
		if (!Strings.isEmpty(comment))
			return true;
		return false;
	}

	/*
	 * @Transient public boolean isRequired() { String isRequired =
	 * question.getIsRequired(); if (isRequired.equals("Yes")) return true;
	 * 
	 * if (isRequired.equals("Depends")) { if (question.getr == null) return
	 * false; String dependsOnAnswer = question.getDependsOnAnswer(); if
	 * (dependsOnAnswer == null) return false;
	 * 
	 * // TODO BEFORE RELEASE! figure out some way to get the answer of a //
	 * dependent question // dependsOnQuestion.getAnswer(); AuditData
	 * contractorAnswer = null;
	 * 
	 * if (contractorAnswer == null) // The contractor hasn't answered this
	 * question yet return false; // Such as "Yes" and "Yes with Office"
	 * answers. if (dependsOnAnswer.equals("Yes*")) return
	 * contractorAnswer.getAnswer().startsWith("Yes");
	 * 
	 * if (dependsOnAnswer.equals(contractorAnswer.getAnswer())) return true; }
	 * return false; }
	 */

	/**
	 * Get a unique ID for this answer regardless if it has been saved or not
	 * 
	 * @return
	 */
	@Transient
	public String getDivId() {
		return "" + getQuestion().getId();
	}

	@Transient
	public boolean isMultipleChoice() {
		return question != null && question.getQuestionType().equals("MultipleChoice") && question.getOption() != null;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	public int compareTo(AuditData other) {
		if (other == null) {
			return 1;
		}
		return 0;
	}

	@Transient
	public float getScorePercentage() {
		float scorePercentage = 0f;
		if (answer != null && isMultipleChoice()) {
			for (AuditOptionValue value : question.getOption().getValues()) {
				if (answer.equals(value.getIdentifier())) {
					scorePercentage = value.getScorePercent();
					break;
				}
			}
		}
		return scorePercentage;
	}

	@Transient
	public float getScoreValue() {
		return Math.round(getScorePercentage() * question.getScoreWeight());
	}

	@Transient
	public boolean isScoreApplies() {
		return getScorePercentage() >= 0;
	}

	@Transient
	public List<String> getTaggitList() {
		List<String> listOfOptionValueIl8nKeys = new ArrayList<String>();

		JSONArray itemsSelected = (JSONArray) JSONValue.parse(this.getAnswer());
		List<AuditOptionValue> optionValues = this.getQuestion().getOption().getValues();
		for (Object answer : itemsSelected.toArray()) {
			String uniqueCode = ((JSONObject) answer).get("id").toString();
			for (AuditOptionValue optionValue : optionValues) {
				if (uniqueCode.equals(optionValue.getUniqueCode())) {
					listOfOptionValueIl8nKeys.add(optionValue.getI18nKey());
					break;
				}
			}
		}
		return listOfOptionValueIl8nKeys;
	}

	@Override
	public String toString() {
		return id + " '" + answer + "'";
	}

	/**
	 * Comparator for comparing AuditData based on Audit Questions.
	 * 
	 * @return Comparator for comparing AuditData based on AuditQuestion
	 */
	public static Comparator<AuditData> getQuestionComparator() {
		return new Comparator<AuditData>() {
			public int compare(AuditData o1, AuditData o2) {
				String[] o1a = o1.getQuestion().getExpandedNumber().split("\\.");
				String[] o2a = o2.getQuestion().getExpandedNumber().split("\\.");
				for (int i = 0; i < o1a.length && i < o2a.length; i++) {
					if (o1a[i].equals(o2a[i]))
						continue;
					else
						return Integer.valueOf(o1a[i]).compareTo(Integer.valueOf(o2a[i]));
				}
				return 0;
			}
		};
	}

	@Transient
	public String getAnswerInDate(String format) throws ParseException {
		SimpleDateFormat displayFormat = new SimpleDateFormat(format);
		Date date = DateBean.parseDate(answer);
		String dateStr = displayFormat.format(date);
		return dateStr;
	}

	@Transient
	public String getNumberFormatAnswer(Locale locale) {
		NumberFormat displayFormat;
		NumberFormat dbFormat;
		ParsePosition pp = new ParsePosition(0);
		if ("Number".equals(question.getQuestionType())) {
			displayFormat = NumberFormat.getIntegerInstance(locale);
			dbFormat = NumberFormat.getIntegerInstance(Locale.US);
		} else {
			displayFormat = NumberFormat.getNumberInstance(locale);
			dbFormat = NumberFormat.getNumberInstance(Locale.US);
		}
		Number truthValue = dbFormat.parse(answer, pp);
		// check for invalid number
		if (answer.length() != pp.getIndex() || truthValue == null) {
			return answer;
		}
		String displayValue = displayFormat.format(truthValue);
		return displayValue;
	}

}
