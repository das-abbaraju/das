package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum ContractorOperatorRelationshipType implements Translatable {
	ContractorOperator("", ""), GeneralContractor("", ""), SubContractorOperator("", "");

	private String type;
	private String description;

	private ContractorOperatorRelationshipType(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

}
