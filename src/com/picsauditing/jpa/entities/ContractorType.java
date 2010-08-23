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

	public static String[] getValues(boolean addBlank){
		String[] result;
		if(addBlank){	
			result = new String[values().length+1];
			result[result.length-1] = "*";
		}else 
			result = new String[values().length];
		for(int i=0; i<values().length; i++){
			result[i] =values()[i].name();
		}
		return result;
	}
}
