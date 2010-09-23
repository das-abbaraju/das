package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class Certificate extends BaseTable {
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

//	@Transient
//	public void updateExpirationDate() {
//		if (caos.size() > 0) {
//			// if there are caos - use the latest expiration date
//			expirationDate = null;
//			for (ContractorAuditOperator cao : caos) {
//				if (expirationDate == null
//						|| (cao.getAudit().getExpiresDate() != null && cao.getAudit().getExpiresDate().after(
//								expirationDate)))
//					expirationDate = cao.getAudit().getExpiresDate();
//			}
//		}
//
//		// 1 - there are no caos
//		// 2 - caos do not expire
//		if (expirationDate == null) {
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.MONTH, 6);
//			expirationDate = cal.getTime();
//		}
//	}
}
