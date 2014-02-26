package com.picsauditing.employeeguard.services.models;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Date;

public final class EntityAuditInfo {

	private final int userId;
	private final Date timestamp;

	public EntityAuditInfo(final Builder builder) {
		this.userId = builder.userId;
		this.timestamp = builder.timestamp;
	}

	public int getUserId() {
		return userId;
	}

	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	public static class Builder {

		private int userId;
		private Date timestamp;

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = new Date(timestamp.getTime());
		}

		public EntityAuditInfo build() {
			if (userId == 0) {
				throw new InvalidStateException("Cannot build an EntityAuditInfo object without userId");
			}

			return new EntityAuditInfo(this);
		}
	}
}
