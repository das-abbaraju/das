package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "certificate")
public class Certificate extends BaseTable implements Comparable<Certificate> {
	private ContractorAccount contractor;
	private String fileType;
	private String description;
	private String fileHash;
	private Date expirationDate;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false, updatable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@Column(nullable = false)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Column(length = 100)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Transient
	public boolean isExpired() {
		return isExpired(new Date());
	}

	@Transient
	public boolean isExpired(Date date) {
		if (expirationDate == null)
			return false;
		return expirationDate.before(date);
	}

	@Override
	@Transient
	public int compareTo(Certificate o) {
		if (description.equals(o.description)) {
			return fileType.compareTo(o.fileType);
		}

		return description.compareTo(o.description);
	}
}