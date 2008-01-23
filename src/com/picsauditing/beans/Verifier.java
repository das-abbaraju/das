package com.picsauditing.beans;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class Verifier<T, ID extends Serializable> implements Verifiable<ID> {
	
	private Class<T> persistentClass;
	private T entity;
	
	
		
	@SuppressWarnings("unchecked")
	public Verifier() {
		this.persistentClass = (Class<T>)( (ParameterizedType) getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
	}
	
	public Class<T> getPersistentClass() {
		return persistentClass;
	}
	
	
	@Override
	public abstract String edit();
	
		
	public abstract String verifyLog();

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	public String getTodaysDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("M/d/yy");
		String temp = format.format(cal.getTime());
		return temp;
	}//getTodaysDate
	
	

}
