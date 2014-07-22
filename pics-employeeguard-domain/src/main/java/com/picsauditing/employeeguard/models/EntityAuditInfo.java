package com.picsauditing.employeeguard.models;


import java.util.Date;

public final class EntityAuditInfo {

	private final int appUserId;
	private final Date timestamp;

	public EntityAuditInfo(final Builder builder) {
		this.appUserId = builder.appUserId;
		this.timestamp = builder.timestamp;
	}

	public static EntityAuditInfo newEntityAuditInfo(int appUserId){
		return new EntityAuditInfo.Builder().appUserId(appUserId).timestamp(new Date()).build();
	}

	/**
	 * This is the unique identifier for the user (for example, a user logged in through single-sign-on)
	 *
	 * @return
	 */
	public int getAppUserId() {
		return appUserId;
	}

	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	public static class Builder {

		private int appUserId;
		private Date timestamp;

		public Builder appUserId(int userId) {
			this.appUserId = userId;
			return this;
		}

		public Builder timestamp(Date timestamp) {
			this.timestamp = new Date(timestamp.getTime());
			return this;
		}

		public EntityAuditInfo build() {
			if (appUserId == 0 || timestamp == null) {
				throw new IllegalStateException("Cannot build an EntityAuditInfo object " +
						"without appUserId or timestamp");
			}

			return new EntityAuditInfo(this);
		}
	}
}
