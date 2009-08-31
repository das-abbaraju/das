package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class EmailTemplate extends BaseTable implements java.io.Serializable {
	private int accountID;
	private String templateName = "";
	private String subject;
	private String body;
	private ListType listType;
	private boolean allowsVelocity = false;
	private boolean html = false;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "templateID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(nullable = false)
	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	@Column(length = 150)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTemplateName() {
		return templateName;
	}

	@Column(length = 50, nullable = false)
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Enumerated(EnumType.STRING)
	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

	public boolean isAllowsVelocity() {
		return allowsVelocity;
	}

	public void setAllowsVelocity(boolean allowsVelocity) {
		this.allowsVelocity = allowsVelocity;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

}
