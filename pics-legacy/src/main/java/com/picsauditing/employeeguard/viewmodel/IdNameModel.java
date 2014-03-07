package com.picsauditing.employeeguard.viewmodel;

public class IdNameModel implements Comparable<IdNameModel> {
	protected final String id;
	protected final String name;

	public IdNameModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}

	protected IdNameModel(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(IdNameModel that) {
		return this.name.compareToIgnoreCase(that.name);
	}

	public static class Builder {
		private String id;
		private String name;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public IdNameModel build() {
			return new IdNameModel(this);
		}
	}
}
