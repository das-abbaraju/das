package com.picsauditing.jpa.entities;

import org.apache.commons.io.FileUtils;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
@Table(name = "operator_competency_employee_file")
public class OperatorCompetencyEmployeeFile extends BaseTable implements Comparable<OperatorCompetencyEmployeeFile> {
	private OperatorCompetency competency;
	private Employee employee;
	private String fileName;
	private String fileType;
	private byte[] fileContent;
	private Date expiration;

	@ManyToOne
	@JoinColumn(name = "competencyID")
	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	@ManyToOne
	@JoinColumn(name = "employeeID")
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@javax.persistence.Column(name = "fileImage")
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Transient
	public boolean isExpired() {
		return expiration.before(new Date());
	}

	@Override
	public int compareTo(OperatorCompetencyEmployeeFile o) {
		if (this.expiration != null && o.getExpiration() != null && !this.expiration.equals(o.getExpiration())) {
			// Sort dates descending
			return this.expiration.compareTo(o.getExpiration()) * -1;
		}

		if (this.competency != null && o.getCompetency() != null) {
			return this.competency.getLabel().compareTo(o.getCompetency().getLabel());
		}

		return 0;
	}
}
