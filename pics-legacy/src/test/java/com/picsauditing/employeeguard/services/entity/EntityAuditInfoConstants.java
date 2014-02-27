package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.models.EntityAuditInfo;

import java.util.Date;

public class EntityAuditInfoConstants {

	public static final int ENTITY_ID = 123;
	public static final int USER_ID = 22091;
	public static final int ACCOUNT_ID = 234;
	public static final Date CREATED_DATE = DateBean.today();
	public static final Date UPDATED_DATE = DateBean.addDays(DateBean.today(), 15);
	public static final String SEARCH_TERM = "Search Term";

	public static final EntityAuditInfo CREATED = new EntityAuditInfo.Builder()
			.appUserId(USER_ID)
			.timestamp(CREATED_DATE)
			.build();

	public static final EntityAuditInfo UPDATED = new EntityAuditInfo.Builder()
			.appUserId(USER_ID)
			.timestamp(UPDATED_DATE)
			.build();

}
