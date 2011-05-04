package com.picsauditing.util;

public final class IndexObject {
	private String value;
	private int weight;

	public IndexObject(String value, int weight) {
		this.value = value;
		this.weight = weight;
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
