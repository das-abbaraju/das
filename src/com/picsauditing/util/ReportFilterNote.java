package com.picsauditing.util;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;

public class ReportFilterNote extends ReportFilter {
	protected String[] keyword;
	protected NoteCategory[] category;
	protected LowMedHigh[] priority;
	protected int[] userID;
	protected int[] userAccountID;

	public String[] getKeyword() {
		return keyword;
	}

	public void setKeyword(String[] keyword) {
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

	public LowMedHigh[] getPriorityList() {
		return LowMedHigh.values();
	}

	public NoteCategory[] getNoteCategoryList() {
		return NoteCategory.values();
	}

	public NoteStatus[] getNoteStatus() {
		return NoteStatus.values();
	}

}
