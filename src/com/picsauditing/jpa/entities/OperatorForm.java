package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "operatorforms")
public class OperatorForm extends BaseTable implements java.io.Serializable {

	protected OperatorAccount operatorAccount;
	protected String formName;
	protected String file;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "formName", nullable = false, length = 100)
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	@Column(name = "file", nullable = false, length = 100)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
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
		if (getClass() != obj.getClass())
			return false;
		final AuditCatOperator other = (AuditCatOperator) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
