package com.picsauditing.PICS.data;

public class DataEvent<T> {
	
	protected T data;	 
	
	public DataEvent(T data) {
		super();
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}	 
	 
}