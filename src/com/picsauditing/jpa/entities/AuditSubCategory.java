package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfsubcategories")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class AuditSubCategory implements java.io.Serializable, Comparable<AuditSubCategory> {

	private int id;
	private String subCategory;
	private AuditCategory category;
	private int number;

	private List<AuditQuestion> questions;
	private List<AuditData> answerList;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "subCatID", nullable = false, insertable = false, updatable = false, unique = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@OneToMany(mappedBy = "subCategory")
	@OrderBy("number")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	@Transient
	public List<AuditData> getAnswerList() {
		return answerList;
	}

	public boolean hasValidQuestions() {
		for (AuditQuestion question : this.getQuestions())
			if (category.getValidDate().after(question.getEffectiveDate())
					&& category.getValidDate().before(question.getExpirationDate()))
				return true;
		return false;
	}

	/**
	 * 
	 * @param answerMap
	 *            Map of questionID, parentID, AuditData. If parentID is null,
	 *            then use 0
	 */
	public void build(ContractorAudit conAudit, AnswerMap answerMap) {
		answerList = new ArrayList<AuditData>();

		for (AuditQuestion question : getQuestions()) {
			if (question.getParentQuestion() == null)
				addChildren(conAudit, 0, question, answerMap);
		}
	}

	private void addChildren(ContractorAudit conAudit, int rowID, AuditQuestion question,
			AnswerMap answerMap) {
		if (category.getValidDate().after(question.getEffectiveDate())
				&& category.getValidDate().before(question.getExpirationDate())) {
			// This is a valid question we want to include
			//System.out.println("Adding question:" + rowID + " " + question.getQuestion());
			if (question.isAllowMultipleAnswers()) {
				for (AuditData childData : answerMap.getAnswers(question.getId())) {
					int childRowID = childData.getId();
					//System.out.println("Put answer:" + childData.getAnswer());
					answerList.add(childData);
					
					for (AuditQuestion childQuestion : question.getChildQuestions()) {
						addChildren(conAudit, childRowID, childQuestion, answerMap);
					}
				}
				// Always add a blank entry
				AuditData answer = new AuditData();
				answer.setQuestion(question);
				answer.setAudit(conAudit);
				// System.out.println("Put new entry");
				answerList.add(answer);
			} else {
				AuditData answer = answerMap.get(rowID);
				if (answer == null) {
					answer = new AuditData();
					answer.setQuestion(question);
					if (rowID > 0) {
						answer.setParentAnswer(new AuditData());
						answer.getParentAnswer().setId(rowID);
					}
					answer.setAudit(conAudit);
				}
				//System.out.println("Put single answer:" + answer.getId() + " " + answer.getAnswer());
				answerList.add(answer);
			}
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		try {
			final AuditSubCategory other = (AuditSubCategory) obj;
			if (id != other.id)
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AuditSubCategory other) {
		if (other == null) {
			return 1;
		}

		int cmp = getCategory().compareTo(other.getCategory());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}

}
