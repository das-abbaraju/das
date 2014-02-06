package com.picsauditing.employeeguard.viewmodel;

public class IdNameTitleModel implements Comparable<IdNameTitleModel> {
	private final String id;
	private final String name;
	private final String title;

	public IdNameTitleModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.title = builder.title;
	}

	@Override
	public int compareTo(IdNameTitleModel that) {
		return this.name.compareToIgnoreCase(that.name);
	}

	public static class Builder {
		private String id;
		private String name;
		private String title;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public IdNameTitleModel build() {
			return new IdNameTitleModel(this);
		}
	}
}
