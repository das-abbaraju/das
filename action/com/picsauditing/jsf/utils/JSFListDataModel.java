package com.picsauditing.jsf.utils;

import java.util.Calendar;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public abstract class JSFListDataModel<T> implements JSFDataModelable<T>{
	
	private int maxResults = 0;
	private int firstResult = 0;
	private int resultSize = 0;
	private T selectedItem;
	private boolean ascending = true;
	private int sortBy = 0;
	private DataModel dm = new ListDataModel();
	private String persistenceCtx;
	
		
	@Override
	public DataModel getDataModel() {
		
		if(dm.getRowCount() == -1)
			dm.setWrappedData(getList());
		
		return dm;
	}
		
	
	@Override
	public DataModel getSortedModel() {
		 List<T> list = (List<T>)getDataModel().getWrappedData();		 
	     dm.setWrappedData(sortList(list));
		 return dm;
	}

	
	protected abstract List<T> getList();
	protected abstract List<T> sortList(List<T> list);

	@Override
	public int getFirstResult() {
		return firstResult;
	}

	@Override
	public int getMaxResults() {
		return maxResults;
	}

	@Override
	public int getResultSize() {
		return resultSize;
	}

	
	@Override
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;

	}

	@Override
	public void setMaxResults(int maxResults) {
		this.maxResults= maxResults;

	}
	
	@Override
	public T getSelectedItem(){
		return selectedItem;
	}
	
	@Override
	public void setSelectedItem(T selectedItem){
		this.selectedItem = selectedItem;
	}
	
	@SuppressWarnings("unchecked")
	public abstract String select( );
			
	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}
	
	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	@Override
	public String sort(int sortBy) {
		if (this.sortBy == sortBy) {
            ascending = !ascending;
        }
        else {
            this.sortBy = sortBy;
            ascending = true;
        }
        return "success";
	}

	public String getPersistenceCtx() {
		return persistenceCtx;
	}

	public void setPersistenceCtx(String persistenceCtx) {
		this.persistenceCtx = persistenceCtx;
	}
	
	public void clearModel(){
		dm = new ListDataModel();
	}
	
	public int getCurrentYear() {
		return Utilities.getCurrentYear();
	}//getCurrentYear
	
	
}
	