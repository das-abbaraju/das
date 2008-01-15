package com.picsauditing.jsf.utils;
import javax.faces.model.DataModel;

public interface JSFDataModelable<T> {

	public DataModel getDataModel();
	public DataModel getSortedModel();
	public int getMaxResults();
	public void setMaxResults(int maxResults);
	public int getFirstResult();
	public void setFirstResult(int firstResult);
	public int getResultSize();
	public T getSelectedItem();
	public void setSelectedItem(T selectedItem);
	public String select();
	public String sort(int sortBy);
	public int getSortBy();
	public void setSortBy(int sortBy);
	public boolean isAscending();
	public void setAscending(boolean ascending);
	public void clearModel();
	public String refreshModel();
		
}
