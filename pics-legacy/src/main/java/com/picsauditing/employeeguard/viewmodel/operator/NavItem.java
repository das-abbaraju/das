package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.services.status.SkillStatus;

public class NavItem {

	private final int id;
	private final String name;
	private final SkillStatus statusRollup;

	public NavItem(final Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.statusRollup = builder.statusRollup;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public SkillStatus getStatusRollup() {
		return statusRollup;
	}

	public static class Builder {

		private int id;
		private String name;
		private SkillStatus statusRollup;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder statusRollup(SkillStatus statusRollup) {
			this.statusRollup = statusRollup;
			return this;
		}

		public NavItem build() {
			return new NavItem(this);
		}
	}
}
