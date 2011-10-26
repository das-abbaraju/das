package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "operatorforms")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorForm extends BaseTable implements java.io.Serializable {

	protected Account account;
	protected String formName;
	protected String file;
	protected String formType;
	private Locale locale;
	protected OperatorForm parent;
	protected List<OperatorForm> children = new ArrayList<OperatorForm>();

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(nullable = false, length = 100)
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	@Column(nullable = false, length = 100)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public OperatorForm getParent() {
		return parent;
	}

	public void setParent(OperatorForm parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	public List<OperatorForm> getChildren() {
		return children;
	}

	public void setChildren(List<OperatorForm> children) {
		this.children = children;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
