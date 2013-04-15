package com.picsauditing.service;

import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

import java.util.Date;

public class ReportFavoriteInfo {

	private int id;
	private String name;
	private boolean favorite;
	private User createdBy;
	private int sortOrder;
	private int pinnedIndex;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getPinnedIndex() {
		return pinnedIndex;
	}

	public void setPinnedIndex(int pinnedIndex) {
		this.pinnedIndex = pinnedIndex;
	}

	public boolean isPinned() {
		return pinnedIndex != ReportUser.UNPINNED_INDEX;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
