package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.models.AccountModel;

public class SiteAssignmentStatisticsModel implements Comparable<SiteAssignmentStatisticsModel> {

	private final AccountModel site;
	private final int completed;
	private final int expiring;
	private final int expired;

	public SiteAssignmentStatisticsModel(final Builder builder) {
		this.site = builder.site;
		this.completed = builder.completed;
		this.expiring = builder.expiring;
		this.expired = builder.expired;
	}

	public AccountModel getSite() {
		return site;
	}

	public int getCompleted() {
		return completed;
	}

	public int getExpiring() {
		return expiring;
	}

	public int getExpired() {
		return expired;
	}

	@Override
	public int compareTo(SiteAssignmentStatisticsModel that) {
		return this.site.compareTo(that.site);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SiteAssignmentStatisticsModel that = (SiteAssignmentStatisticsModel) o;

		if (site != null ? !site.equals(that.site) : that.site != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return site != null ? site.hashCode() : 0;
	}

	public static class Builder {
		private AccountModel site;
		private int completed;
		private int expiring;
		private int expired;

		public Builder site(final AccountModel site) {
			this.site = site;
			return this;
		}

		public Builder completed(final int completed) {
			this.completed = completed;
			return this;
		}

		public Builder expiring(final int expiring) {
			this.expiring = expiring;
			return this;
		}

		public Builder expired(final int expired) {
			this.expired = expired;
			return this;
		}

		public SiteAssignmentStatisticsModel build() {
			return new SiteAssignmentStatisticsModel(this);
		}
	}
}
