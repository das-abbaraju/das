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
import javax.persistence.Transient;

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
	private boolean clientSiteOnly;
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

	public boolean isClientSiteOnly() {
		return clientSiteOnly;
	}

	public void setClientSiteOnly(boolean clientSiteOnly) {
		this.clientSiteOnly = clientSiteOnly;
	}

	@Transient
	public OperatorForm getMostApplicableForm(Locale loc) {
		OperatorForm selectedForm = null;
		boolean countryMatch = getLocale().getCountry().toString().equals(loc.getCountry().toString());
		boolean primaryLanguageMatch = getLocale().getLanguage().toString().equals(loc.getLanguage().toString());

		if (countryMatch && primaryLanguageMatch) {
			selectedForm = this;
		}

		if (selectedForm == null) {
			for (OperatorForm child : getChildren()) {
				countryMatch = child.getLocale().getCountry().toString().equals(loc.getCountry().toString());
				boolean languageMatch = child.getLocale().getLanguage().toString().equals(loc.getLanguage().toString());
				if (countryMatch && languageMatch) {
					selectedForm = child;
					break;
				} else if (languageMatch && !primaryLanguageMatch && selectedForm == null) {
					selectedForm = child; // first match of just language
					break;
				}
			}
		}

		if (selectedForm == null) {
			selectedForm = this;
		}

		return selectedForm;
	}

	@Transient
	public List<OperatorForm> getAllForms() {
		ArrayList<OperatorForm> list = new ArrayList<OperatorForm>();

		OperatorForm parent = (getParent() != null) ? getParent() : this;
		list.add(parent);
		for (OperatorForm child : getChildren()) {
			list.add(child);
		}

		return list;
	}
}
