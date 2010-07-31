package com.picsauditing.jpa.entities;

import java.util.List;

public interface Indexable {
	public boolean isNeedsIndexing();
	public List<String> getIndexValues(); 
	public String getIndexType();
	public int getId();
	public void setNeedsIndexing(boolean b);
}
