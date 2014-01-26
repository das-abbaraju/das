package com.picsauditing.employeeguard.viewmodel.contractor;

public class SiteAssignmentStatisticsModel implements Comparable<SiteAssignmentStatisticsModel> {

	private final int siteId;
	private final String siteName;
	private final int completed;
	private final int expiring;
	private final int expired;

	public SiteAssignmentStatisticsModel(final Builder builder) {
		this.siteId = builder.siteId;
		this.siteName = builder.siteName;
		this.completed = builder.completed;
		this.expiring = builder.expiring;
		this.expired = builder.expired;
	}

	public int getSiteId() {
		return siteId;
	}

	public String getSiteName() {
		return siteName;
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
		return this.siteName.compareTo(that.siteName);
	}

	public static class Builder {
		private int siteId;
		private String siteName;
		private int completed;
		private int expiring;
		private int expired;

		public Builder siteId(int siteId) {
			this.siteId = siteId;
			return this;
		}

		public Builder siteName(String siteName) {
			this.siteName = siteName;
			return this;
		}

		public Builder completed(int completed) {
			this.completed = completed;
			return this;
		}

		public Builder expiring(int expiring) {
			this.expiring = expiring;
			return this;
		}

		public Builder expired(int expired) {
			this.expired = expired;
			return this;
		}

		public SiteAssignmentStatisticsModel build() {
			return new SiteAssignmentStatisticsModel(this);
		}
	}
}
