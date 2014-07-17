package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

public class MIdAndName implements Comparable<MIdAndName> {
	@Expose
	protected final String id;
	@Expose
	protected final String name;

	public MIdAndName(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}

	protected MIdAndName(String id, String name) {
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
	public int compareTo(MIdAndName that) {
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

		public MIdAndName build() {
			return new MIdAndName(this);
		}
	}
}
