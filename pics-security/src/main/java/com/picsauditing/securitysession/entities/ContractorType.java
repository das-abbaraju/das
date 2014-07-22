package com.picsauditing.securitysession.entities;

public enum ContractorType /*implements Translatable*/ {
	Onsite/*("Onsite Services")*/,
	Offsite/*("Offsite Services")*/,
	Supplier/*("Material Supplier")*/,
	Transportation/*("Transportation Services")*/;
//
//	private String type;
//
//	ContractorType(String type) {
//		this.type = type;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public static String[] getValues(boolean addBlank){
//		String[] result;
//		if(addBlank){
//			result = new String[values().length+1];
//			result[result.length-1] = "*";
//		}else
//			result = new String[values().length];
//		for(int i=0; i<values().length; i++){
//			result[i] =values()[i].name();
//		}
//		return result;
//	}
//
//	@Transient
//	@Override
//	public String getI18nKey() {
//		return this.getClass().getSimpleName() + "." + this.name();
//	}
//
//	@Transient
//	@Override
//	public String getI18nKey(String property) {
//		return getI18nKey() + "." + property;
//	}
}
