package com.picsauditing.flagcalculator.messaging;

import com.picsauditing.flagcalculator.entities.*;
import org.codehaus.jackson.annotate.*;

import java.util.Date;

@JsonAutoDetect(JsonMethod.FIELD)
public class FlagChange {
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

	@JsonProperty
	public FlagColor getFromColor() {
		return fromColor;
	}

	public void setFromColor(FlagColor fromColor) {
		this.fromColor = fromColor;
	}

	@JsonProperty
	public FlagColor getToColor() {
		return toColor;
	}

	public void setToColor(FlagColor toColor) {
		this.toColor = toColor;
	}

	@JsonProperty
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
