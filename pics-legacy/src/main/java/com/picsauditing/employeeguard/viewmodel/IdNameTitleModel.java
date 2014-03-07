package com.picsauditing.employeeguard.viewmodel;

public class IdNameTitleModel extends IdNameModel {
	private final String title;

	public IdNameTitleModel(Builder builder) {
		super(builder.id, builder.name);
		this.title = builder.title;
	}

	public String getTitle() {
		return title;
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
