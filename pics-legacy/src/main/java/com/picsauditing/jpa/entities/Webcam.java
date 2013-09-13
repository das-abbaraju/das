package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "webcam")
public class Webcam extends BaseTable implements JSONable {
	private String make = "Logitech";
	private String model;
	private boolean active = true;
	private ContractorAccount contractor;
	private Date receivedDate;
	private Date sentDate;
	private User receivedBy;
	private User sendBy;
	private String serialNumber;
	private String carrier;
	private String shippingMethod;
	private String trackingNumber;
	private String trackingNumberIncoming;
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

	@OneToOne
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
	
	@Column(length = 100)
	public String getTrackingNumberIncoming() {
		return trackingNumberIncoming;
	}

	public void setTrackingNumberIncoming(String trackingNumberIncoming) {
		this.trackingNumberIncoming = trackingNumberIncoming;
	}

	public int getReplacementCost() {
		return replacementCost;
	}

	public void setReplacementCost(int replacementCost) {
		this.replacementCost = replacementCost;
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
		// obj.put("receivedDate", receivedDate);
		// obj.put("sentDate", sentDate);
		if (receivedBy != null)
			obj.put("receivedBy", receivedBy.toJSON());
		if (sendBy != null)
			obj.put("sendBy", sendBy.toJSON());
		obj.put("serialNumber", serialNumber);
		obj.put("carrier", carrier);
		obj.put("shippingMethod", shippingMethod);
		obj.put("trackingNumber", trackingNumber);
		obj.put("trackingNumberIncoming", trackingNumberIncoming);
		obj.put("replacementCost", replacementCost);

		return obj;
	}

	public void fromJSON(JSONObject o) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append(id);
		if (!Strings.isEmpty(make))
			s.append(" - ").append(make);
		if (!Strings.isEmpty(model))
			s.append(" - ").append(model);

		return s.toString();
	}

}
