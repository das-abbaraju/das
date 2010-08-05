package com.picsauditing.util;

public class IndexObject {
	public String value;
	public int weight;
	
	public IndexObject(String value, int weight){
		this.value = value;
		this.weight = weight;
	}

	public IndexObject() {
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
