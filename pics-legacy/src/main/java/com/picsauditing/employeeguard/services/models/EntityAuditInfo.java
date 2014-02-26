package com.picsauditing.employeeguard.services.models;


import java.util.Date;

public final class EntityAuditInfo {

	private final int userId;
	private final Date timestamp;

	public EntityAuditInfo(final Builder builder) {
		this.userId = builder.appUserId;
		this.timestamp = builder.timestamp;
	}

	/**
	 * This is the unique identifier for the user (for example, a user logged in through single-sign-on)
	 *
	 * @return
	 */
	public int getAppUserId() {
		return userId;
	}

	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	public static class Builder {

		private int appUserId;
		private Date timestamp;

		public void appUserId(int userId) {
			this.appUserId = userId;
		}

		public void timestamp(Date timestamp) {
			this.timestamp = new Date(timestamp.getTime());
		}

		public EntityAuditInfo build() {
			if (appUserId == 0) {
				throw new IllegalStateException("Cannot build an EntityAuditInfo object without userId");
			}

			return new EntityAuditInfo(this);
		}
	}
}
