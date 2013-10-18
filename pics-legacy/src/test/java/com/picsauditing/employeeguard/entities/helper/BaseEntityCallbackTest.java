package com.picsauditing.employeeguard.entities.helper;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.picsauditing.employeeguard.entities.BaseEntity;

public class BaseEntityCallbackTest {

	private static final int USER_ID = 10202;
	private static final Date TIMESTAMP = new Date();

	@Test
	public void testHandleDuplicate() {
		BaseEntityCallback<BaseEntityImpl> baseEntityCallback = new BaseEntityCallback<>(
				USER_ID, TIMESTAMP);
		BaseEntityImpl baseEntityImpl = new BaseEntityImpl();

		baseEntityCallback.handleDuplicate(baseEntityImpl);

		verifyTestEmptyRemovalList(baseEntityImpl.updatedBy,
				baseEntityImpl.updatedDate, baseEntityCallback
						.getRemovedEntities().size());
	}

	@Test
	public void testHandleNewEntity() {
		BaseEntityCallback<BaseEntityImpl> baseEntityCallback = new BaseEntityCallback<>(
				USER_ID, TIMESTAMP);
		BaseEntityImpl baseEntityImpl = new BaseEntityImpl();

		baseEntityCallback.handleNewEntity(baseEntityImpl);

		verifyTestEmptyRemovalList(baseEntityImpl.createdBy,
				baseEntityImpl.createdDate, baseEntityCallback
						.getRemovedEntities().size());
	}

	private void verifyTestEmptyRemovalList(int id, Date timestamp,
			int removedEntityListSize) {
		assertEquals(USER_ID, id);
		assertEquals(TIMESTAMP, timestamp);
		assertEquals(0, removedEntityListSize);
	}

	@Test
	public void testHandleRemoval() {
		BaseEntityCallback<BaseEntityImpl> baseEntityCallback = new BaseEntityCallback<>(
				USER_ID, TIMESTAMP);
		BaseEntityImpl baseEntityImpl = new BaseEntityImpl();

		baseEntityCallback.handleRemoval(baseEntityImpl);

		assertEquals(USER_ID, baseEntityImpl.deletedBy);
		assertEquals(TIMESTAMP, baseEntityImpl.deletedDate);

		verifyTestOneRemovedEntity(baseEntityImpl.deletedBy,
				baseEntityImpl.deletedDate, baseEntityCallback
						.getRemovedEntities().size());
	}

	private void verifyTestOneRemovedEntity(int id, Date timestamp,
			int removedEntityListSize) {
		assertEquals(USER_ID, id);
		assertEquals(TIMESTAMP, timestamp);
		assertEquals(1, removedEntityListSize);
	}

	private class BaseEntityImpl implements BaseEntity {

		private static final long serialVersionUID = -4136630905628191191L;

		private int id;
		private int createdBy;
		private Date createdDate;
		private int updatedBy;
		private Date updatedDate;
		private int deletedBy;
		private Date deletedDate;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(int createdBy) {
			this.createdBy = createdBy;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public int getUpdatedBy() {
			return updatedBy;
		}

		public void setUpdatedBy(int updatedBy) {
			this.updatedBy = updatedBy;
		}

		public Date getUpdatedDate() {
			return updatedDate;
		}

		public void setUpdatedDate(Date updatedDate) {
			this.updatedDate = updatedDate;
		}

		public int getDeletedBy() {
			return deletedBy;
		}

		public void setDeletedBy(int deletedBy) {
			this.deletedBy = deletedBy;
		}

		public Date getDeletedDate() {
			return deletedDate;
		}

		public void setDeletedDate(Date deletedDate) {
			this.deletedDate = deletedDate;
		}
	}

}
