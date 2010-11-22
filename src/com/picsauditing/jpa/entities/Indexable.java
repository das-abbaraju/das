package com.picsauditing.jpa.entities;

import java.util.List;

import com.picsauditing.util.IndexObject;

public interface Indexable {
	public boolean isNeedsIndexing();
	public List<IndexObject> getIndexValues(); 
	public String getIndexType();
	public String getReturnType();
	public int getId();
	public void setNeedsIndexing(boolean b);
	public String getViewLink();
	public String getSearchText();
}
