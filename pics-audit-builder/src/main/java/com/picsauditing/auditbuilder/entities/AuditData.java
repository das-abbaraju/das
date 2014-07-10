package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfdata")
public class AuditData extends BaseTable implements java.io.Serializable/*, Comparable<AuditData>*/ {

    private ContractorAudit audit;
	private AuditQuestion question;
	private String answer;
//	private String comment;
//	private YesNo wasChanged;
//	private User auditor;
	private Date dateVerified;
//
//	private List<AuditDataHistory> dataHistory = new ArrayList<AuditDataHistory>();
//
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

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "auditorID")
//	public User getAuditor() {
//		return auditor;
//	}
//
//	public void setAuditor(User auditor) {
//		this.auditor = auditor;
//	}
//
//	@ReportField(type = FieldType.String, importance = FieldImportance.Average)
//	public String getComment() {
//		return comment;
//	}
//
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
//
//	@OneToMany(mappedBy = "currentAuditData", cascade = { CascadeType.ALL })
//	public List<AuditDataHistory> getDataHistory() {
//		return dataHistory;
//	}
//
//	public void setDataHistory(List<AuditDataHistory> dataHistory) {
//		this.dataHistory = dataHistory;
//	}
//
//	@Transient
//	public String getHtmlDisplay(String value) {
//		return Utilities.escapeHTML(value);
//	}
//
	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

//	public void setVerified(boolean inValue) {
//		this.setDateVerified(inValue ? new Date() : null);
//	}
//
//	@Enumerated(EnumType.STRING)
//	public YesNo getWasChanged() {
//		return wasChanged;
//	}
//
//	@Transient
//	public boolean isWasChangedB() {
//		return YesNo.Yes.equals(wasChanged);
//	}
//
//	public void setWasChanged(YesNo wasChanged) {
//		this.wasChanged = wasChanged;
//	}
//
//	@Transient
//	public boolean isHasRequirements() {
//		// Audits that have requirements
//		if (!audit.getAuditType().getWorkFlow().isHasRequirements())
//			return false;
//		if (!getQuestion().isHasRequirement())
//			return false;
//		return (YesNo.Yes.equals(wasChanged) || isRequirementOpen());
//	}
//
//	@Transient
//	public boolean isRequirementOpen() {
//		if (Strings.isEmpty(question.getOkAnswer()))
//			return false;
//		if (Strings.isEmpty(answer))
//			return false;
//		return (question.getOkAnswer().indexOf(answer) == -1);
//	}
//
//	@Transient
//	public boolean isCommentLength() {
//		if (!Strings.isEmpty(comment))
//			return true;
//		return false;
//	}
//
//	/*
//	 * @Transient public boolean isRequired() { String isRequired =
//	 * question.getIsRequired(); if (isRequired.equals("Yes")) return true;
//	 *
//	 * if (isRequired.equals("Depends")) { if (question.getr == null) return
//	 * false; String dependsOnAnswer = question.getDependsOnAnswer(); if
//	 * (dependsOnAnswer == null) return false;
//	 *
//	 * // TODO BEFORE RELEASE! figure out some way to get the answer of a //
//	 * dependent question // dependsOnQuestion.getAnswer(); AuditData
//	 * contractorAnswer = null;
//	 *
//	 * if (contractorAnswer == null) // The contractor hasn't answered this
//	 * question yet return false; // Such as "Yes" and "Yes with Office"
//	 * answers. if (dependsOnAnswer.equals("Yes*")) return
//	 * contractorAnswer.getAnswer().startsWith("Yes");
//	 *
//	 * if (dependsOnAnswer.equals(contractorAnswer.getAnswer())) return true; }
//	 * return false; }
//	 */
//
//	/**
//	 * Get a unique ID for this answer regardless if it has been saved or not
//	 *
//	 * @return
//	 */
//	@Transient
//	public String getDivId() {
//		return "" + getQuestion().getId();
//	}
//
//	@Override
//	public int hashCode() {
//		final int PRIME = 31;
//		int result = 1;
//		result = PRIME * result + id;
//		return result;
//	}
//
//	public int compareTo(AuditData other) {
//		if (other == null) {
//			return 1;
//		}
//		return 0;
//	}
//
//    @Transient
//    public float getStraightScoreValue() {
//        float straightScoreValue = 0f;
//        if (answer != null && isMultipleChoice()) {
//            for (AuditOptionValue value : question.getOption().getValues()) {
//                if (answer.equals(value.getIdentifier())) {
//                    straightScoreValue = value.getScore();
//                    break;
//                }
//            }
//        }
//        return straightScoreValue;
//    }
//
//	@Transient
//	public List<String> getTaggitList() {
//		List<String> listOfOptionValueIl8nKeys = new ArrayList<String>();
//
//		JSONArray itemsSelected = (JSONArray) JSONValue.parse(this.getAnswer());
//		List<AuditOptionValue> optionValues = this.getQuestion().getOption().getValues();
//		for (Object answer : itemsSelected.toArray()) {
//			String uniqueCode = ((JSONObject) answer).get("id").toString();
//			for (AuditOptionValue optionValue : optionValues) {
//				if (uniqueCode.equals(optionValue.getUniqueCode())) {
//					listOfOptionValueIl8nKeys.add(optionValue.getI18nKey());
//					break;
//				}
//			}
//		}
//		return listOfOptionValueIl8nKeys;
//	}
//
//	@Override
//	public String toString() {
//		return id + " '" + answer + "'";
//	}
//
//	/**
//	 * Comparator for comparing AuditData based on Audit Questions.
//	 *
//	 * @return Comparator for comparing AuditData based on AuditQuestion
//	 */
//	public static Comparator<AuditData> getQuestionComparator() {
//		return new Comparator<AuditData>() {
//			public int compare(AuditData o1, AuditData o2) {
//				String[] o1a = o1.getQuestion().getExpandedNumber().split("\\.");
//				String[] o2a = o2.getQuestion().getExpandedNumber().split("\\.");
//				for (int i = 0; i < o1a.length && i < o2a.length; i++) {
//					if (o1a[i].equals(o2a[i]))
//						continue;
//					else
//						return Integer.valueOf(o1a[i]).compareTo(Integer.valueOf(o2a[i]));
//				}
//				return 0;
//			}
//		};
//	}
//
//	@Transient
//	public String getAnswerInDate(String format) throws ParseException {
//		SimpleDateFormat displayFormat = new SimpleDateFormat(format);
//		Date date = DateBean.parseDate(answer);
//		String dateStr = displayFormat.format(date);
//		return dateStr;
//	}
//
//	@Transient
//	public String getNumberFormatAnswer(Locale locale) {
//		NumberFormat displayFormat;
//		NumberFormat dbFormat;
//		ParsePosition pp = new ParsePosition(0);
//		if ("Number".equals(question.getQuestionType())) {
//			displayFormat = NumberFormat.getIntegerInstance(locale);
//			dbFormat = NumberFormat.getIntegerInstance(Locale.US);
//		} else {
//			displayFormat = NumberFormat.getNumberInstance(locale);
//			dbFormat = NumberFormat.getNumberInstance(Locale.US);
//		}
//		Number truthValue = dbFormat.parse(answer, pp);
//		// check for invalid number
//		if (answer.length() != pp.getIndex() || truthValue == null) {
//			return answer;
//		}
//		String displayValue = displayFormat.format(truthValue);
//		return displayValue;
//	}
//
//    public static AuditDataBuilder builder() {
//        return new AuditDataBuilder();
//    }
}
