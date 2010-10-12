package com.picsauditing.jpa.entities;

public enum ContractorType {
	Onsite("Onsite Services", "Check this box if your company provides <b>onsite</b> services such as maintenance, construction, inspection, catering, or other services performed at <b>any</b> customer location."),
	Offsite("Offsite Services", "Check this box if your company provides <b>offsite</b> services such as transportation, disposal, lab, or other services for <b>any</b> of your customers."),
	Supplier("Material Supplier", "Check this box if your company supplies products, materials, or equipment rentals for <b>any</b> of your customers.");
	
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
