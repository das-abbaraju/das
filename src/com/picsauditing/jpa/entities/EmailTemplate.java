package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "email_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class EmailTemplate implements java.io.Serializable {
	private int templateID;
	private OperatorAccount operatorAccount;
	private String subject;
	private String body;
	private User createdBy;
	private Date creationDate;
	private User updatedBy;
	private Date updateDate;
	private String templateName = "";

	@Id
	@GeneratedValue(strategy = IDENTITY)
	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "subject", length = 150)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "body")
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Temporal(TemporalType.DATE)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getTemplateName() {
		return templateName;
	}

	@Column(name = "templateName", length = 50, nullable = false)
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + templateID;
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
		final EmailTemplate other = (EmailTemplate) obj;
		if (templateID != other.templateID)
			return false;
		return true;
	}

}
