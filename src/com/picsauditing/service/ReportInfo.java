package com.picsauditing.service;

import java.util.Date;

import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

public class ReportInfo {

	private int id;
	private String name;
	private String description;
	private boolean favorite;
	private boolean isPrivate;
	private int numberOfTimesFavorited;
	private boolean editable;
	private User createdBy;
	private Date creationDate;
	private Date lastViewedDate;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public int getNumberOfTimesFavorited() {
		return numberOfTimesFavorited;
	}

	public void setNumberOfTimesFavorited(int numberOfTimesFavorited) {
		this.numberOfTimesFavorited = numberOfTimesFavorited;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastViewedDate() {
		return lastViewedDate;
	}

	public void setLastViewedDate(Date lastViewedDate) {
		this.lastViewedDate = lastViewedDate;
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