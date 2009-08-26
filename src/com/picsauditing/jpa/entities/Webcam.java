package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.gwt.shared.WebcamDTO;

@SuppressWarnings("serial")
@Entity
@Table(name = "webcam")
public class Webcam extends BaseTable implements java.io.Serializable {
	private String make;
	private String model;
	private boolean active;
	private ContractorAccount contractor;
	private Date receivedDate;
	private Date sentDate;
	private User receivedBy;
	private User sendBy;

	@Column(length = 100)
	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@Column(length = 100)
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@ManyToOne
	@JoinColumn(name = "receivedBy")
	public User getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}

	@ManyToOne
	@JoinColumn(name = "sendBy")
	public User getSendBy() {
		return sendBy;
	}

	public void setSendBy(User sendBy) {
		this.sendBy = sendBy;
	}

	@Transient
	public WebcamDTO toDTO() {
		WebcamDTO webcamDTO = new WebcamDTO();
		webcamDTO.setId(id);
		webcamDTO.setMake(make);
		webcamDTO.setModel(model);
		webcamDTO.setActive(active);

		if (contractor == null)
			webcamDTO.setConID(0);
		else {
			webcamDTO.setConID(contractor.getId());
			webcamDTO.setContractorName(contractor.getName());
		}

		webcamDTO.setReceivedDate(receivedDate);
		webcamDTO.setSentDate(sentDate);

		if (receivedBy == null)
			webcamDTO.setReceivedById(0);
		else
			webcamDTO.setReceivedById(receivedBy.getId());

		if (sendBy == null)
			webcamDTO.setSendById(0);
		else
			webcamDTO.setSendById(sendBy.getId());

		return webcamDTO;
	}

	@Transient
	public void fromDTO(WebcamDTO webcamDTO) {
		this.id = webcamDTO.getId();
		this.make = webcamDTO.getMake();
		this.model = webcamDTO.getModel();
		this.active = webcamDTO.isActive();
		this.contractor = new ContractorAccount(webcamDTO.getConID());
		this.receivedDate = webcamDTO.getReceivedDate();
		this.sentDate = webcamDTO.getSentDate();
		this.receivedBy = new User(webcamDTO.getReceivedById());
		this.sendBy = new User(webcamDTO.getSendById());
	}

}
