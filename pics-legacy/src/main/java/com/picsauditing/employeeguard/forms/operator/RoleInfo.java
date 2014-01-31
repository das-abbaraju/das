package com.picsauditing.employeeguard.forms.operator;

public class RoleInfo implements Comparable<RoleInfo> {

    private final int id;
    private final String name;

	public RoleInfo(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

	@Override
	public int compareTo(RoleInfo that) {
		return this.getName().compareTo(that.getName());
	}

	public static class Builder {
		private int id;
		private String name;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public RoleInfo build() {
			return new RoleInfo(this);
		}
	}
}
