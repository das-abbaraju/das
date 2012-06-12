package com.picsauditing.jpa.entities;

import java.util.Date;

import org.json.simple.JSONObject;

public class FlagChange implements JSONable, Message {
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagColor fromColor;
	private FlagColor toColor;
	private Date timestamp;
	private String details;
	
	public ContractorAccount getContractor() {
		return contractor;
	}
	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
	public OperatorAccount getOperator() {
		return operator;
	}
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
	public FlagColor getFromColor() {
		return fromColor;
	}
	public void setFromColor(FlagColor fromColor) {
		this.fromColor = fromColor;
	}
	public FlagColor getToColor() {
		return toColor;
	}
	public void setToColor(FlagColor toColor) {
		this.toColor = toColor;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	public String getDetails() {
		return details;
	}
	
	@Override
	public void fromJSON(JSONObject o) {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("contractor", contractor.toJSON(full));
		json.put("operator", operator.toJSON(full));
		json.put("fromColor", fromColor.toString());
		json.put("toColor", toColor.toString());
		json.put("timestamp", timestamp.toString());
		json.put("details", details);
		
		return json;
	}
	
	public JSONObject toJSON() {
		return toJSON(false);
	}
	
	@Override
	public String getMessage() {
		return toJSON().toJSONString();
	}
}
