package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "operator_competency_employee_file")
public class OperatorCompetencyEmployeeFile extends BaseTable {
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
}
