package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.picsauditing.gwt.shared.WebcamDTO;

@SuppressWarnings("serial")
@Entity
@Table(name = "webcam")
public class Webcam extends BaseTable implements java.io.Serializable, JSONable {
	private String make;
	private String model;
	private boolean active;
	private ContractorAccount contractor;
	private Date receivedDate;
	private Date sentDate;
	private User receivedBy;
	private User sendBy;
	private String serialNumber;
	private String carrier;
	private String shippingMethod;
	private String trackingNumber;
	private int replacementCost = 0;

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

	@Column(length = 100)
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Column(length = 50)
	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	@Column(length = 30)
	public String getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	@Column(length = 100)
	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public int getReplacementCost() {
		return replacementCost;
	}

	public void setReplacementCost(int replacementCost) {
		this.replacementCost = replacementCost;
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

		webcamDTO.setSerialNumber(serialNumber);
		webcamDTO.setCarrier(carrier);
		webcamDTO.setShippingMethod(shippingMethod);
		webcamDTO.setTrackingNumber(trackingNumber);
		webcamDTO.setReplacementCost(replacementCost);

		return webcamDTO;
	}

	@Transient
	public void fromDTO(WebcamDTO webcamDTO) {
		this.make = webcamDTO.getMake();
		this.model = webcamDTO.getModel();
		this.active = webcamDTO.isActive();
		this.receivedDate = webcamDTO.getReceivedDate();
		this.sentDate = webcamDTO.getSentDate();
		if (webcamDTO.getReceivedById() > 0)
			this.receivedBy = new User(webcamDTO.getReceivedById());
		if (webcamDTO.getSendById() > 0)
			this.sendBy = new User(webcamDTO.getSendById());
		this.serialNumber = webcamDTO.getSerialNumber();
		this.carrier = webcamDTO.getCarrier();
		this.shippingMethod = webcamDTO.getShippingMethod();
		this.trackingNumber = webcamDTO.getTrackingNumber();
		this.replacementCost = webcamDTO.getReplacementCost();
	}
	@Transient
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("make", make);
		obj.put("model", model);
		obj.put("active", active);
		if (contractor != null)
			obj.put("contractor", contractor.toJSON());
		//obj.put("receivedDate", receivedDate);
		//obj.put("sentDate", sentDate);
		if (receivedBy != null)
			obj.put("receivedBy", receivedBy.toJSON());
		if (sendBy != null)
			obj.put("sendBy", sendBy.toJSON());
		obj.put("serialNumber", serialNumber);
		obj.put("carrier", carrier);
		obj.put("shippingMethod", shippingMethod);
		obj.put("trackingNumber", trackingNumber);
		obj.put("replacementCost", replacementCost);
		
		return obj;
	}

	public void fromJSON(JSONObject o) {
		// TODO Auto-generated method stub
		
	}

}
