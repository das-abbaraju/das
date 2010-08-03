package com.picsauditing.jpa.entities;

public enum ContractorType {
	// TODO: Update the type/descriptions
	Onsite("Onsite Services", "Contractor provides onsite services"),
	Offsite("Offsite Services", "Contractor provides offsite services"),
	Supplier("Material Supplier", "Contractor is a material supplier");
	
	private String type;
	private String description;
	
	ContractorType(String type, String description) {
		this.type = type;
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
}
