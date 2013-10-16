package com.picsauditing.employeeguard.entities;

import java.io.Serializable;
import java.util.Date;

public interface BaseEntity extends Serializable {
	int getId();
	void setId(int id);

	int getCreatedBy();
	void setCreatedBy(int createdById);

	Date getCreatedDate();
	void setCreatedDate(Date createdDate);

	int getUpdatedBy();
	void setUpdatedBy(int updatedById);

	Date getUpdatedDate();
	void setUpdatedDate(Date updatedDate);

	int getDeletedBy();
	void setDeletedBy(int deletedById);

	Date getDeletedDate();
	void setDeletedDate(Date deletedDate);
}
