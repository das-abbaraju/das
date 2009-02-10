package com.picsauditing.util;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;

@SuppressWarnings("serial")
public class ReportFilterNote extends ReportFilter {
	protected String keyword;
	protected NoteCategory[] category;
	protected LowMedHigh[] priority;
	protected NoteStatus[] status;
	protected int[] userID;
	protected int[] userAccountID;
	protected int[] viewableBy;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public NoteCategory[] getCategory() {
		return category;
	}

	public void setCategory(NoteCategory[] category) {
		this.category = category;
	}

	public LowMedHigh[] getPriority() {
		return priority;
	}

	public void setPriority(LowMedHigh[] priority) {
		this.priority = priority;
	}

	public int[] getUserID() {
		return userID;
	}

	public void setUserID(int[] userID) {
		this.userID = userID;
	}

	public int[] getUserAccountID() {
		return userAccountID;
	}

	public void setUserAccountID(int[] userAccountID) {
		this.userAccountID = userAccountID;
	}

	public NoteStatus[] getStatus() {
		return status;
	}

	public void setStatus(NoteStatus[] status) {
		this.status = status;
	}

	public LowMedHigh[] getPriorityList() {
		return LowMedHigh.values();
	}

	public NoteCategory[] getCategoryList() {
		return NoteCategory.values();
	}

	public NoteStatus[] getStatusList() {
		return NoteStatus.values();
	}

	public int[] getViewableBy() {
		return viewableBy;
	}

	public void setViewableBy(int[] viewableBy) {
		this.viewableBy = viewableBy;
	}

}
